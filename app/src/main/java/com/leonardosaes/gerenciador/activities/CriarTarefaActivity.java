package com.leonardosaes.gerenciador.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView; // Importar para AdapterView
import android.widget.ArrayAdapter; // Importar para ArrayAdapter
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner; // Importar para Spinner
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.leonardosaes.gerenciador.api.ApiService;
import com.leonardosaes.gerenciador.R;
import com.leonardosaes.gerenciador.api.RetrofitClient;
import com.leonardosaes.gerenciador.models.Task;
import com.leonardosaes.gerenciador.models.TaskRequest;

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

    private EditText editTituloNovaTarefa, editDescricaoNovaTarefa, editDataVencimentoNovaTarefa; // Removido editStatusTarefaNovaTarefa e editIdUsuarioNovaTarefa
    private Spinner spinnerStatusTarefa, spinnerTagEnum; // Usando Spinners
    private Button btnSalvarNovaTarefa;
    private ApiService apiService;
    private String authToken;
    private int userId; // Para armazenar o ID do usuário logado
    private String statusTarefaSelecionada;
    private String tagEnumSelecionada;

    private static final String TAG = "CriarTarefaActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_tarefa);

        // Inicializar elementos da UI
        editTituloNovaTarefa = findViewById(R.id.edit_titulo_nova_tarefa);
        editDescricaoNovaTarefa = findViewById(R.id.edit_descricao_nova_tarefa);
        editDataVencimentoNovaTarefa = findViewById(R.id.edit_data_vencimento_nova_tarefa);
        spinnerStatusTarefa = findViewById(R.id.spinner_status_tarefa);
        spinnerTagEnum = findViewById(R.id.spinner_tag_enum);
        btnSalvarNovaTarefa = findViewById(R.id.btn_salvar_nova_tarefa);

        // Configurar Spinner de Status
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.status_options, // Certifique-se que este array está em res/values/arrays.xml
                android.R.layout.simple_spinner_item
        );
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatusTarefa.setAdapter(statusAdapter);
        spinnerStatusTarefa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                statusTarefaSelecionada = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                statusTarefaSelecionada = "";
            }
        });

        // Configurar Spinner de Tag
        ArrayAdapter<CharSequence> tagAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.tag_options, // Certifique-se que este array está em res/values/arrays.xml
                android.R.layout.simple_spinner_item
        );
        tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTagEnum.setAdapter(tagAdapter);
        spinnerTagEnum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tagEnumSelecionada = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                tagEnumSelecionada = "";
            }
        });

        // Obter o token e o ID do usuário
        authToken = obterTokenSalvo();
        userId = obterUserIdSalvo(); // Novo método para obter o ID do usuário

        // Verificar se o ID do usuário foi encontrado
        if (userId == -1) {
            Toast.makeText(this, "ID de usuário não encontrado. Faça login novamente.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

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

        // Validar campos (incluindo as variáveis do Spinner)
        if (titulo.isEmpty() || descricao.isEmpty() || dataVencimentoStr.isEmpty() || statusTarefaSelecionada.isEmpty() || tagEnumSelecionada.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String dataVencimentoFormatada = formatarDataParaISO8601(dataVencimentoStr);
        if (dataVencimentoFormatada == null) {
            Toast.makeText(this, "Data de vencimento inválida. Use o formato ISO 8601 (yyyy-MM-dd) ou dd/MM/yyyy", Toast.LENGTH_SHORT).show();
            return;
        }

        // Criar objeto TaskRequest - Usando userId obtido automaticamente
        TaskRequest novaTarefa = new TaskRequest(titulo, descricao, statusTarefaSelecionada, tagEnumSelecionada, dataVencimentoFormatada, userId);

        Call<Task> call = apiService.cadastrarTarefa("Bearer " + authToken, novaTarefa);
        call.enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                Log.d(TAG, "Código de resposta: " + response.code());
                Log.d(TAG, "Mensagem de resposta: " + response.message());

                if (response.isSuccessful()) {
                    Toast.makeText(CriarTarefaActivity.this, "Tarefa criada com sucesso!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CriarTarefaActivity.this, ListaTarefasActivity.class);
                    intent.putExtra("atualizarLista", true);
                    startActivity(intent);
                    finish();
                } else {
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
                Log.e(TAG, "Erro de conexão: " + t.getMessage(), t);
                Toast.makeText(CriarTarefaActivity.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String obterTokenSalvo() {
        return getSharedPreferences("auth_prefs", MODE_PRIVATE).getString("token", null);
    }

    private int obterUserIdSalvo() {
        // Assume que o userId é salvo em "auth_prefs" com a chave "user_id"
        SharedPreferences sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE);
        return sharedPreferences.getInt("user_id", -1); // Retorna -1 se não encontrar o ID
    }

    private String formatarDataParaISO8601(String data) {
        try {
            LocalDateTime ldt = LocalDateTime.parse(data, DateTimeFormatter.ISO_DATE_TIME);
            return ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e1) {
            try {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());
                LocalDate localDate = LocalDate.parse(data, dateFormatter);
                return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException e2) {
                Log.e(TAG, "Data inválida: " + data, e2);
                return null;
            }
        }
    }
}