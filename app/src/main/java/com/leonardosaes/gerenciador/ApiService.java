package com.leonardosaes.gerenciador;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("/auth/register")
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

    @POST("/auth/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("/tarefa/cadastrar") // Alterado para o endpoint correto
    Call<Task> cadastrarTarefa(
            @Header("Authorization") String authToken, // Adicione o Header de Autorização, se necessário
            @Body TaskRequest taskRequest // Use TaskRequest no corpo da requisição
    );


    @GET("/tarefa/listar")
    Call<List<Task>> listarTarefas(@Header("Authorization") String authorization);

    @PATCH("/tarefa/{id}")
    Call<Task> atualizarTarefa(@Path("id") Long id, @Body Task tarefaAtualizada);

    // Na interface ApiService
    @DELETE("/tarefa/{id}")
    Call<Void> excluirTarefa(
            @Header("Authorization") String authToken,
            @Path("id") Long id
    );


    @GET("/tarefa/buscar-por-titulo")
    Call<List<Task>> buscarTarefasPorTitulo(@Query("titulo") String titulo);

    @GET("/tarefa/busca-por-descricao")
    Call<List<Task>> buscarTarefasPorDescricao(@Query("descricao") String descricao);
}