package com.example.portfolio.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SignupRequest {
    private String username;
    private String password;
    private String passwordConfirm;
    private String email;
    private String nickname;

    public boolean isPasswordMatch() {
        return password != null && password.equals(passwordConfirm);
    }
}
