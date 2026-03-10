package com.bookstore.management.sales.service;

import com.bookstore.management.book.model.Book;
import com.bookstore.management.book.repository.BookRepository;
import com.bookstore.management.customer.model.Customer;
import com.bookstore.management.customer.repository.CustomerRepository;
import com.bookstore.management.inventory.dto.UpdateStockDTO;
import com.bookstore.management.inventory.model.MovementType;
import com.bookstore.management.inventory.service.InventoryService;
import com.bookstore.management.sales.dto.SaleRequestDTO;
import com.bookstore.management.sales.dto.SaleResponseDTO;
import com.bookstore.management.sales.mapper.SaleMapper;
import com.bookstore.management.sales.model.Sale;
import com.bookstore.management.sales.model.SalesDetail;
import com.bookstore.management.sales.model.SalesStatus;
import com.bookstore.management.sales.repository.SaleRepository;
import com.bookstore.management.shared.exception.custom.InsufficientReservedStockException;
import com.bookstore.management.shared.exception.custom.InvalidSalesStatusException;
import com.bookstore.management.shared.exception.custom.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Transactional(readOnly=true)
@RequiredArgsConstructor
@Service
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final CustomerRepository customerRepository;
    private final BookRepository bookRepository;
    private final InventoryService inventoryService;
    private final SaleMapper saleMapper;

    @Override
    public SaleResponseDTO findById(Long id) {
        Sale sale = findByIdOrThrow(id);
        return saleMapper.toResponseDto(sale);
    }

    private Sale findByIdOrThrow(Long saleId){
        return saleRepository.findById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale","Id",saleId));
    }

    @Override
    public List<SaleResponseDTO> findAll() {
        List<Sale> saleList = saleRepository.findAll();
        return saleMapper.toResponseDtoList(saleList);
    }

    @Override
    public List<SaleResponseDTO> findByCustomerId(Long customerId) {

        customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer","Id",customerId));

        List<Sale> sales = saleRepository.findByCustomerId(customerId);
        return saleMapper.toResponseDtoList(sales);
    }

    @Override
    public List<SaleResponseDTO> findByStatus(SalesStatus status) {
        List<Sale> sales = saleRepository.findByStatus(status);
        return saleMapper.toResponseDtoList(sales);
    }

    @Override
    public List<SaleResponseDTO> findByDateRange(LocalDate start, LocalDate end) {
        List<Sale> sales = saleRepository.findByDateRange(start,end);
        return saleMapper.toResponseDtoList(sales);
    }

    @Transactional
    @Override
    public SaleResponseDTO createSale(SaleRequestDTO saleRequestDTO) {

        log.info("Creating new sale request");

        Customer customer = null;
        if (saleRequestDTO.customerId() != null) {

            customer = customerRepository
                    .findById(saleRequestDTO.customerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer","Id",saleRequestDTO.customerId()));
        }

        Sale sale = Sale.builder()
                .customer(customer)
                .status(SalesStatus.PENDING)
                .paymentMethod(saleRequestDTO.paymentMethod())
                .observation(saleRequestDTO.observation())
                .expiredAt(LocalDateTime.now().plusMinutes(5))
                .build();

        List<SalesDetail> details = saleRequestDTO.items().stream()
                .map(detailDto ->{

                    Book book = bookRepository.findById(detailDto.bookId())
                            .orElseThrow(() -> new ResourceNotFoundException("Book","Id",detailDto.bookId()));

                    SalesDetail salesDetail = SalesDetail.builder()
                            .book(book)
                            .quantity(detailDto.quantity())
                            .unitPrice(book.getPrice())
                            .discountPercent(book.getDiscountPercent())
                            .sale(sale)
                            .build();

                    inventoryService.reserveStock(book.getId(), detailDto.quantity());

                    salesDetail.setLineTotal(calculateLineTotal(salesDetail));
                    return salesDetail;
                })
                .toList();

        sale.setDetails(details);
        calculateTotal(sale);

        Sale saleSaved = saleRepository.save(sale);

        return saleMapper.toResponseDto(saleSaved);
    }

    @Transactional
    @Override
    public SaleResponseDTO completeSale(Long saleId) {
        log.info("Completing sale with id: {}", saleId);

        Sale sale = findByIdOrThrow(saleId);

        verifyStatusIsPending(sale.getStatus());

        for (SalesDetail salesDetail : sale.getDetails()) {
            UpdateStockDTO updateStockDTO = new UpdateStockDTO(
                    salesDetail.getQuantity(),
                    MovementType.EXIT
            );
            inventoryService.registerSale(updateStockDTO, salesDetail.getBook().getId());
        }
        sale.setStatus(SalesStatus.COMPLETED);
        Sale saleSaved = saleRepository.save(sale);

        log.info("Sale {} completed successfully with {} items", saleSaved.getId(), saleSaved.getDetails().size());
        return saleMapper.toResponseDto(saleSaved);
    }

    @Transactional
    @Override
    public SaleResponseDTO cancelSale(Long saleId) {

        log.info("Canceling sale with id: {}", saleId);

        Sale sale = findByIdOrThrow(saleId);
        verifyStatusIsPending(sale.getStatus());

        sale.getDetails().forEach(
                detail -> inventoryService
                        .releaseReservation( detail.getBook().getId(), detail.getQuantity()));

        sale.setStatus(SalesStatus.CANCELLED);
        Sale saleSaved = saleRepository.save(sale);

        log.info("sale cancelled successfully with {} id", saleSaved.getId());
        return saleMapper.toResponseDto(saleSaved);
    }

    @Scheduled(fixedRate = 60000)
    public void expirePendingSales() {

        LocalDateTime now = LocalDateTime.now();
        List<Sale> expired = saleRepository
                .findByStatusAndExpiredAtBefore(SalesStatus.PENDING, now);
        expired.forEach(sale -> {
            try {
                cancelSale(sale.getId());
                log.info("Successfully cancelled expired sale with id: {}", sale.getId());
            }catch (InsufficientReservedStockException | InvalidSalesStatusException | ResourceNotFoundException e) {
                log.error("Failed to cancel expired sale with id: {}. Reason: {}", sale.getId(), e.getMessage());
            }
        });

    }

    private void verifyStatusIsPending(SalesStatus status) {
        if (status != SalesStatus.PENDING) {
            throw new InvalidSalesStatusException(
                    "Cannot process sale with status " + status
            );
        }
    }

    private void calculateTotal(Sale sale) {
        BigDecimal total = sale.getDetails().stream()
                .map(SalesDetail::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        sale.setTotal(total);
    }

    private BigDecimal calculateLineTotal(SalesDetail detail) {

        BigDecimal subTotal = detail.getUnitPrice()
                .multiply(BigDecimal.valueOf(detail.getQuantity()));

        BigDecimal discount = subTotal
                .multiply(detail.getDiscountPercent())
                .divide(BigDecimal.valueOf(100),2, RoundingMode.HALF_UP);

        return subTotal.subtract(discount);
    }
}
