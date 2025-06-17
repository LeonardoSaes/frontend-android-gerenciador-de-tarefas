package com.leonardosaes.gerenciador.models;

public class RegisterResponse {
    private String message;
    private int userId;

    public String getMensagem() {
        return message;
    }

    public void setMemsagem(String message) {
        this.message = message;
    }

    public int getId() {
        return userId;
    }

    public void setId(int userId) {
        this.userId = userId;
    }

}
