package com.example.smartdarzi.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartdarzi.R;
import com.example.smartdarzi.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;

public class EditProfileFragment extends Fragment {

    public EditProfileFragment() {
        super(R.layout.fragment_edit_profile);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbarEditProfile);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());

        SessionManager sessionManager = new SessionManager(requireContext());
        HashMap<String, String> userDetails = sessionManager.getUserDetails();

        TextInputEditText etName = view.findViewById(R.id.etProfileName);
        TextInputEditText etEmail = view.findViewById(R.id.etProfileEmail);
        TextInputEditText etPhone = view.findViewById(R.id.etProfilePhone);
        MaterialButton btnSave = view.findViewById(R.id.btnSaveProfile);

        if (userDetails.get(SessionManager.KEY_NAME) != null) {
            etName.setText(userDetails.get(SessionManager.KEY_NAME));
        }
        if (userDetails.get(SessionManager.KEY_EMAIL) != null) {
            etEmail.setText(userDetails.get(SessionManager.KEY_EMAIL));
        }

        btnSave.setOnClickListener(v -> {
            String name = etName.getText() != null ? etName.getText().toString() : "";
            String email = etEmail.getText() != null ? etEmail.getText().toString() : "";
            if (!name.isEmpty() && !email.isEmpty()) {
                sessionManager.createLoginSession(
                        userDetails.get(SessionManager.KEY_ID) != null ? Integer.parseInt(userDetails.get(SessionManager.KEY_ID)) : 1,
                        name,
                        email
                );
                Snackbar.make(view, "Profile updated successfully!", Snackbar.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStack();
            } else {
                Snackbar.make(view, "Name and Email cannot be empty.", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}
