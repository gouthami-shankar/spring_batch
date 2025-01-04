package com.org.gouthami.customer_management.batch;

import com.org.gouthami.customer_management.entity.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class CustomerNameProcessor implements ItemProcessor<Customer, Customer> {

  @Override
  public Customer process(Customer customer) {
    log.info("Processing customer: {} ", customer);
    customer.setName(customer.getName().toUpperCase());
    log.info("Processing customer name to UpperCase: {} ", customer.getName());
    return customer;
  }
}
