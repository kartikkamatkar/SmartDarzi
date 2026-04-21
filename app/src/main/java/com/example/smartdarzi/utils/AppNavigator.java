package com.example.smartdarzi.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.smartdarzi.activities.LoginActivity;
import com.example.smartdarzi.activities.MainActivity;
import com.example.smartdarzi.activities.RegisterActivity;

public final class AppNavigator {

    private AppNavigator() {
    }

    public static void openMainClearingTask(@NonNull Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void openLoginClearingTask(@NonNull Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void openRegister(@NonNull Activity activity) {
        activity.startActivity(new Intent(activity, RegisterActivity.class));
    }

    public static void returnToLogin(@NonNull Activity activity) {
        if (activity.isTaskRoot()) {
            openLoginClearingTask(activity);
        }
        activity.finish();
    }
}

