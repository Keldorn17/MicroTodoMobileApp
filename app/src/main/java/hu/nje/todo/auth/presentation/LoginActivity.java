package hu.nje.todo.auth.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import dagger.hilt.android.AndroidEntryPoint;
import hu.nje.todo.todo.presentation.view.MainActivity;
import hu.nje.todo.databinding.ActivityLoginBinding;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {

    private AuthViewModel viewModel;
    private ActivityLoginBinding binding;

    private final ActivityResultLauncher<Intent> loginLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    viewModel.handleAuthResult(result.getData());
                } else {
                    Toast.makeText(this, "Login canceled", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.loginBtn.setOnClickListener(v -> {
            Intent intent = viewModel.login();
            loginLauncher.launch(intent);
        });

        binding.registerBtn.setOnClickListener(v -> {
            Intent intent = viewModel.register();
            loginLauncher.launch(intent);
        });

        viewModel.getAuthSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
