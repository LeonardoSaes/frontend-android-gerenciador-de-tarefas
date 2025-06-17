package com.leonardosaes.gerenciador.api;

import com.leonardosaes.gerenciador.models.LoginRequest;
import com.leonardosaes.gerenciador.models.LoginResponse;
import com.leonardosaes.gerenciador.models.RegisterRequest;
import com.leonardosaes.gerenciador.models.RegisterResponse;
import com.leonardosaes.gerenciador.models.Task;
import com.leonardosaes.gerenciador.models.TaskRequest;

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

    @POST("/tarefa/cadastrar")
    Call<Task> cadastrarTarefa(
            @Header("Authorization") String authToken,
            @Body TaskRequest taskRequest
    );

    @GET("/tarefa/listar")
    Call<List<Task>> listarTarefas(@Header("Authorization") String authorization);

    @PATCH("/tarefa/{id}")
    Call<Task> atualizarTarefa(@Path("id") Long id, @Body Task tarefaAtualizada);

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