package com.interview.materials.feature.test.inditex.infrastructure.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.security")
@Validated
public record SecurityProperties(
        @NestedConfigurationProperty
        AdminUser admin
) {
    public record AdminUser(
            @NotBlank String username,
            @NotBlank String password
    ) {}
}