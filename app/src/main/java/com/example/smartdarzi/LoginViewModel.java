package com.example.smartdarzi;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartdarzi.models.User;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<LoginState> loginState = new MutableLiveData<>();

    public LoginViewModel() {
        loginState.setValue(new LoginState(LoginStatus.IDLE));
    }

    public LiveData<LoginState> getLoginState() {
        return loginState;
    }

    public void login(String email, String password) {
        loginState.setValue(new LoginState(LoginStatus.LOADING));

        // Simulate network call - Replace with actual repository call
        new Thread(() -> {
            try {
                Thread.sleep(1500); // Simulate network delay

                // Mock success/failure - Replace with actual authentication
                if (email.contains("@") && password.length() >= 6) {
                    User user = new User(1, "John Doe", email, "9999999999", "");
                    loginState.postValue(new LoginState(LoginStatus.SUCCESS, user));
                } else {
                    loginState.postValue(new LoginState(
                            LoginStatus.ERROR,
                            "Invalid email or password"
                    ));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                loginState.postValue(new LoginState(
                        LoginStatus.ERROR,
                        "Login failed. Please try again."
                ));
            }
        }).start();
    }
}

enum LoginStatus {
    IDLE, LOADING, SUCCESS, ERROR
}

class LoginState {
    private final LoginStatus status;
    private final User user;
    private final String message;

    public LoginState(LoginStatus status) {
        this(status, null, null);
    }

    public LoginState(LoginStatus status, User user) {
        this(status, user, null);
    }

    public LoginState(LoginStatus status, String message) {
        this(status, null, message);
    }

    public LoginState(LoginStatus status, User user, String message) {
        this.status = status;
        this.user = user;
        this.message = message;
    }

    public LoginStatus getStatus() { return status; }
    public User getUser() { return user; }
    public String getMessage() { return message; }
}