package com.example.validation.service;

import com.example.validation.dto.AddressDto;
import com.example.validation.dto.UserRegistrationRequest;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void testMethodValidation() {
        assertThrows(ConstraintViolationException.class, () -> {
            userService.checkEmail("invalid-email");
        });
    }

    @Test
    void testProgrammaticValidation() {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "J", "invalid-email", "phone", "pwd", "pwd", 10,
                new AddressDto(" ", " ", "123")
        );

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            userService.programmaticValidation(request);
        });

        assertTrue(exception.getConstraintViolations().size() > 0);
    }
    
    @Test
    void testProgrammaticValidationValid() {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "John Doe", "john@example.com", "+1234567890", "Password123!", "Password123!", 25,
                new AddressDto("123 Main St", "New York", "10001")
        );

        userService.programmaticValidation(request);
    }
}