package com.example.smartdarzi.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartdarzi.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.snackbar.Snackbar;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        super(R.layout.fragment_settings);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbarSettings);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());

        MaterialSwitch switchNotifications = view.findViewById(R.id.switchNotifications);
        MaterialSwitch switchDarkMode = view.findViewById(R.id.switchDarkMode);

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Snackbar.make(view, isChecked ? "Notifications enabled" : "Notifications disabled", Snackbar.LENGTH_SHORT).show();
        });

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Snackbar.make(view, "Dark mode feature coming soon", Snackbar.LENGTH_SHORT).show();
        });
    }
}
