package com.bookstore.management.customers.service;

import com.bookstore.management.customers.dto.CustomerDto;
import com.bookstore.management.customers.model.Customer;
import com.bookstore.management.customers.repository.CustomerRepository;
import com.bookstore.management.shared.exception.custom.CustomerNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }
    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(()-> new CustomerNotFoundException("Customer","Id",id));
    }
    @Transactional
    public Customer create(CustomerDto customerDto) {

        Customer customer = Customer.builder()
                .name(customerDto.getName())
                .surname(customerDto.getSurname())
                .email(customerDto.getEmail())
                .birthDate(customerDto.getBirthDate())
                .build();
        log.info("Creating customer with id: {}", customer.getId());

        return customerRepository.save(customer);
    }
    @Transactional
    public Customer update(CustomerDto customerDto, Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(()-> new CustomerNotFoundException("Customer","Id",id));

        customer.setName(customerDto.getName());
        customer.setSurname(customerDto.getSurname());
        customer.setEmail(customerDto.getEmail());
        customer.setBirthDate(customerDto.getBirthDate());

        log.info("Updating customer with id: {}", customer.getId());

        return customerRepository.save(customer);
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
