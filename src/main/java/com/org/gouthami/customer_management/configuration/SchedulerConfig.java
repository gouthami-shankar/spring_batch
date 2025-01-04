package com.org.gouthami.customer_management.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@Slf4j
public class SchedulerConfig {

  @Autowired
  private JobLauncher jobLauncher;
  @Autowired
  private Job job;


  @Scheduled(fixedDelay = 10000, initialDelay = 2000)
  public void scheduleJob() throws Exception {
    log.info("Job scheduler started to work");
    JobParameters jobParameters = new JobParametersBuilder()
      .addLong("time", System.currentTimeMillis())
      .toJobParameters();
    jobLauncher.run(job, jobParameters);
    log.info("Job scheduler finished to work");
  }
}
