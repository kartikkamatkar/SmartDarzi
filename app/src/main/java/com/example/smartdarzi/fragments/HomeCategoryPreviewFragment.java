package com.example.smartdarzi.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartdarzi.R;
import com.example.smartdarzi.adapters.HomeCategoryPreviewAdapter;
import com.example.smartdarzi.models.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeCategoryPreviewFragment extends Fragment {

    public interface Callbacks {
        void onHomeCategorySelected(@NonNull String categoryName);
        void onSeeAllCategoriesRequested();
    }

    private final List<Category> allCategories = new ArrayList<>();
    private HomeCategoryPreviewAdapter adapter;
    private RecyclerView rvCategories;
    private TextView tvEmptyState;

    public HomeCategoryPreviewFragment() {
        super(R.layout.fragment_home_category_preview);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvCategories = view.findViewById(R.id.rvHomeCategories);
        tvEmptyState = view.findViewById(R.id.tvHomeCategoriesEmpty);
        TextView tvSeeAll = view.findViewById(R.id.tvHomeSeeAllCategories);

        adapter = new HomeCategoryPreviewAdapter(category -> {
            Fragment parent = getParentFragment();
            if (parent instanceof Callbacks) {
                ((Callbacks) parent).onHomeCategorySelected(category.getName());
            }
        });

        rvCategories.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(adapter);

        if (tvSeeAll != null) {
            tvSeeAll.setOnClickListener(v -> {
                Fragment parent = getParentFragment();
                if (parent instanceof Callbacks) {
                    ((Callbacks) parent).onSeeAllCategoriesRequested();
                }
            });
        }

        allCategories.clear();
        allCategories.addAll(buildCategories());
        adapter.submitList(allCategories);
        updateEmptyState(allCategories.isEmpty());
    }

    public void filterCategories(@NonNull String query) {
        if (adapter == null) {
            return;
        }

        String normalized = query.trim().toLowerCase(Locale.getDefault());
        if (normalized.isEmpty()) {
            adapter.submitList(allCategories);
            updateEmptyState(allCategories.isEmpty());
            return;
        }

        List<Category> filtered = new ArrayList<>();
        for (Category category : allCategories) {
            if (category.getName().toLowerCase(Locale.getDefault()).contains(normalized)) {
                filtered.add(category);
            }
        }

        adapter.submitList(filtered);
        updateEmptyState(filtered.isEmpty());
    }

    private void updateEmptyState(boolean isEmpty) {
        if (tvEmptyState == null || rvCategories == null) {
            return;
        }
        tvEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        rvCategories.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private List<Category> buildCategories() {
        List<Category> list = new ArrayList<>();
        try {
            if (isAdded()) {
                list.add(new Category(1, getString(R.string.category_shirts),
                        R.drawable.ic_shirt_navy, R.color.category_color_shirts));
                list.add(new Category(2, getString(R.string.category_pants),
                        android.R.drawable.ic_menu_crop, R.color.category_color_pants));
                list.add(new Category(3, getString(R.string.category_suits),
                        android.R.drawable.ic_menu_agenda, R.color.category_color_suits));
                list.add(new Category(4, getString(R.string.category_kurtas),
                        R.drawable.ic_kurta_blue, R.color.category_color_kurtas));
                list.add(new Category(5, getString(R.string.category_wedding_wear),
                        android.R.drawable.ic_menu_gallery, R.color.category_color_wedding));
                list.add(new Category(6, getString(R.string.category_custom_stitching),
                        android.R.drawable.ic_menu_compass, R.color.category_color_custom));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}

