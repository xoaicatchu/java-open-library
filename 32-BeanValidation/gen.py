import os

base_dir = r"d:\GitHub\java-example\32-BeanValidation"

files = {
    "pom.xml": """<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.4.1</version>
        <relativePath/>
    </parent>
    <groupId>com.example</groupId>
    <artifactId>validation</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>32-BeanValidation</name>
    <properties>
        <java.version>21</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>""",
    ".mvn/wrapper/maven-wrapper.properties": """distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip
wrapperUrl=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar""",
    "src/main/resources/application.yml": """server:
  port: 8132
spring:
  threads:
    virtual:
      enabled: true""",
    "requests.http": """### Register User
POST http://localhost:8132/users/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "+1234567890",
  "password": "Password123!",
  "confirmPassword": "Password123!",
  "age": 25,
  "address": {
    "street": "123 Main St",
    "city": "New York",
    "zipCode": "10001"
  }
}""",
    "src/main/java/com/example/validation/ValidationApplication.java": """package com.example.validation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ValidationApplication {
    public static void main(String[] args) {
        SpringApplication.run(ValidationApplication.class, args);
    }
}""",
    "src/main/java/com/example/validation/dto/ValidationGroups.java": """package com.example.validation.dto;

public interface ValidationGroups {
    interface OnCreate {}
    interface OnUpdate {}
}""",
    "src/main/java/com/example/validation/dto/AddressDto.java": """package com.example.validation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AddressDto(
    @NotBlank(message = "Street cannot be blank")
    String street,

    @NotBlank(message = "City cannot be blank")
    String city,

    @NotBlank(message = "Zip code cannot be blank")
    @Pattern(regexp = "^\\\\d{5}(-\\\\d{4})?$", message = "Invalid zip code")
    String zipCode
) {}""",
    "src/main/java/com/example/validation/dto/UserRegistrationRequest.java": """package com.example.validation.dto;

import com.example.validation.validation.PasswordMatch;
import com.example.validation.validation.PhoneNumber;
import com.example.validation.validation.StrongPassword;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@PasswordMatch(groups = {ValidationGroups.OnCreate.class})
public record UserRegistrationRequest(
    @NotBlank(message = "Name is required", groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class})
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters", groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class})
    String name,

    @NotBlank(message = "Email is required", groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class})
    @Email(message = "Invalid email format", groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class})
    String email,

    @PhoneNumber(groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class})
    String phone,

    @StrongPassword(groups = {ValidationGroups.OnCreate.class})
    String password,

    String confirmPassword,
    
    @Min(value = 18, message = "Age must be at least 18", groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class})
    @Max(value = 120, message = "Age must be less than 120", groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class})
    @NotNull(message = "Age is required", groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class})
    Integer age,

    @NotNull(message = "Address is required", groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class})
    @Valid
    AddressDto address
) {}""",
    "src/main/java/com/example/validation/validation/PasswordMatch.java": """package com.example.validation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordMatchValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatch {
    String message() default "Passwords do not match";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}""",
    "src/main/java/com/example/validation/validation/PasswordMatchValidator.java": """package com.example.validation.validation;

import com.example.validation.dto.UserRegistrationRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, UserRegistrationRequest> {
    @Override
    public boolean isValid(UserRegistrationRequest value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (value.password() == null && value.confirmPassword() == null) {
            return true;
        }
        boolean isValid = value.password() != null && value.password().equals(value.confirmPassword());
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                   .addPropertyNode("confirmPassword").addConstraintViolation();
        }
        return isValid;
    }
}""",
    "src/main/java/com/example/validation/validation/PhoneNumber.java": """package com.example.validation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneNumber {
    String message() default "Invalid phone number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}""",
    "src/main/java/com/example/validation/validation/PhoneNumberValidator.java": """package com.example.validation.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        return value.matches("^\\\\+?[0-9. ()-]{7,25}$");
    }
}""",
    "src/main/java/com/example/validation/validation/StrongPassword.java": """package com.example.validation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {
    String message() default "Password must be at least 8 chars and contain uppercase, lowercase, number, and special char";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}""",
    "src/main/java/com/example/validation/validation/StrongPasswordValidator.java": """package com.example.validation.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        return value.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\\\d)(?=.*[@$!%*?&])[A-Za-z\\\\d@$!%*?&]{8,}$");
    }
}""",
    "src/main/java/com/example/validation/service/UserService.java": """package com.example.validation.service;

import com.example.validation.dto.UserRegistrationRequest;
import com.example.validation.dto.ValidationGroups;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Set;
import jakarta.validation.constraints.Email;

@Service
@Validated
public class UserService {

    private final Validator validator;

    public UserService(Validator validator) {
        this.validator = validator;
    }

    public void registerUser(@Validated(ValidationGroups.OnCreate.class) UserRegistrationRequest request) {
    }

    public void updateUser(@Validated(ValidationGroups.OnUpdate.class) UserRegistrationRequest request) {
    }

    public void checkEmail(@Email String email) {
    }

    public void programmaticValidation(UserRegistrationRequest request) {
        Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(request, ValidationGroups.OnCreate.class);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}""",
    "src/main/java/com/example/validation/exception/GlobalExceptionHandler.java": """package com.example.validation.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setTitle("Bad Request");
        
        List<Map<String, String>> fieldErrors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    String fieldName = error instanceof FieldError ? ((FieldError) error).getField() : error.getObjectName();
                    String errorMessage = error.getDefaultMessage();
                    return Map.of("field", fieldName, "message", errorMessage != null ? errorMessage : "");
                })
                .collect(Collectors.toList());

        problemDetail.setProperty("errors", fieldErrors);
        return problemDetail;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Constraint violation");
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setTitle("Bad Request");
        
        List<Map<String, String>> errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> Map.of(
                        "field", violation.getPropertyPath().toString(),
                        "message", violation.getMessage()
                ))
                .collect(Collectors.toList());
                
        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }
}""",
    "src/main/java/com/example/validation/controller/UserController.java": """package com.example.validation.controller;

import com.example.validation.dto.UserRegistrationRequest;
import com.example.validation.dto.ValidationGroups;
import com.example.validation.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Validated(ValidationGroups.OnCreate.class) @RequestBody UserRegistrationRequest request) {
        userService.registerUser(request);
        return ResponseEntity.ok("User registered");
    }

    @PutMapping("/update")
    public ResponseEntity<String> update(@Validated(ValidationGroups.OnUpdate.class) @RequestBody UserRegistrationRequest request) {
        userService.updateUser(request);
        return ResponseEntity.ok("User updated");
    }
}""",
    "src/test/java/com/example/validation/ValidationApplicationTests.java": """package com.example.validation;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ValidationApplicationTests {

    @Test
    void contextLoads() {
    }
}""",
    "src/test/java/com/example/validation/controller/UserControllerTest.java": """package com.example.validation.controller;

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
}""",
    "src/test/java/com/example/validation/service/UserServiceTest.java": """package com.example.validation.service;

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
}"""
}

for rel_path, content in files.items():
    full_path = os.path.join(base_dir, rel_path)
    os.makedirs(os.path.dirname(full_path), exist_ok=True)
    with open(full_path, "w", encoding="utf-8") as f:
        f.write(content)

print("Project generated.")
