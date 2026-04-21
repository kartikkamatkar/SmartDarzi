package com.example.smartdarzi.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartdarzi.R;
import com.example.smartdarzi.models.Category;

import java.util.ArrayList;
import java.util.List;

public class HomeCategoryPreviewAdapter extends RecyclerView.Adapter<HomeCategoryPreviewAdapter.HomeCategoryViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(@NonNull Category category);
    }

    private final List<Category> categories = new ArrayList<>();
    private final OnCategoryClickListener listener;

    public HomeCategoryPreviewAdapter(@NonNull OnCategoryClickListener listener) {
        this.listener = listener;
    }

    public void submitList(@NonNull List<Category> newItems) {
        categories.clear();
        categories.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HomeCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home_category_preview, parent, false);
        return new HomeCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeCategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        
        holder.tvCategoryTitle.setText(category.getName());
        
        // Map category names to emojis for consistency with other parts of the app
        String emoji = "⭐";
        switch (category.getName().toLowerCase()) {
            case "shirt": case "shirts": emoji = "👕"; break;
            case "pant": case "pants": case "trouser": emoji = "👖"; break;
            case "suit": case "suits": case "coat": emoji = "🧥"; break;
            case "kurta": case "kurtas": emoji = "🩱"; break;
            case "blouse": case "blouses": emoji = "👚"; break;
            case "lehenga": case "lehengas": emoji = "👘"; break;
            case "alteration": case "alterations": emoji = "✂️"; break;
        }
        
        holder.tvCategoryIcon.setText(emoji);
        holder.tvCategoryIcon.setBackgroundResource(R.drawable.bg_category_icon);
        
        holder.itemView.setOnClickListener(v -> listener.onCategoryClick(category));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class HomeCategoryViewHolder extends RecyclerView.ViewHolder {

        private final View cardCategory;
        private final TextView tvCategoryIcon;
        private final TextView tvCategoryTitle;

        HomeCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cardCategory = itemView.findViewById(R.id.cardHomeCategory);
            tvCategoryIcon = itemView.findViewById(R.id.tvHomeCategoryIcon);
            tvCategoryTitle = itemView.findViewById(R.id.tvHomeCategoryTitle);
        }
    }
}
