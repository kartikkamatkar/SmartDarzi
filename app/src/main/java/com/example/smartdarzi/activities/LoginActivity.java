package com.example.smartdarzi.activities;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartdarzi.R;
import com.example.smartdarzi.database.DatabaseHelper;
import com.example.smartdarzi.models.User;
import com.example.smartdarzi.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnSignIn;
    private ProgressBar pbLoading;
    private TextView tvRegisterLink;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupListeners();
    }

    private void initViews() {
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        pbLoading = findViewById(R.id.pbLoginLoading);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);
    }

    private void setupListeners() {
        tvRegisterLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        // Real-time email validation
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilEmail.setError(null);
            }
            @Override
            public void afterTextChanged(Editable s) {
                validateEmailLive(s.toString());
            }
        });

        // Real-time password validation
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilPassword.setError(null);
            }
            @Override
            public void afterTextChanged(Editable s) {
                validatePasswordLive(s.toString());
            }
        });

        btnSignIn.setOnClickListener(v -> attemptLogin());
    }

    private void validateEmailLive(String email) {
        if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.error_invalid_email));
        }
    }

    private void validatePasswordLive(String password) {
        if (!password.isEmpty() && password.length() < 6) {
            tilPassword.setError(getString(R.string.error_password_length));
        }
    }

    private boolean isValidForm() {
        boolean valid = true;
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            tilEmail.setError(getString(R.string.error_empty_field));
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.error_invalid_email));
            valid = false;
        }

        if (password.isEmpty()) {
            tilPassword.setError(getString(R.string.error_empty_field));
            valid = false;
        } else if (password.length() < 6) {
            tilPassword.setError(getString(R.string.error_password_length));
            valid = false;
        }

        return valid;
    }

    private void attemptLogin() {
        if (!isValidForm()) {
            shakeView(btnSignIn);
            return;
        }

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        setLoadingState(true);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            User user = dbHelper.authenticateUser(email, password);

            if (user != null) {
                sessionManager.createLoginSession(user.getId(), user.getName(), user.getEmail());
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            } else {
                setLoadingState(false);
                tilEmail.setError(getString(R.string.error_invalid_credentials));
                tilPassword.setError(getString(R.string.error_invalid_credentials));
                shakeView(btnSignIn);
                Toast.makeText(this, R.string.error_invalid_credentials, Toast.LENGTH_SHORT).show();
            }
        }, 1500);
    }

    private void setLoadingState(boolean isLoading) {
        btnSignIn.setText(isLoading ? "" : getString(R.string.sign_in));
        btnSignIn.setEnabled(!isLoading);
        pbLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        etEmail.setEnabled(!isLoading);
        etPassword.setEnabled(!isLoading);
    }

    private void shakeView(View view) {
        PropertyValuesHolder translationX = PropertyValuesHolder.ofFloat(
                View.TRANSLATION_X, 0, -10, 10, -10, 10, -10, 10, -10, 10, 0);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, translationX);
        animator.setDuration(250);
        animator.start();
    }
}
