package com.example.smartdarzi.fragments;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.smartdarzi.R;
import com.google.android.material.appbar.MaterialToolbar;

public class ProfileSectionFragment extends Fragment {

    private static final String ARG_TITLE = "arg_title";
    private static final String ARG_BODY = "arg_body";

    public ProfileSectionFragment() {
        super();
    }

    public static ProfileSectionFragment newInstance(@NonNull String title, @NonNull String body) {
        ProfileSectionFragment fragment = new ProfileSectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_BODY, body);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        String title = getArguments() != null ? getArguments().getString(ARG_TITLE, "") : "";
        String body = getArguments() != null ? getArguments().getString(ARG_BODY, "") : "";

        LinearLayout root = new LinearLayout(requireContext());
        root.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.premium_bg));

        MaterialToolbar toolbar = new MaterialToolbar(requireContext());
        toolbar.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (56 * getResources().getDisplayMetrics().density)));
        toolbar.setTitle(title);
        toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent));
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
        toolbar.setNavigationOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        TextView heading = new TextView(requireContext());
        LinearLayout.LayoutParams headingParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        heading.setLayoutParams(headingParams);
        heading.setPadding(pad, pad, pad, 0);
        heading.setText(title);
        heading.setTextSize(22);
        heading.setTextColor(ContextCompat.getColor(requireContext(), R.color.premium_text_primary));

        TextView content = new TextView(requireContext());
        LinearLayout.LayoutParams bodyParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        content.setLayoutParams(bodyParams);
        content.setPadding(pad, (int) (10 * getResources().getDisplayMetrics().density), pad, pad);
        content.setText(body);
        content.setTextSize(14);
        content.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_600));

        root.addView(toolbar);
        root.addView(heading);
        root.addView(content);
        return root;
    }
}
