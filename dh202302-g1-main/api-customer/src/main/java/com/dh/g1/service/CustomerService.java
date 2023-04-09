package com.dh.g1.service;

import com.dh.g1.exceptions.CustomerException;
import com.dh.g1.exceptions.MessageError;
import com.dh.g1.exceptions.ResourceNotFoundException;
import com.dh.g1.model.Customer;
import com.dh.g1.model.CustomerDto;
import com.dh.g1.repository.ICustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class CustomerService implements ICustomerService{


    private final ICustomerRepository customerRepository;
    private final ObjectMapper mapper;

    public CustomerService(ICustomerRepository customerRepository, ObjectMapper mapper) {
        this.customerRepository = customerRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public void createCustomer(CustomerDto customerDto) {
        customerRepository.save(mapper.convertValue(customerDto,Customer.class));
    }

    @Override
    @Transactional
    public Optional<CustomerDto> updateCustomer(Long id, CustomerDto customerDto) {

        return customerRepository.findById(id).map(customer -> {
            customer.setNombre(customerDto.getNombre() != null ? customerDto.getNombre() : customer.getNombre());
            customer.setApellido(customerDto.getApellido() != null ? customerDto.getApellido() : customer.getApellido());
            customer.setGenero(customerDto.getGenero() != null ? customerDto.getGenero() : customer.getGenero());
            customer.setFechaNacimiento(customerDto.getFechaNacimiento() != null ? customerDto.getFechaNacimiento() : customer.getFechaNacimiento());
            return mapper.convertValue(customerRepository.save(customer), CustomerDto.class);
        });
    }

    @Override
    @Transactional
    public Set<CustomerDto> readCustomers() {
        List<Customer> customers = customerRepository.findAll();
        Set<CustomerDto> customerDtos = new HashSet<>();

        for (Customer customer:customers) {
            customerDtos.add(mapper.convertValue(customer, CustomerDto.class));
        }
        return customerDtos;
    }

    @Transactional
    public Set<CustomerDto> readCustomerByTypeAndNumber(String tipoDocumento,String nroDocumento) {
        List<Customer> customers = customerRepository.findByTypeAndNumber(tipoDocumento,nroDocumento);
        Set<CustomerDto> customerDtos = new HashSet<>();

        for (Customer customer:customers) {
            customerDtos.add(mapper.convertValue(customer, CustomerDto.class));
        }
        return customerDtos;
    }

    @Override
    @Transactional
    public Optional<CustomerDto> readCustomer(Long id) {
        Optional<Customer> customer = customerRepository.findById(id);

        return Optional.ofNullable(mapper.convertValue(customer, CustomerDto.class));
    }

    @Override
    public Optional<CustomerDto> getCustomer(String tipoDocumento, String nroDocumento) throws CustomerException {
        Optional<Customer> customer = Optional.ofNullable(customerRepository.findBytipoDocumentoAndnroDocumento(tipoDocumento, nroDocumento).orElseThrow(() -> new CustomerException(MessageError.CUSTOMER_NOT_FOUND)));
        return Optional.ofNullable(mapper.convertValue(customer, CustomerDto.class));
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        customerRepository.softDelete(id);
    }
}
