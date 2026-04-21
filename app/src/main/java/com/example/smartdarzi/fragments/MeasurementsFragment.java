package com.example.smartdarzi.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartdarzi.R;
import com.example.smartdarzi.database.DatabaseHelper;
import com.example.smartdarzi.models.Measurement;
import com.example.smartdarzi.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class MeasurementsFragment extends Fragment {

    private TextInputEditText etChest, etWaist, etHip, etShoulder, etSleeve, etHeight;

    public MeasurementsFragment() {
        super(R.layout.fragment_measurements);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbarMeasurements);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
        toolbar.setNavigationOnClickListener(v -> {
            // FIX: if running inside MeasurementsActivity, finish the activity
            // If running embedded inside a fragment transaction (ProfileFragment), pop back stack
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else if (getActivity() != null) {
                getActivity().finish();
            }
        });

        etChest = view.findViewById(R.id.etGlobalChest);
        etWaist = view.findViewById(R.id.etGlobalWaist);
        etHip = view.findViewById(R.id.etGlobalHip);
        etShoulder = view.findViewById(R.id.etGlobalShoulder);
        etSleeve = view.findViewById(R.id.etGlobalSleeve);
        etHeight = view.findViewById(R.id.etGlobalHeight);
        MaterialButton btnSave = view.findViewById(R.id.btnSaveGlobalMeasurements);

        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        SessionManager sessionManager = new SessionManager(requireContext());
        int userId = sessionManager.getLoggedInUserId();

        Measurement latest = dbHelper.getLatestMeasurementForUser(userId);
        if (latest != null) {
            etChest.setText(latest.getChest());
            etWaist.setText(latest.getWaist());
            etHip.setText(latest.getHip());
            etShoulder.setText(latest.getShoulder());
            etSleeve.setText(latest.getSleeveLength());
            etHeight.setText(latest.getHeight());
        }

        btnSave.setOnClickListener(v -> {
            String chest = valueOf(etChest);
            String waist = valueOf(etWaist);
            String hip = valueOf(etHip);
            String shoulder = valueOf(etShoulder);
            String sleeve = valueOf(etSleeve);
            String height = valueOf(etHeight);

            if (chest.isEmpty() && waist.isEmpty() && hip.isEmpty()
                    && shoulder.isEmpty() && sleeve.isEmpty() && height.isEmpty()) {
                Snackbar.make(v, R.string.measurement_empty_error, Snackbar.LENGTH_SHORT).show();
                return;
            }

            Measurement existing = dbHelper.getLatestMeasurementForUser(userId);
            boolean success;
            if (existing != null) {
                Measurement updated = new Measurement(existing.getId(), userId, chest, waist, hip, shoulder, sleeve, height, "");
                success = dbHelper.updateMeasurement(updated);
            } else {
                Measurement created = new Measurement(0, userId, chest, waist, hip, shoulder, sleeve, height, "");
                success = dbHelper.insertMeasurement(created) != -1;
            }

            Snackbar.make(v, success ? R.string.measurement_saved : R.string.measurement_save_error, Snackbar.LENGTH_SHORT).show();
            if (success) {
                new android.os.Handler().postDelayed(() -> {
                    // FIX: Work in both Activity and Fragment contexts
                    if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                        getParentFragmentManager().popBackStack();
                    } else if (getActivity() != null) {
                        getActivity().finish();
                    }
                }, 900);
            }
        });
    }

    @NonNull
    private String valueOf(@NonNull TextInputEditText field) {
        return field.getText() == null ? "" : field.getText().toString().trim();
    }
}
