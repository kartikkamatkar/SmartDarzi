package com.example.smartdarzi.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartdarzi.R;
import com.google.android.material.appbar.MaterialToolbar;

public class HelpSupportFragment extends Fragment {

    public HelpSupportFragment() {
        super(R.layout.fragment_help_support);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbarHelpSupport);
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());

        populateFaq(view.findViewById(R.id.faq1), "How do I track my order?", "You can track your order status in the 'Orders' tab.");
        populateFaq(view.findViewById(R.id.faq2), "How do measurements work?", "You can request a tailor to visit or submit your own sizes in 'Measurements' section.");
        populateFaq(view.findViewById(R.id.faq3), "Can I cancel a booking?", "Yes, bookings can be cancelled before the tailor confirms or starts work.");
    }

    private void populateFaq(View root, String q, String a) {
        if (root == null) return;
        TextView tvQ = root.findViewById(R.id.tvQuestion);
        TextView tvA = root.findViewById(R.id.tvAnswer);
        if (tvQ != null) tvQ.setText(q);
        if (tvA != null) tvA.setText(a);
    }
}
