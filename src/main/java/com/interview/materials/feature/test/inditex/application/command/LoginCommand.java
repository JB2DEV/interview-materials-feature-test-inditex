package com.interview.materials.feature.test.inditex.application.command;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class LoginCommand {
    private final String username;
    private final String password;

    @Builder
    public LoginCommand(String username, String password) {
        this.username = username;
        this.password = password;
    }
}