package com.example.smartdarzi.activities;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartdarzi.R;
import com.example.smartdarzi.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {

    private ImageView ivLogo;
    private TextView tvTitle;
    private TextView tvSubtitle;
    private View dot1, dot2, dot3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ivLogo = findViewById(R.id.ivSplashLogo);
        tvTitle = findViewById(R.id.tvSplashTitle);
        tvSubtitle = findViewById(R.id.tvSplashSubtitle);
        dot1 = findViewById(R.id.dot1);
        dot2 = findViewById(R.id.dot2);
        dot3 = findViewById(R.id.dot3);

        startAnimations();

        new Handler(Looper.getMainLooper()).postDelayed(this::routeUser, 2000);
    }

    private void startAnimations() {
        // Logo: fade in + scale up
        ivLogo.setScaleX(0.8f);
        ivLogo.setScaleY(0.8f);
        ivLogo.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(800)
                .setInterpolator(new OvershootInterpolator())
                .start();

        // Title: fade in with slight delay
        tvTitle.animate()
                .alpha(1f)
                .setStartDelay(200)
                .setDuration(800)
                .start();

        // Subtitle: fade in with more delay
        tvSubtitle.animate()
                .alpha(1f)
                .setStartDelay(400)
                .setDuration(800)
                .start();

        // Animated 3-dot pulse loop
        animateDot(dot1, 0);
        animateDot(dot2, 200);
        animateDot(dot3, 400);
    }

    private void animateDot(View dot, long delay) {
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.5f, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.5f, 1f);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0.5f, 1f, 0.5f);

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(dot, scaleX, scaleY, alpha);
        animator.setDuration(1000);
        animator.setStartDelay(delay);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.start();
    }

    private void routeUser() {
        SessionManager sessionManager = new SessionManager(this);
        Intent intent;

        if (sessionManager.isLoggedIn()) {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}
