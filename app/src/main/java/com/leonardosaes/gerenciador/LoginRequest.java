package com.leonardosaes.gerenciador;

public class LoginRequest {
    private String email;
    private String password;

    public LoginRequest(String email, String senha) {
        this.email = email;
        this.password = senha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return password;
    }

    public void setSenha(String senha) {
        this.password = senha;
    }
}
