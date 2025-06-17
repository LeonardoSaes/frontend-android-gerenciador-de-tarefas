package com.leonardosaes.gerenciador.models;

import java.io.Serializable;
import java.time.LocalDate;

import com.google.gson.annotations.SerializedName;
import com.leonardosaes.gerenciador.utils.tagEnum;

public class Task implements Serializable {
    private Long id;
    private String titulo;
    @SerializedName("descricao")
    private String descricao;
    private String statusTarefa;
    private com.leonardosaes.gerenciador.utils.tagEnum tagEnum;
    private LocalDate prazoFinal;
    @SerializedName("usuario_id")
    private Long usuarioId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getStatusTarefa() {
        return statusTarefa;
    }

    public void setStatusTarefa(String statusTarefa) {
        this.statusTarefa = statusTarefa;
    }

    public tagEnum getTagEnum() {
        return tagEnum;
    }

    public void setTagEnum(tagEnum tagEnum) {
        this.tagEnum = tagEnum;
    }

    public LocalDate getPrazoFinal() {
        return prazoFinal;
    }

    public void setPrazoFinal(LocalDate prazoFinal) {
        this.prazoFinal = prazoFinal;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
}