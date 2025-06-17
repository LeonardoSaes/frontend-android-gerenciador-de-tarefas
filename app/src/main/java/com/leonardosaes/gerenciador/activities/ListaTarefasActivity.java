package com.leonardosaes.gerenciador.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.leonardosaes.gerenciador.api.ApiService;
import com.leonardosaes.gerenciador.R;
import com.leonardosaes.gerenciador.api.RetrofitClient;
import com.leonardosaes.gerenciador.adapter.TarefaAdapter;
import com.leonardosaes.gerenciador.models.Task;

import android.app.AlertDialog;
import android.content.DialogInterface;

public class ListaTarefasActivity extends AppCompatActivity {

    private RecyclerView recyclerTarefas;
    private TarefaAdapter tarefaAdapter;
    private ApiService apiService;
    private String authToken;
    private FloatingActionButton fabNovaTarefa;
    private List<Task> listaDeTarefas; // Mantenha uma referência à lista de tarefas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_tarefas);

        // Inicializa o RecyclerView e o ApiService
        recyclerTarefas = findViewById(R.id.recycler_tarefas);
        recyclerTarefas.setLayoutManager(new LinearLayoutManager(this));
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        // Recupera o token de autenticação
        authToken = obterTokenSalvo();

        // Inicializa o FloatingActionButton
        fabNovaTarefa = findViewById(R.id.fab_add_tarefa);

        // Configura o listener para o FloatingActionButton
        if (fabNovaTarefa != null) {
            fabNovaTarefa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Inicia a atividade CriarTarefaActivity
                    Intent intent = new Intent(ListaTarefasActivity.this, CriarTarefaActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            Log.e("ListaTarefasActivity", "FloatingActionButton não encontrado!");
        }

        // Chama o metodo para listar as tarefas
        listarTarefas();
    }

    private String obterTokenSalvo() {
        SharedPreferences sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        Log.d("ListaTarefasActivity", "Token recuperado: " + token);
        return token;
    }

    private void listarTarefas() {
        if (authToken != null) {
            Call<List<Task>> call = apiService.listarTarefas("Bearer " + authToken);
            call.enqueue(new Callback<List<Task>>() {
                @Override
                public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                    Log.d("ListaTarefasActivity", "Resposta da API: " + response.code() + " - " + response.message());
                    if (response.isSuccessful() && response.body() != null) {
                        listaDeTarefas = response.body(); // Inicializa a lista de tarefas
                        Log.d("ListaTarefasActivity", "Número de tarefas recebidas: " + listaDeTarefas.size());
                        if (!listaDeTarefas.isEmpty()) {
                            Log.d("ListaTarefasActivity", "Primeira tarefa: " + listaDeTarefas.get(0).getTitulo());
                        }
                        tarefaAdapter = new TarefaAdapter(listaDeTarefas, ListaTarefasActivity.this); // Passa o contexto para o adapter
                        recyclerTarefas.setAdapter(tarefaAdapter);
                    } else {
                        Toast.makeText(ListaTarefasActivity.this, "Erro ao listar tarefas: " + response.code() + " - " + response.message(), Toast.LENGTH_SHORT).show();
                        Log.e("ListaTarefasActivity", "Erro ao listar tarefas: " + response.code() + " - " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<List<Task>> call, Throwable t) {
                    Log.e("ListaTarefasActivity", "Erro de conexão: " + t.getMessage());
                    Toast.makeText(ListaTarefasActivity.this, "Erro de conexão ao listar tarefas: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(ListaTarefasActivity.this, "Token de autenticação não encontrado.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ListaTarefasActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void excluirTarefa(Long tarefaId, int position) { // Removido o static
        new AlertDialog.Builder(this) // Use 'this' como contexto
                .setTitle("Excluir Tarefa")
                .setMessage("Tem certeza que deseja excluir esta tarefa?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (apiService != null) {
                            Call<Void> call = apiService.excluirTarefa("Bearer " + authToken, tarefaId);
                            call.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(ListaTarefasActivity.this, "Tarefa excluída com sucesso!", Toast.LENGTH_SHORT).show();
                                        // Remova a tarefa da lista e notifique o adaptador
                                        if (listaDeTarefas != null && position < listaDeTarefas.size()) {
                                            listaDeTarefas.remove(position);
                                            tarefaAdapter.notifyItemRemoved(position);
                                        } else {
                                            Log.e("ListaTarefasActivity", "Erro: posição inválida ao excluir tarefa.");
                                        }
                                    } else {
                                        Toast.makeText(ListaTarefasActivity.this, "Erro ao excluir tarefa: " + response.message(), Toast.LENGTH_SHORT).show();
                                        Log.e("ListaTarefasActivity", "Erro ao excluir tarefa: " + response.code() + " - " + response.message());
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Toast.makeText(ListaTarefasActivity.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.e("ListaTarefasActivity", "Erro de conexão: " + t.getMessage(), t);
                                }
                            });
                        } else {
                            Toast.makeText(ListaTarefasActivity.this, "ApiService não inicializado", Toast.LENGTH_SHORT).show();
                            Log.e("ListaTarefasActivity", "ApiService é nulo");
                        }
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }
}

