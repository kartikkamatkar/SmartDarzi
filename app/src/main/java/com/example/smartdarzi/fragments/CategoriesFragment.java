package com.example.smartdarzi.fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartdarzi.R;
import com.example.smartdarzi.database.DatabaseHelper;
import com.example.smartdarzi.models.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CategoriesFragment extends Fragment {

    private static final String ARG_FILTER = "arg_filter";
    private static String pendingFilter;

    private final List<CategoryItem> categories = new ArrayList<>();
    private DatabaseHelper dbHelper;

    public CategoriesFragment() {
        super(R.layout.fragment_categories);
    }

    public static CategoriesFragment newInstance(@Nullable String initialFilter) {
        CategoriesFragment fragment = new CategoriesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FILTER, initialFilter);
        fragment.setArguments(args);
        return fragment;
    }

    public static void setPendingFilter(@Nullable String filter) {
        pendingFilter = filter;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DatabaseHelper(requireContext());
        RecyclerView rvCategories = view.findViewById(R.id.rvCategories);

        categories.clear();
        categories.add(new CategoryItem("Shirts", R.drawable.shirt1));
        categories.add(new CategoryItem("Pants", R.drawable.jeans1));
        categories.add(new CategoryItem("Suits", R.drawable.suit1));
        categories.add(new CategoryItem("Kurtas", R.drawable.kurta1));
        categories.add(new CategoryItem("Blouses", R.drawable.designerblouse1));
        categories.add(new CategoryItem("Lehengas", R.drawable.lehenga1));
        categories.add(new CategoryItem("Saree", R.drawable.saree1));
        categories.add(new CategoryItem("Wedding Wear", R.drawable.sherwani1));
        categories.add(new CategoryItem("Alterations", R.drawable.alteration1));
        categories.add(new CategoryItem("Custom Stitching", R.drawable.shirt8));

        rvCategories.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvCategories.setAdapter(new CategoriesAdapter(categories));

        String initialFilter = getArguments() != null ? getArguments().getString(ARG_FILTER) : null;
        String effectiveFilter = pendingFilter != null ? pendingFilter : initialFilter;
        pendingFilter = null;
        if (effectiveFilter != null && !effectiveFilter.trim().isEmpty()) {
            openProductList(effectiveFilter);
        }
    }

    private int getServiceCountForCategory(@NonNull String category) {
        int count = dbHelper.getServiceCountByCategory(category);
        return count == 0 ? 3 : count;
    }

    private void openProductList(@NonNull String categoryName) {
        getParentFragmentManager()
                .beginTransaction()
                .add(R.id.container, ProductListFragment.newInstance(categoryName), "PRODUCT_LIST")
                .addToBackStack("product_list")
                .commit();
    }

    private class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryVH> {

        private final List<CategoryItem> items;

        CategoriesAdapter(List<CategoryItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public CategoryVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_category_card, parent, false);
            return new CategoryVH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryVH holder, int position) {
            CategoryItem item = items.get(position);
            holder.tvCategoryName.setText(item.name);
            holder.tvCategoryCount.setText(getString(R.string.category_service_count, getServiceCountForCategory(item.name)));

            holder.ivCategoryBg.setImageResource(item.iconRes);
            holder.ivCategoryBg.setBackground(null);
            holder.ivCategoryBg.setPadding(0, 0, 0, 0);
            holder.ivCategoryBg.setScaleType(ImageView.ScaleType.CENTER_CROP);
            // Removed color filter to show real product images
            holder.ivCategoryBg.setColorFilter(null);

            holder.itemView.setOnClickListener(v -> openProductList(item.name));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class CategoryVH extends RecyclerView.ViewHolder {
            final ImageView ivCategoryBg;
            final TextView tvCategoryName;
            final TextView tvCategoryCount;

            CategoryVH(@NonNull View itemView) {
                super(itemView);
                ivCategoryBg = itemView.findViewById(R.id.ivCategoryBg);
                tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
                tvCategoryCount = itemView.findViewById(R.id.tvCategoryCount);
            }
        }
    }

    private static class CategoryItem {
        final String name;
        final int iconRes;

        CategoryItem(String name, int iconRes) {
            this.name = name;
            this.iconRes = iconRes;
        }
    }
}
