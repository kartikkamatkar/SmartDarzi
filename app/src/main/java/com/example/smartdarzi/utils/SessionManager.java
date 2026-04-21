package com.example.smartdarzi.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.smartdarzi.models.User;
import java.util.HashMap;

public class SessionManager {
    private static final String PREF_NAME = "darzi_session";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";

    public static final String KEY_ID = "user_id";
    public static final String KEY_NAME = "user_name";
    public static final String KEY_EMAIL = "user_email";

    private final SharedPreferences preferences;

    public SessionManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveUserSession(User user) {
        preferences.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putInt(KEY_USER_ID, user.getId())
                .putString(KEY_USER_NAME, user.getFullName())
                .putString(KEY_USER_EMAIL, user.getEmail())
                .apply();
    }

    public void createLoginSession(int id, String name, String email) {
        preferences.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putInt(KEY_USER_ID, id)
                .putString(KEY_USER_NAME, name)
                .putString(KEY_USER_EMAIL, email)
                .apply();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_ID, String.valueOf(preferences.getInt(KEY_USER_ID, -1)));
        user.put(KEY_NAME, preferences.getString(KEY_USER_NAME, ""));
        user.put(KEY_EMAIL, preferences.getString(KEY_USER_EMAIL, ""));
        return user;
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public int getLoggedInUserId() {
        return preferences.getInt(KEY_USER_ID, -1);
    }

    public String getLoggedInUserName() {
        return preferences.getString(KEY_USER_NAME, "Guest");
    }

    public void clearSession() {
        preferences.edit().clear().apply();
    }
    
    public void logoutUser() {
        clearSession();
    }
}
