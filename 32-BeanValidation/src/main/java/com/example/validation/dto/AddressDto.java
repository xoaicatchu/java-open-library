package com.example.validation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AddressDto(
    @NotBlank(message = "Street cannot be blank")
    String street,

    @NotBlank(message = "City cannot be blank")
    String city,

    @NotBlank(message = "Zip code cannot be blank")
    @Pattern(regexp = "^\\d{5}(-\\d{4})?$", message = "Invalid zip code")
    String zipCode
) {}