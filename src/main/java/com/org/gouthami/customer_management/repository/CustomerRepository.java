package com.org.gouthami.customer_management.repository;

import com.org.gouthami.customer_management.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
