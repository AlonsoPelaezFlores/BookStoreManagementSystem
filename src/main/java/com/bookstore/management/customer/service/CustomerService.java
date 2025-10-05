package com.bookstore.management.customer.service;

import com.bookstore.management.customer.dto.CustomerDto;
import com.bookstore.management.customer.mapper.CustomerMapper;
import com.bookstore.management.customer.model.Customer;
import com.bookstore.management.customer.repository.CustomerRepository;
import com.bookstore.management.shared.exception.custom.CustomerNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }
    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(()-> new CustomerNotFoundException("Customer","Id",id));
    }
    @Transactional
    public Customer create(CustomerDto customerDto) {

        Customer customer = customerMapper.toEntity(customerDto);

        log.info("Creating customer with id: {}", customer.getId());

        return customerRepository.save(customer);
    }
    @Transactional
    public Customer update(CustomerDto customerDto, Long id) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(()-> new CustomerNotFoundException("Customer","Id",id));

        customerMapper.updateEntityFromDto(customerDto, existingCustomer);

        log.info("Updating customer with id: {}", existingCustomer.getId());

        return customerRepository.save(existingCustomer);
    }
    @Transactional
    public void deleteById(Long id) {
        if(!customerRepository.existsById(id)) {
           throw new CustomerNotFoundException("Customer","Id",id);
        }
        log.info("Deleting customer with id: {}", id);
        customerRepository.deleteById(id);
    }





}
