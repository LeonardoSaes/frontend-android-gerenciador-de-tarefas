package com.leonardosaes.gerenciador;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText editEmail, editSenha;
    private Button btnLogin;
    private TextView textRegister;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar elementos da UI
        editEmail = findViewById(R.id.edit_email);
        editSenha = findViewById(R.id.edit_senha);
        btnLogin = findViewById(R.id.btn_login);
        textRegister = findViewById(R.id.text_register);

        // Inicializar ApiService
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        // Configurar clique do botão de login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        textRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                //startActivity(intent);
            }
        });
    }

    private void login() {
        // Obter texto dos campos
        String email = editEmail.getText().toString().trim();
        String senha = editSenha.getText().toString().trim();

        // Validar campos
        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Criar objeto LoginRequest
        LoginRequest loginRequest = new LoginRequest(email, senha);

        // Chamar metodo de login da API
        Call<LoginResponse> call = apiService.loginUser(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse != null && loginResponse.getToken() != null) {
                        // Login bem-sucedido
                        String token = loginResponse.getToken();
                        int userId = loginResponse.getId(); // Supondo que a classe LoginResponse tenha um metodo getId()
                        String nomeUsuario = loginResponse.getNomeUsuario();
                        // Salvar o token e o ID do usuário
                        SharedPreferences sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("token", token);
                        editor.putInt("user_id", userId); // Salva o ID do usuário
                        editor.apply();
                        Log.d("LoginActivity", "Token: " + token + ", Nome: " + nomeUsuario + ", ID: " + userId);
                        // Navegar para a próxima tela (Lista de Tarefas, por exemplo)
                        Intent intent = new Intent(LoginActivity.this, ListaTarefasActivity.class);
                        startActivity(intent);
                        finish(); // Opcional: finalizar a LoginActivity
                    } else {
                        // Erro no login (token nulo)
                        Toast.makeText(LoginActivity.this, "Erro ao realizar login: Token nulo", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Erro no login (código de resposta diferente de 200)
                    Toast.makeText(LoginActivity.this, "Erro ao realizar login: " + response.message(), Toast.LENGTH_SHORT).show();
                    Log.e("LoginActivity", "Erro na resposta: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Erro na requisição
                Toast.makeText(LoginActivity.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("LoginActivity", "Erro na requisição: " + t.getMessage(), t);
            }
        });
    }
}
