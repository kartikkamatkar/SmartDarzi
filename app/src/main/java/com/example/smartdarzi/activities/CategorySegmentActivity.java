package com.example.smartdarzi.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartdarzi.R;
import com.example.smartdarzi.fragments.ProductListFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;

public class CategorySegmentActivity extends AppCompatActivity {

    public static final String EXTRA_SEGMENT = "extra_segment";

    private String currentSegment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_segment);

        MaterialToolbar toolbar = findViewById(R.id.toolbarSegment);
        Chip chipWomen = findViewById(R.id.chipSegmentWomen);
        Chip chipMen = findViewById(R.id.chipSegmentMen);
        Chip chipKids = findViewById(R.id.chipSegmentKids);

        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        String initial = getIntent() != null
                ? getIntent().getStringExtra(EXTRA_SEGMENT)
                : null;
        if (initial == null || initial.trim().isEmpty()) {
            initial = getString(R.string.segment_women);
        }

        currentSegment = normalizeSegment(initial);
        chipWomen.setOnClickListener(v -> switchSegment(getString(R.string.segment_women)));
        chipMen.setOnClickListener(v -> switchSegment(getString(R.string.segment_men)));
        chipKids.setOnClickListener(v -> switchSegment(getString(R.string.segment_kids)));

        syncChipSelection(chipWomen, chipMen, chipKids, currentSegment);
        updateToolbarTitle(toolbar, currentSegment);

        if (savedInstanceState == null) {
            renderSegment(currentSegment);
        }
    }

    private void switchSegment(@NonNull String rawSegment) {
        String normalized = normalizeSegment(rawSegment);
        if (normalized.equalsIgnoreCase(currentSegment)) {
            return;
        }
        currentSegment = normalized;

        Chip chipWomen = findViewById(R.id.chipSegmentWomen);
        Chip chipMen = findViewById(R.id.chipSegmentMen);
        Chip chipKids = findViewById(R.id.chipSegmentKids);
        MaterialToolbar toolbar = findViewById(R.id.toolbarSegment);

        syncChipSelection(chipWomen, chipMen, chipKids, currentSegment);
        updateToolbarTitle(toolbar, currentSegment);
        renderSegment(currentSegment);
    }

    private void syncChipSelection(@NonNull Chip chipWomen,
                                   @NonNull Chip chipMen,
                                   @NonNull Chip chipKids,
                                   @NonNull String segment) {
        chipWomen.setChecked(segment.equalsIgnoreCase(getString(R.string.segment_women)));
        chipMen.setChecked(segment.equalsIgnoreCase(getString(R.string.segment_men)));
        chipKids.setChecked(segment.equalsIgnoreCase(getString(R.string.segment_kids)));
    }

    private void updateToolbarTitle(@NonNull MaterialToolbar toolbar, @NonNull String segment) {
        toolbar.setTitle(getString(R.string.segment_title_with_name, segment));
    }

    private void renderSegment(@NonNull String segment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.segmentFragmentContainer, ProductListFragment.newInstance(segment))
                .commit();
    }

    @NonNull
    private String normalizeSegment(@NonNull String segment) {
        String lower = segment.trim().toLowerCase();
        if (lower.contains("men")) {
            return getString(R.string.segment_men);
        }
        if (lower.contains("kid")) {
            return getString(R.string.segment_kids);
        }
        return getString(R.string.segment_women);
    }
}

