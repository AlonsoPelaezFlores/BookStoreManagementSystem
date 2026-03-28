package com.bookstore.management.customer.service;

import com.bookstore.management.customer.dto.CustomerCreateDTO;
import com.bookstore.management.customer.dto.CustomerSummaryDTO;
import com.bookstore.management.customer.mapper.CustomerMapper;
import com.bookstore.management.customer.model.Customer;
import com.bookstore.management.customer.repository.CustomerRepository;
import com.bookstore.management.shared.exception.custom.DuplicateEntityException;
import com.bookstore.management.shared.exception.custom.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public List<CustomerSummaryDTO> findAll() {
        return customerMapper.toSummaryDTOList(customerRepository.findAll());
    }
    public CustomerSummaryDTO findById(Long id) {
        return customerMapper.toSummaryDTO(findByIdOrThrow(id));
    }
    private Customer findByIdOrThrow(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Customer","Id",id));
    }
    @Transactional
    public CustomerSummaryDTO create(CustomerCreateDTO customerDto) {
        Customer customer = customerRepository.findByEmail(customerDto.getEmail());
        if (customer != null) {
            throw new DuplicateEntityException("Customer already exists with this email");
        }
        customer = customerMapper.toEntity(customerDto);

        return customerMapper.toSummaryDTO(customerRepository.save(customer));
    }
    @Transactional
    public CustomerSummaryDTO update(CustomerCreateDTO customerDto, Long id) {
        Customer existingCustomer = findByIdOrThrow(id);

        customerMapper.updateEntityFromDto(customerDto, existingCustomer);

        return customerMapper.toSummaryDTO(customerRepository.save(existingCustomer));
    }
    @Transactional
    public void deleteById(Long id) {
        findByIdOrThrow(id);
        customerRepository.deleteById(id);
    }
}
