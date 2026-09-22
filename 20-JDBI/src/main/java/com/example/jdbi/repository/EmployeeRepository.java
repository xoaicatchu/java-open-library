package com.example.jdbi.repository;

import com.example.jdbi.domain.Employee;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmployeeRepository {
    private final Jdbi jdbi;

    public EmployeeRepository(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    // Sử dụng Fluent API
    public List<Employee> findByDepartment(String department) {
        return jdbi.withHandle(handle ->
            handle.createQuery("SELECT * FROM employee WHERE department = :dept")
                  .bind("dept", department)
                  .mapTo(Employee.class)
                  .list()
        );
    }
    
    // Sử dụng Transaction thủ công
    public void executeInTransaction() {
        jdbi.useTransaction(handle -> {
            handle.createUpdate("UPDATE employee SET department = :dept WHERE id = :id")
                  .bind("dept", "SALES")
                  .bind("id", 1)
                  .execute();
        });
    }
}
