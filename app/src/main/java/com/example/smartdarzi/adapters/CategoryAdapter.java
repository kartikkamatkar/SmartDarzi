package com.example.smartdarzi.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartdarzi.R;
import com.example.smartdarzi.models.Category;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    private final List<Category> categories = new ArrayList<>();
    private final OnCategoryClickListener listener;

    public CategoryAdapter(@NonNull OnCategoryClickListener listener) {
        this.listener = listener;
    }

    public void submitList(@NonNull List<Category> newCategories) {
        categories.clear();
        categories.addAll(newCategories);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);

        // FIX: Use local drawable instead of broken Glide URL
        int imageRes = getDrawableForCategory(category.getName(), position);
        holder.ivCategoryImage.setImageResource(imageRes);

        holder.cardCategoryIconWrap.setCardBackgroundColor(
                ContextCompat.getColor(holder.itemView.getContext(), category.getCardColorRes()));
        holder.tvCategoryName.setText(category.getName());
        holder.itemView.setOnClickListener(v -> listener.onCategoryClick(category));
    }

    /**
     * Maps category name to a representative local drawable.
     * Category strings match DB seed: Shirts, Pants, Suits, Kurtas, Blouses, Lehengas, Wedding Wear, Alterations, Custom Stitching
     */
    private int getDrawableForCategory(String name, int position) {
        if (name == null || name.isEmpty()) return R.drawable.shirt1;

        switch (name.toLowerCase(Locale.getDefault())) {
            case "shirt": case "shirts": return R.drawable.shirt1;
            case "pant": case "pants": case "trouser": case "trousers": return R.drawable.jeans1;
            case "suit": case "suits": return R.drawable.formal1;
            case "kurta": case "kurtas": return R.drawable.kurta1;
            case "blouse": case "blouses": case "designer blouse": return R.drawable.designerblouse1;
            case "lehenga": case "lehengas": return R.drawable.designerblouse3;
            case "wedding wear": case "bridal": return R.drawable.designerblouse2;
            case "casual": case "casuals": return R.drawable.casual1;
            case "jeans": case "denim": return R.drawable.jeans1;
            case "formal": case "formals": return R.drawable.formal3;
            case "alterations": return R.drawable.shirt3;
            case "custom stitching": return R.drawable.shirt7;
            case "all": return R.drawable.shirt1;
            default:
                int[] fallback = {R.drawable.shirt1, R.drawable.casual1, R.drawable.formal1, R.drawable.jeans1,
                        R.drawable.designerblouse1, R.drawable.kurta1, R.drawable.casual2, R.drawable.formal3};
                return fallback[position % fallback.length];
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {

        final MaterialCardView cardCategoryIconWrap;
        final ImageView ivCategoryImage;
        final TextView tvCategoryName;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cardCategoryIconWrap = itemView.findViewById(R.id.cardCategoryIconWrap);
            ivCategoryImage = itemView.findViewById(R.id.ivCategoryImage);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}
