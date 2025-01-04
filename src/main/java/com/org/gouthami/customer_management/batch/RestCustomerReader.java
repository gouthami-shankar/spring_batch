package com.org.gouthami.customer_management.batch;

import com.org.gouthami.customer_management.entity.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class RestCustomerReader implements ItemReader<Customer> {

  private final String url;
  private final RestTemplate restTemplate;
  private int nextCustomer;
  private List<Customer> customers;

  public RestCustomerReader(String url, RestTemplate restTemplate) {
    this.url = url;
    this.restTemplate = restTemplate;

  }

  @Override
  public Customer read() {
    if (this.customers == null) {
      customers = getCustomers();
    }
    Customer customer = null;
    if (nextCustomer < customers.size()) {
      customer = customers.get(nextCustomer);
      nextCustomer++;
    } else {
      nextCustomer = 0;
      customers = null;
    }
    return customer;
  }

  private List<Customer> getCustomers() {
    ResponseEntity<Customer[]> response = restTemplate.getForEntity(url, Customer[].class);
    Customer[] customers = response.getBody();
    return Arrays.asList(customers);
  }
}
