package com.example.validation.validation;

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
}