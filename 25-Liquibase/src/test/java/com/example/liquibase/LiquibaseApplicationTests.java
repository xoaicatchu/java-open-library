package com.example.liquibase;

import com.example.liquibase.entity.Department;
import com.example.liquibase.entity.Employee;
import com.example.liquibase.repository.DepartmentRepository;
import com.example.liquibase.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LiquibaseApplicationTests {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
        assertThat(employeeRepository).isNotNull();
        assertThat(departmentRepository).isNotNull();
    }

    @Test
    void testSeedDataIsPresent() {
        List<Department> departments = departmentRepository.findAll();
        assertThat(departments).isNotEmpty();
        assertThat(departments).anyMatch(d -> d.getName().equals("Engineering"));

        List<Employee> employees = employeeRepository.findAll();
        assertThat(employees).isNotEmpty();
        assertThat(employees).anyMatch(e -> e.getEmail().equals("alice@example.com"));
    }

    @Test
    void testLiquibaseStatusEndpoint() throws Exception {
        mockMvc.perform(get("/api/liquibase/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].ID").value("01-create-departments"));
    }
    
    @Test
    void testDatabaseChangelogHasAllChangesets() throws Exception {
        mockMvc.perform(get("/api/liquibase/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(8)); // 01-dept, 01-emp, 02-email, 02-idx, 03-seed-dept, 03-seed-emp, 04-salary, 04-modify
        // Actually length is 8 because 01 has 2, 02 has 2, 03 has 2, 04 has 2.
    }
    
    @Test
    void testSaveNewEmployee() {
        Employee employee = new Employee();
        employee.setFirstName("Bob");
        employee.setLastName("Builder");
        employee.setEmail("bob@builder.com");
        
        employeeRepository.save(employee);
        
        Employee saved = employeeRepository.findById(employee.getId()).orElse(null);
        assertThat(saved).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("Bob");
    }
}
