package com.example.smartdarzi.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartdarzi.R;
import com.google.android.material.appbar.MaterialToolbar;

public class PrivacyPolicyFragment extends Fragment {

    public PrivacyPolicyFragment() {
        super(R.layout.fragment_privacy_policy);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbarPrivacy);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
        }
    }
}
