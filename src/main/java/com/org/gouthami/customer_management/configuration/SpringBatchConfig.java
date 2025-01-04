package com.org.gouthami.customer_management.configuration;

import com.org.gouthami.customer_management.batch.CustomerNameProcessor;
import com.org.gouthami.customer_management.batch.CustomerWriter;
import com.org.gouthami.customer_management.batch.RestCustomerReader;
import com.org.gouthami.customer_management.entity.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
@Slf4j
public class SpringBatchConfig {

  @Bean
  public Job customerJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new JobBuilder("customerJob", jobRepository)
      .incrementer(new RunIdIncrementer())
      .start(customerStep(jobRepository, transactionManager))
      .build();
  }

  @Bean
  public Step customerStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new StepBuilder("customerStep", jobRepository)
      .<Customer, Customer>chunk(10, transactionManager)
      .reader(reader())
//    .reader(customerReader())
      .processor(processor())
      .writer(writer())
      .build();
  }

  //To insert the data directly using the script and reading it via job
  @Bean
  @StepScope
  public ItemReader<Customer> customerReader() {
    return new RestCustomerReader("http://localhost:8080/customers", new RestTemplate());
  }

  @Bean
  @StepScope
  public FlatFileItemReader<Customer> reader() {
    FlatFileItemReader<Customer> reader = new FlatFileItemReader<>();
    reader.setResource(new ClassPathResource("customers.csv"));
    reader.setName("csvReader");
    reader.setLinesToSkip(1);
    reader.setLineMapper(lineMapper());
    reader.setSaveState(false);

    log.info("Reader is set up!");
    return reader;
  }

  @Bean
  public DefaultLineMapper<Customer> lineMapper() {
    DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();
    DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
    delimitedLineTokenizer.setDelimiter(",");
    delimitedLineTokenizer.setNames("name", "email", "age", "gender", "address", "phoneNumber");
    lineMapper.setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
      setTargetType(Customer.class);
    }});
    lineMapper.setLineTokenizer(delimitedLineTokenizer);
    return lineMapper;
  }

  @Bean
  @StepScope
  public ItemProcessor<Customer, Customer> processor() {
    CompositeItemProcessor<Customer, Customer> processor = new CompositeItemProcessor<>();
    processor.setDelegates(List.of(new CustomerNameProcessor()));
    return processor;
  }

  @Bean
  @StepScope
  public ItemWriter<Customer> writer() {
    return new CustomerWriter();
  }
}
