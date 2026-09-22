package com.example.jdbi;

import com.example.jdbi.domain.Employee;
import com.example.jdbi.repository.EmployeeDao;
import com.example.jdbi.repository.EmployeeRepository;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@org.springframework.test.annotation.DirtiesContext(classMode = org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ApplicationTests {

    @Autowired
    private Jdbi jdbi;

    @Autowired
    private EmployeeRepository fluentRepository;

    @Test
    void testFluentApi() {
        List<Employee> itEmployees = fluentRepository.findByDepartment("IT");
        assertNotNull(itEmployees);
        assertFalse(itEmployees.isEmpty());
        assertEquals("Alice", itEmployees.get(0).name());
    }
    
    @Test
    void testSqlObjectInsertAndFind() {
        EmployeeDao dao = jdbi.onDemand(EmployeeDao.class);
        int id = dao.insert("John", "HR", "john@example.com");
        assertTrue(id > 0);
        
        Employee emp = dao.findById(id).orElseThrow();
        assertEquals("John", emp.name());
        assertEquals("HR", emp.department().name());
        assertEquals("john@example.com", emp.email().value());
    }
    
    @Test
    void testPreparedBatch() {
        EmployeeDao dao = jdbi.onDemand(EmployeeDao.class);
        dao.insertBatch(Arrays.asList("A", "B"), Arrays.asList("IT", "SALES"), Arrays.asList("a@a.com", "b@b.com"));
        List<Employee> all = dao.findAll();
        assertTrue(all.size() >= 5); // 3 from data.sql + 2 inserted
    }
    
    @Test
    void testTransaction() {
        fluentRepository.executeInTransaction();
        EmployeeDao dao = jdbi.onDemand(EmployeeDao.class);
        Employee emp = dao.findById(1).orElseThrow();
        assertEquals("SALES", emp.department().name());
    }
    
    @Test
    void testSqlObjectTransaction() {
        EmployeeDao dao = jdbi.onDemand(EmployeeDao.class);
        dao.updateMultipleInTransaction(2, "Updated 2", 3, "Updated 3");
        Employee emp2 = dao.findById(2).orElseThrow();
        assertEquals("Updated 2", emp2.name());
        Employee emp3 = dao.findById(3).orElseThrow();
        assertEquals("Updated 3", emp3.name());
    }
    
    @Test
    void testFluentApiHandleUseTransaction() {
        jdbi.useTransaction(handle -> {
            int count = handle.createUpdate("DELETE FROM employee WHERE id = 1").execute();
            assertEquals(1, count);
        });
    }
}
