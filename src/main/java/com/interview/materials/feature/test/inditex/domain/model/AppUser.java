package com.interview.materials.feature.test.inditex.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AppUser {
    private String username;
    private String password;
    private List<String> roles;
}