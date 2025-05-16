package com.leonardosaes.gerenciador;

public class LoginResponse {
    private String token;
    private String nomeUsuario;

    private int id;

    public LoginResponse(String token, String nomeUsuario) {
        this.token = token;
        this.nomeUsuario = nomeUsuario;
    }

    public String getToken() {
        return token;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public int getId(){
        return id;
    }

}

