package com.example.application.service;

import com.example.application.domain.model.Customer;
import com.example.application.domain.model.CustomerRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class CustomerService {

    private final CustomerRepository customerRepository;

    CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Optional<Customer> get(Long customerId) {
        return customerRepository.findById(customerId);
    }

    public List<Customer> list(Pageable pageable) {
        return customerRepository.findAll(pageable).toList();
    }

    public Customer save(Customer customer) {
        return customerRepository.saveAndFlush(customer);
    }

    public void delete(Customer customer) {
        customerRepository.delete(customer);
    }
}
