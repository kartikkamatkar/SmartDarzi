package com.example.smartdarzi.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartdarzi.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class AuthBenefitsFragment extends Fragment {

    private static final String ARG_BENEFITS_ARRAY_RES = "arg_benefits_array_res";

    public AuthBenefitsFragment() {
        super(R.layout.fragment_auth_benefits);
    }

    @NonNull
    public static AuthBenefitsFragment newInstance(@ArrayRes int benefitsArrayRes) {
        AuthBenefitsFragment fragment = new AuthBenefitsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_BENEFITS_ARRAY_RES, benefitsArrayRes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ChipGroup chipGroup = view.findViewById(R.id.chipGroupDynamicBenefits);
        if (chipGroup == null) {
            return;
        }

        int arrayRes = R.array.auth_benefits_login;
        Bundle args = getArguments();
        if (args != null) {
            arrayRes = args.getInt(ARG_BENEFITS_ARRAY_RES, R.array.auth_benefits_login);
        }

        chipGroup.removeAllViews();
        String[] benefits = getResources().getStringArray(arrayRes);
        for (String benefit : benefits) {
            Chip chip = new Chip(requireContext(), null, com.google.android.material.R.style.Widget_Material3_Chip_Assist);
            chip.setText(benefit);
            chip.setClickable(false);
            chip.setCheckable(false);
            chip.setChipBackgroundColorResource(R.color.surface);
            chip.setChipStrokeWidth(1f);
            chip.setChipStrokeColorResource(R.color.app_border);
            chipGroup.addView(chip);
        }
    }
}


