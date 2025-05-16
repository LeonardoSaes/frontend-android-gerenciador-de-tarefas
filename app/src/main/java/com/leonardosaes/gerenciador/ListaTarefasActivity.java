package com.leonardosaes.gerenciador;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Import necessário para o FloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.content.Intent;
import android.view.View;

public class ListaTarefasActivity extends AppCompatActivity {

    private RecyclerView recyclerTarefas;
    private TarefaAdapter tarefaAdapter;
    private ApiService apiService;
    private String authToken; // Para armazenar o token
    private FloatingActionButton fabNovaTarefa; // Declaração do FloatingActionButton

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
        fabNovaTarefa = findViewById(R.id.fab_add_tarefa); // Correção do ID para fab_add_tarefa

        // Configura o listener para o FloatingActionButton
        fabNovaTarefa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Inicia a atividade CriarTarefaActivity
                Intent intent = new Intent(ListaTarefasActivity.this, CriarTarefaActivity.class);
                startActivity(intent);
            }
        });

        // Chama o metodo para listar as tarefas
        listarTarefas();
    }

    private String obterTokenSalvo() {
        SharedPreferences sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null); // Tenta obter o token com a chave "token"
        Log.d("ListaTarefasActivity", "Token recuperado: " + token); // Adiciona um log para verificar o valor
        return token; // Retorna o token ou null se não for encontrado
    }

    private void listarTarefas() {
        if (authToken != null) {
            Call<List<Task>> call = apiService.listarTarefas("Bearer " + authToken);
            call.enqueue(new Callback<List<Task>>() {
                @Override
                public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                    Log.d("ListaTarefasActivity", "Resposta da API: " + response.code() + " - " + response.message());
                    if (response.isSuccessful() && response.body() != null) {
                        List<Task> listaDeTarefas = response.body();
                        Log.d("ListaTarefasActivity", "Número de tarefas recebidas: " + listaDeTarefas.size());
                        if (!listaDeTarefas.isEmpty()) {
                            Log.d("ListaTarefasActivity", "Primeira tarefa: " + listaDeTarefas.get(0).getTitulo());
                        }
                        tarefaAdapter = new TarefaAdapter(listaDeTarefas);
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
            // Redirecionar para a tela de login?
        }
    }
}
