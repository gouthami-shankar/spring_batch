package com.org.gouthami.customer_management.controller;

import com.org.gouthami.customer_management.entity.Customer;
import com.org.gouthami.customer_management.repository.CustomerRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

  //To trigger job using an endpoint, need to set the enabled flag as false
  @Autowired
  private JobLauncher jobLauncher;
  @Autowired
  private Job job;

  @GetMapping("/triggerJob")
  public ResponseEntity<String> importCsvDataToDB() {
    try {
      JobParameters jobParameters = new JobParametersBuilder()
        .addLong("time", System.currentTimeMillis())
        .toJobParameters();

      jobLauncher.run(job, jobParameters);

      return ResponseEntity.ok("Job executed successfully!");
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(500).body("Job execution failed: " + e.getMessage());
    }
  }


  //To check the data when job is triggered
  @Autowired
  private CustomerRepository customerRepository;

  @GetMapping
  public ResponseEntity<List<Customer>> getCustomers() {
    List<Customer> customers = customerRepository.findAll();
    return new ResponseEntity<>(customers, HttpStatus.OK);
  }

}
