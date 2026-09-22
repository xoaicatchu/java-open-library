package com.example.jackson.config;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class ThirdPartyUserMixin {
    @JsonIgnore
    private String password;
}
