package com.leonardosaes.gerenciador.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.leonardosaes.gerenciador.R;
import com.leonardosaes.gerenciador.models.RegisterRequest;
import com.leonardosaes.gerenciador.models.RegisterResponse;
import com.leonardosaes.gerenciador.api.ApiService;
import com.leonardosaes.gerenciador.api.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.IOException; // Adicionar este import

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText editNome, editEmail, editSenha;
    private Button btnRegister;
    private ApiService apiService;

    private static final String TAG = "RegisterActivity"; // Adicionar TAG para logs

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

        // Você pode adicionar mais validações aqui, como força da senha
        if (senha.length() < 6) { // Exemplo: senha mínima de 6 caracteres
            Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }


        // Criar objeto RegisterRequest
        RegisterRequest registerRequest = new RegisterRequest(nome, email, senha);

        // Chamar metodo de registro da API
        Call<RegisterResponse> call = apiService.registerUser(registerRequest);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                Log.d(TAG, "Código de resposta: " + response.code()); // Log do código de resposta
                Log.d(TAG, "Mensagem HTTP da resposta: " + response.message()); // Log da mensagem HTTP

                if (response.isSuccessful()) {
                    // Cadastro bem-sucedido
                    RegisterResponse registerResponse = response.body();
                    if (registerResponse != null) {
                        // Usar getMensagem() e getId() conforme a RegisterResponse que definimos
                        String message = registerResponse.getMensagem(); // Usar getMensagem()
                        int userId = registerResponse.getId();           // Usar getId()

                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "User ID: " + userId);
                        // Navegar para a tela de login
                        finish(); // Encerra a RegisterActivity e volta para a tela anterior (LoginActivity)
                    } else {
                        // Erro no registo (resposta nula) - Cenário improvável se isSuccessful for true
                        Toast.makeText(RegisterActivity.this, "Erro ao realizar registo: resposta do servidor vazia", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Resposta isSuccessful, mas corpo está nulo.");
                    }
                } else {
                    // Erro no registo (código de resposta diferente de 2xx)
                    String errorMessage = "Erro desconhecido ao registrar.";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string(); // Tenta obter a mensagem de erro do corpo
                            Log.e(TAG, "Corpo do erro: " + errorMessage); // Loga o corpo do erro
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Erro ao ler corpo do erro: " + e.getMessage(), e);
                    }
                    Toast.makeText(RegisterActivity.this, "Erro ao realizar registo: " + errorMessage, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Erro na resposta: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                // Erro na requisição (problemas de rede, servidor fora do ar, etc.)
                Toast.makeText(RegisterActivity.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Erro na requisição: " + t.getMessage(), t); // Loga a exceção completa
            }
        });
    }
}