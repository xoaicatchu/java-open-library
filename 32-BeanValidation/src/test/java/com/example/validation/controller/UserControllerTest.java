package com.example.validation.controller;

import com.example.validation.dto.AddressDto;
import com.example.validation.dto.UserRegistrationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testValidRegistration() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "John Doe", "john@example.com", "+1234567890", "Password123!", "Password123!", 25,
                new AddressDto("123 Main St", "New York", "10001")
        );

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testStandardAnnotationFailures() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "J", "invalid-email", "phone", "pwd", "pwd", 10,
                new AddressDto(" ", " ", "123")
        );

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void testCustomValidatorStrongPassword() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "John Doe", "john@example.com", "+1234567890", "weak", "weak", 25,
                new AddressDto("123 Main St", "New York", "10001")
        );

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("password"))
                .andExpect(jsonPath("$.errors[0].message").value("Password must be at least 8 chars and contain uppercase, lowercase, number, and special char"));
    }

    @Test
    void testValidationGroupsOnUpdate() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "John Doe", "john@example.com", "+1234567890", null, null, 25,
                new AddressDto("123 Main St", "New York", "10001")
        );

        mockMvc.perform(put("/users/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testCrossFieldValidationPasswordMatch() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "John Doe", "john@example.com", "+1234567890", "Password123!", "Password321!", 25,
                new AddressDto("123 Main St", "New York", "10001")
        );

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("confirmPassword"))
                .andExpect(jsonPath("$.errors[0].message").value("Passwords do not match"));
    }
}