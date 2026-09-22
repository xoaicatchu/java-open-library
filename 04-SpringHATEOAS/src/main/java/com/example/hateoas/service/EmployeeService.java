package com.example.hateoas.service;

import com.example.hateoas.dto.EmployeeDto;
import com.example.hateoas.entity.Employee;
import com.example.hateoas.exception.EmployeeNotFoundException;
import com.example.hateoas.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public List<EmployeeDto> findAll() {
        return repository.findAll().stream()
                .map(this::toDto)
                .toList();
    }
    
    public List<EmployeeDto> findByDepartment(String department) {
        return repository.findByDepartment(department).stream()
                .map(this::toDto)
                .toList();
    }

    public EmployeeDto findById(Long id) {
        return repository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    @Transactional
    public EmployeeDto save(EmployeeDto dto) {
        Employee employee = new Employee(dto.name(), dto.role(), dto.department(), dto.salary());
        return toDto(repository.save(employee));
    }

    @Transactional
    public EmployeeDto update(Long id, EmployeeDto dto) {
        return repository.findById(id)
                .map(employee -> {
                    employee.setName(dto.name());
                    employee.setRole(dto.role());
                    employee.setDepartment(dto.department());
                    employee.setSalary(dto.salary());
                    return toDto(repository.save(employee));
                })
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EmployeeNotFoundException(id);
        }
        repository.deleteById(id);
    }

    private EmployeeDto toDto(Employee employee) {
        return new EmployeeDto(
                employee.getId(),
                employee.getName(),
                employee.getRole(),
                employee.getDepartment(),
                employee.getSalary()
        );
    }
}
