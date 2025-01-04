package com.org.gouthami.customer_management.batch;

import com.org.gouthami.customer_management.entity.Customer;
import com.org.gouthami.customer_management.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
@Slf4j
public class CustomerWriter implements ItemWriter<Customer> {

  @Autowired
  private CustomerRepository customerRepository;

  @Override
  public void write(Chunk<? extends Customer> chunk) {
    log.info("Size of chuck: {}" ,chunk.getItems().size());
    customerRepository.saveAll(chunk.getItems());
  }
}
