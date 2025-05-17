package com.leonardosaes.gerenciador;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText editNome, editEmail, editSenha;
    private Button btnRegister;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializar elementos da UI
        editNome = findViewById(R.id.edit_nome);
        editEmail = findViewById(R.id.edit_email);
        editSenha = findViewById(R.id.edit_senha);
        btnRegister = findViewById(R.id.btn_register);

        // Inicializar ApiService
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        // Configurar clique do botão de registo
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        // Obter texto dos campos
        String nome = editNome.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String senha = editSenha.getText().toString().trim();

        // Validar campos
        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Criar objeto RegisterRequest
        RegisterRequest registerRequest = new RegisterRequest(nome, email, senha);

        // Chamar metodo de registro da API
        Call<RegisterResponse> call = apiService.registerUser(registerRequest);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful()) {
                    RegisterResponse registerResponse = response.body();
                    if (registerResponse != null) {
                        // Registo bem-sucedido
                        String message = registerResponse.getMessage();
                        int userId = registerResponse.getUserId();
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                        Log.d("RegisterActivity", "User ID: " + userId);
                        // Navegar para a tela de login
                        finish(); // Encerra a RegisterActivity e volta para a tela anterior (LoginActivity)
                        // Ou navegar para a tela de lista de tarefas, se desejar
                        // Intent intent = new Intent(RegisterActivity.this, ListaTarefasActivity.class);
                        // startActivity(intent);
                        // finish();
                    } else {
                        // Erro no registo (resposta nula)
                        Toast.makeText(RegisterActivity.this, "Erro ao realizar registo", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Erro no registo (código de resposta diferente de 200)
                    Toast.makeText(RegisterActivity.this, "Erro ao realizar registo: " + response.message(), Toast.LENGTH_SHORT).show();
                    Log.e("RegisterActivity", "Erro na resposta: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                // Erro na requisição
                Toast.makeText(RegisterActivity.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("RegisterActivity", "Erro na requisição: " + t.getMessage(), t);
            }
        });
    }
}

