package com.example.jdbi.service;

import com.example.jdbi.domain.Employee;
import com.example.jdbi.repository.EmployeeDao;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeDao employeeDao;

    public EmployeeService(Jdbi jdbi) {
        this.employeeDao = jdbi.onDemand(EmployeeDao.class);
    }

    public List<Employee> getAllEmployees() {
        return employeeDao.findAll();
    }

    public Employee getEmployeeById(int id) {
        return employeeDao.findById(id).orElseThrow(() -> new IllegalArgumentException("Employee not found"));
    }

    @Transactional
    public Employee createEmployee(Employee employee) {
        int id = employeeDao.insert(employee.name(), employee.department().name(), employee.email().value());
        return getEmployeeById(id);
    }

    @Transactional
    public void batchCreateEmployees(List<Employee> employees) {
        List<String> names = employees.stream().map(Employee::name).collect(Collectors.toList());
        List<String> depts = employees.stream().map(e -> e.department().name()).collect(Collectors.toList());
        List<String> emails = employees.stream().map(e -> e.email().value()).collect(Collectors.toList());
        employeeDao.insertBatch(names, depts, emails);
    }
}
