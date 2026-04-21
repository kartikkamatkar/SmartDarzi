package com.example.smartdarzi.activities;

import android.animation.ArgbEvaluator;
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
import androidx.core.content.ContextCompat;

import com.example.smartdarzi.R;
import com.example.smartdarzi.database.DatabaseHelper;
import com.example.smartdarzi.models.User;
import com.example.smartdarzi.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilFullName, tilPhone, tilEmail, tilCity, tilPassword, tilConfirmPassword;
    private TextInputEditText etFullName, etPhone, etEmail, etCity, etPassword, etConfirmPassword;
    private MaterialCheckBox cbTerms;
    private MaterialButton btnRegister;
    private ProgressBar pbLoading;
    private TextView tvLoginLink, tvPasswordStrengthLabel;
    private LinearProgressIndicator progressPasswordStrength;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private int currentColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        currentColor = ContextCompat.getColor(this, R.color.divider);

        initViews();
        setupListeners();
    }

    private void initViews() {
        tilFullName = findViewById(R.id.tilFullName);
        tilPhone = findViewById(R.id.tilPhone);
        tilEmail = findViewById(R.id.tilEmailRegister);
        tilCity = findViewById(R.id.tilCity);
        tilPassword = findViewById(R.id.tilPasswordRegister);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);

        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmailRegister);
        etCity = findViewById(R.id.etCity);
        etPassword = findViewById(R.id.etPasswordRegister);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        cbTerms = findViewById(R.id.cbTerms);
        btnRegister = findViewById(R.id.btnRegister);
        pbLoading = findViewById(R.id.pbRegisterLoading);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        progressPasswordStrength = findViewById(R.id.progressPasswordStrength);
        tvPasswordStrengthLabel = findViewById(R.id.tvPasswordStrengthLabel);
    }

    private void setupListeners() {
        tvLoginLink.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        btnRegister.setOnClickListener(v -> attemptRegister());

        // Live validation clears warnings
        etFullName.addTextChangedListener(new SimpleTextWatcher(tilFullName));
        etCity.addTextChangedListener(new SimpleTextWatcher(tilCity));

        etPhone.addTextChangedListener(new SimpleTextWatcher(tilPhone) {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && s.length() < 10) {
                    tilPhone.setError(getString(R.string.error_phone_length));
                }
            }
        });

        etEmail.addTextChangedListener(new SimpleTextWatcher(tilEmail) {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && !Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
                    tilEmail.setError(getString(R.string.error_invalid_email));
                }
            }
        });

        etConfirmPassword.addTextChangedListener(new SimpleTextWatcher(tilConfirmPassword) {
            @Override
            public void afterTextChanged(Editable s) {
                String pass = etPassword.getText().toString();
                if (!s.toString().equals(pass) && s.length() > 0) {
                    tilConfirmPassword.setError(getString(R.string.error_password_match));
                }
            }
        });

        etPassword.addTextChangedListener(new SimpleTextWatcher(tilPassword) {
            @Override
            public void afterTextChanged(Editable s) {
                calculatePasswordStrength(s.toString());
            }
        });

        cbTerms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                cbTerms.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            }
        });
    }

    private void calculatePasswordStrength(String password) {
        int score = 0;
        int targetColor;
        String label;

        if (password.length() >= 1) score += 25;
        if (password.length() >= 6) score += 25;
        if (password.length() >= 8 && password.matches(".*[0-9].*")) score += 25;
        if (password.length() >= 10 && password.matches(".*[!@#$%^&*].*")) score += 25;

        // Progress Animation
        ObjectAnimator progressAnim = ObjectAnimator.ofInt(
                progressPasswordStrength, "progress",
                progressPasswordStrength.getProgress(), score);
        progressAnim.setDuration(300);
        progressAnim.start();

        if (score == 0) {
            targetColor = ContextCompat.getColor(this, R.color.divider);
            label = "Password Strength";
        } else if (score <= 25) {
            targetColor = ContextCompat.getColor(this, R.color.strength_weak);
            label = "Weak";
        } else if (score <= 50) {
            targetColor = ContextCompat.getColor(this, R.color.strength_fair);
            label = "Fair";
        } else if (score <= 75) {
            targetColor = ContextCompat.getColor(this, R.color.strength_good);
            label = "Good";
        } else {
            targetColor = ContextCompat.getColor(this, R.color.strength_strong);
            label = "Strong";
        }

        // Color Animation
        ObjectAnimator colorAnim = ObjectAnimator.ofInt(
                progressPasswordStrength, "indicatorColor", currentColor, targetColor);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setDuration(300);
        colorAnim.start();

        currentColor = targetColor;
        tvPasswordStrengthLabel.setText(label);
        tvPasswordStrengthLabel.setTextColor(targetColor);
    }

    private boolean isValidForm() {
        boolean valid = true;

        if (etFullName.getText().toString().trim().isEmpty()) {
            tilFullName.setError(getString(R.string.error_empty_field));
            valid = false;
        }
        if (etCity.getText().toString().trim().isEmpty()) {
            tilCity.setError(getString(R.string.error_empty_field));
            valid = false;
        }

        String phone = etPhone.getText().toString().trim();
        if (phone.isEmpty()) {
            tilPhone.setError(getString(R.string.error_empty_field));
            valid = false;
        } else if (phone.length() != 10) {
            tilPhone.setError(getString(R.string.error_phone_length));
            valid = false;
        }

        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            tilEmail.setError(getString(R.string.error_empty_field));
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.error_invalid_email));
            valid = false;
        }

        String password = etPassword.getText().toString();
        String confirm = etConfirmPassword.getText().toString();

        if (password.isEmpty()) {
            tilPassword.setError(getString(R.string.error_empty_field));
            valid = false;
        } else if (password.length() < 6) {
            tilPassword.setError(getString(R.string.error_password_length));
            valid = false;
        }

        if (confirm.isEmpty()) {
            tilConfirmPassword.setError(getString(R.string.error_empty_field));
            valid = false;
        } else if (!password.equals(confirm)) {
            tilConfirmPassword.setError(getString(R.string.error_password_match));
            valid = false;
        }

        if (!cbTerms.isChecked()) {
            cbTerms.setTextColor(ContextCompat.getColor(this, R.color.error));
            Toast.makeText(this, R.string.error_terms, Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }

    private void attemptRegister() {
        if (!isValidForm()) {
            shakeView(btnRegister);
            return;
        }

        setLoadingState(true);

        User newUser = new User(
            0,
            etFullName.getText().toString().trim(),
            etEmail.getText().toString().trim(),
            etPhone.getText().toString().trim(),
            etPassword.getText().toString()
        );

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            boolean success = dbHelper.addUser(newUser);

            if (success) {
                User loggedInUser = dbHelper.authenticateUser(newUser.getEmail(), newUser.getPassword());
                if (loggedInUser != null) {
                    sessionManager.createLoginSession(
                            loggedInUser.getId(),
                            loggedInUser.getName(),
                            loggedInUser.getEmail());

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                } else {
                    // This should ideally never happen if addUser was successful
                    setLoadingState(false);
                    Toast.makeText(this, "Registration successful, please login", Toast.LENGTH_SHORT).show();
                    finish(); 
                }
            } else {
                setLoadingState(false);
                Toast.makeText(this, "Email or Phone already registered", Toast.LENGTH_LONG).show();
                shakeView(btnRegister);
            }
        }, 1500);
    }

    private void setLoadingState(boolean isLoading) {
        btnRegister.setText(isLoading ? "" : getString(R.string.register));
        btnRegister.setEnabled(!isLoading);
        pbLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void shakeView(View view) {
        PropertyValuesHolder translationX = PropertyValuesHolder.ofFloat(
                View.TRANSLATION_X, 0, -10, 10, -10, 10, -10, 10, -10, 10, 0);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, translationX);
        animator.setDuration(250);
        animator.start();
    }

    // Concrete SimpleTextWatcher helper class
    private class SimpleTextWatcher implements TextWatcher {
        private final TextInputLayout layout;

        SimpleTextWatcher(TextInputLayout layout) {
            this.layout = layout;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            layout.setError(null);
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }
}
