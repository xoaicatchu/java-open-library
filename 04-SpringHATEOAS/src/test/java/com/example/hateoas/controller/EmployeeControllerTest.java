package com.example.hateoas.controller;

import com.example.hateoas.dto.EmployeeDto;
import com.example.hateoas.entity.Employee;
import com.example.hateoas.repository.EmployeeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        repository.save(new Employee("Alice", "Manager", "HR", 5000.0));
        repository.save(new Employee("Bob", "Developer", "IT", 4500.0));
    }

    @Test
    void shouldReturnCollectionModelWithLinks() throws Exception {
        mockMvc.perform(get("/employees").accept(MediaType.parseMediaType("application/hal+json")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.employeeDtoList", hasSize(2)))
                .andExpect(jsonPath("$._links.self.href", endsWith("/employees")));
    }

    @Test
    void shouldReturnEntityModelWithLinks() throws Exception {
        Long id = repository.findAll().get(0).getId();

        mockMvc.perform(get("/employees/{id}", id).accept(MediaType.parseMediaType("application/hal+json")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.intValue())))
                .andExpect(jsonPath("$._links.self.href", endsWith("/employees/" + id)))
                .andExpect(jsonPath("$._links.employees.href", endsWith("/employees")))
                .andExpect(jsonPath("$._links.department.href", containsString("/employees/department/")));
    }

    @Test
    void shouldFilterByDepartmentWithLinks() throws Exception {
        mockMvc.perform(get("/employees/department/IT").accept(MediaType.parseMediaType("application/hal+json")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.employeeDtoList", hasSize(1)))
                .andExpect(jsonPath("$._embedded.employeeDtoList[0].name", is("Bob")))
                .andExpect(jsonPath("$._links.self.href", endsWith("/employees/department/IT")))
                .andExpect(jsonPath("$._links.all_employees.href", endsWith("/employees")));
    }

    @Test
    void shouldCreateEmployeeAndReturnLocationHeader() throws Exception {
        EmployeeDto newEmployee = new EmployeeDto(null, "Charlie", "Tester", "QA", 3500.0);

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEmployee))
                        .accept(MediaType.parseMediaType("application/hal+json")))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.name", is("Charlie")))
                .andExpect(jsonPath("$._links.self.href", notNullValue()));
    }

    @Test
    void shouldUpdateEmployeeAndReturnSelfLink() throws Exception {
        Long id = repository.findAll().get(0).getId();
        EmployeeDto updatedEmployee = new EmployeeDto(null, "Alice Updated", "Director", "HR", 6000.0);

        mockMvc.perform(put("/employees/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedEmployee))
                        .accept(MediaType.parseMediaType("application/hal+json")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Alice Updated")))
                .andExpect(jsonPath("$._links.self.href", endsWith("/employees/" + id)));
    }

    @Test
    void shouldDeleteEmployee() throws Exception {
        Long id = repository.findAll().get(0).getId();

        mockMvc.perform(delete("/employees/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/employees/{id}", id))
                .andExpect(status().isNotFound());
    }
}
