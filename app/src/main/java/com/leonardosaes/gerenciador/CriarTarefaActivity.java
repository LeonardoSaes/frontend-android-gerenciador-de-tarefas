package com.leonardosaes.gerenciador;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CriarTarefaActivity extends AppCompatActivity {

    private EditText editTituloNovaTarefa, editDescricaoNovaTarefa, editDataVencimentoNovaTarefa, editStatusTarefaNovaTarefa, editTagEnumNovaTarefa, editIdUsuarioNovaTarefa;
    private Button btnSalvarNovaTarefa;
    private ApiService apiService;
    private String authToken;
    private static final String TAG = "CriarTarefaActivity"; // TAG para logs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_tarefa); // Certifique-se de ter este layout

        // Inicializar elementos da UI
        editTituloNovaTarefa = findViewById(R.id.edit_titulo_nova_tarefa);
        editDescricaoNovaTarefa = findViewById(R.id.edit_descricao_nova_tarefa);
        editDataVencimentoNovaTarefa = findViewById(R.id.edit_data_vencimento_nova_tarefa);
        editStatusTarefaNovaTarefa = findViewById(R.id.edit_status_tarefa_nova_tarefa); // Novo campo
        editTagEnumNovaTarefa = findViewById(R.id.edit_tag_enum_nova_tarefa);           // Novo campo
        editIdUsuarioNovaTarefa = findViewById(R.id.edit_id_usuario_nova_tarefa);       // Novo campo
        btnSalvarNovaTarefa = findViewById(R.id.btn_salvar_nova_tarefa);
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        authToken = obterTokenSalvo(); // Obtenha o token do SharedPreferences

        btnSalvarNovaTarefa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarNovaTarefa();
            }
        });
    }

    private void salvarNovaTarefa() {
        String titulo = editTituloNovaTarefa.getText().toString().trim();
        String descricao = editDescricaoNovaTarefa.getText().toString().trim();
        String dataVencimentoStr = editDataVencimentoNovaTarefa.getText().toString().trim();
        String statusTarefa = editStatusTarefaNovaTarefa.getText().toString().trim();  // Novo campo
        String tagEnum = editTagEnumNovaTarefa.getText().toString().trim();            // Novo campo
        String idUsuarioStr = editIdUsuarioNovaTarefa.getText().toString().trim();        // Novo campo

        // Validar campos
        if (titulo.isEmpty() || descricao.isEmpty() || dataVencimentoStr.isEmpty() || statusTarefa.isEmpty() || tagEnum.isEmpty() || idUsuarioStr.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }


        // Converte a data para o formato ISO 8601 (yyyy-MM-dd)
        String dataVencimentoFormatada = formatarDataParaISO8601(dataVencimentoStr);
        if (dataVencimentoFormatada == null) {
            Toast.makeText(this, "Data de vencimento inválida. Use o formato ISO 8601 (yyyy-MM-dd) ou dd/MM/yyyy", Toast.LENGTH_SHORT).show();
            return;
        }

        int idUsuario;
        try {
            idUsuario = Integer.parseInt(idUsuarioStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "ID de usuário inválido. Deve ser um número inteiro", Toast.LENGTH_SHORT).show();
            return;
        }

        // Criar objeto TaskRequest
        TaskRequest novaTarefa = new TaskRequest(titulo, descricao, statusTarefa, tagEnum, dataVencimentoFormatada, idUsuario);

        // Chamar metodo da API para criar tarefa
        Call<Task> call = apiService.cadastrarTarefa("Bearer " + authToken, novaTarefa);
        call.enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                Log.d(TAG, "Código de resposta: " + response.code()); // Log do código de resposta
                Log.d(TAG, "Mensagem de resposta: " + response.message()); // Log da mensagem de resposta

                if (response.isSuccessful()) {
                    Toast.makeText(CriarTarefaActivity.this, "Tarefa criada com sucesso!", Toast.LENGTH_SHORT).show();
                    // Iniciar a ListaTarefasActivity e passar uma flag para indicar que a lista deve ser atualizada
                    Intent intent = new Intent(CriarTarefaActivity.this, ListaTarefasActivity.class);
                    intent.putExtra("atualizarLista", true); // Use uma chave bem definida
                    startActivity(intent);
                    finish(); // Voltar para a lista de tarefas
                } else {
                    // Tenta obter o corpo da resposta como string (pode estar em formato JSON)
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e(TAG, "Corpo do erro: " + errorBody);
                    } catch (IOException e) {
                        Log.e(TAG, "Erro ao obter corpo do erro: " + e.getMessage(), e);
                    }
                    Toast.makeText(CriarTarefaActivity.this, "Erro ao criar tarefa: " + response.message(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Erro ao criar tarefa: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                Log.e(TAG, "Erro de conexão: " + t.getMessage(), t); // Log da exceção
                Toast.makeText(CriarTarefaActivity.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String obterTokenSalvo() {
        return getSharedPreferences("auth_prefs", MODE_PRIVATE).getString("token", null);
    }

    private String formatarDataParaISO8601(String data) {
        try {
            // Tenta analisar a data no formato ISO 8601 diretamente
            LocalDateTime ldt = LocalDateTime.parse(data, DateTimeFormatter.ISO_DATE_TIME);
            return ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")); // Formata para yyyy-MM-dd
        } catch (DateTimeParseException e1) {
            try {
                // Se falhar, tenta analisar no formato dd/MM/yyyy
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());
                LocalDate localDate = LocalDate.parse(data, dateFormatter);
                return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")); // Formata para yyyy-MM-dd
            } catch (DateTimeParseException e2) {
                // Se ambos falharem, a data é inválida
                Log.e(TAG, "Data inválida: " + data, e2);
                return null;
            }
        }
    }
}
