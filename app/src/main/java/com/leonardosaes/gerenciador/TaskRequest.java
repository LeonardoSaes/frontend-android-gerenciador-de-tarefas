package com.leonardosaes.gerenciador;

import java.time.LocalDateTime;

public class TaskRequest {
    private String titulo;
    private String descricao;
    private String statusTarefa;
    private String tagEnum;
    private String prazoFinal;
    private int idUsuario;

    public TaskRequest(String titulo, String descricao, String statusTarefa, String tagEnum, String prazoFinal, int idUsuario) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.statusTarefa = statusTarefa;
        this.tagEnum = tagEnum;
        this.prazoFinal = prazoFinal;
        this.idUsuario = idUsuario;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getStatusTarefa() {
        return statusTarefa;
    }

    public String getTagEnum() {
        return tagEnum;
    }

    public String getPrazoFinal() {
        return prazoFinal;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setStatusTarefa(String statusTarefa) {
        this.statusTarefa = statusTarefa;
    }

    public void setTagEnum(String tagEnum) {
        this.tagEnum = tagEnum;
    }

    public void setPrazoFinal(String prazoFinal) {
        this.prazoFinal = prazoFinal;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
}
