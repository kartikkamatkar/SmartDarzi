package com.example.smartdarzi.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartdarzi.R;
import com.example.smartdarzi.models.DesignItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DesignItemAdapter extends RecyclerView.Adapter<DesignItemAdapter.DesignViewHolder> {

    private final List<DesignItem> items = new ArrayList<>();

    public void submitList(@NonNull List<DesignItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    public void filterByQuery(@NonNull List<DesignItem> source, @NonNull String query) {
        if (query.isEmpty()) {
            submitList(source);
            return;
        }

        List<DesignItem> filtered = new ArrayList<>();
        String lower = query.toLowerCase(Locale.getDefault());
        for (DesignItem item : source) {
            if (item.getName().toLowerCase(Locale.getDefault()).contains(lower)) {
                filtered.add(item);
            }
        }
        submitList(filtered);
    }

    @NonNull
    @Override
    public DesignViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_design, parent, false);
        return new DesignViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DesignViewHolder holder, int position) {
        DesignItem item = items.get(position);

        // FIX: Use local drawable from getImageResId() instead of external Glide URL
        if (item.getImageResId() != 0) {
            holder.ivDesignImage.setImageResource(item.getImageResId());
        }

        holder.tvDesignName.setText(item.getName());
        holder.tvDesignPrice.setText(String.format(Locale.getDefault(), "Rs. %.0f", item.getPrice()));
        holder.tvDesignRating.setText(holder.itemView.getContext().getString(R.string.design_rating, item.getRating()));

        if (item.getMatchReason() != null) {
            holder.tvDesignMatchReason.setVisibility(View.VISIBLE);
            holder.tvDesignMatchReason.setText(item.getMatchReason());
        } else {
            holder.tvDesignMatchReason.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class DesignViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivDesignImage;
        private final TextView tvDesignName;
        private final TextView tvDesignMatchReason;
        private final TextView tvDesignPrice;
        private final TextView tvDesignRating;

        DesignViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDesignImage = itemView.findViewById(R.id.ivDesignImage);
            tvDesignName = itemView.findViewById(R.id.tvDesignName);
            tvDesignMatchReason = itemView.findViewById(R.id.tvDesignMatchReason);
            tvDesignPrice = itemView.findViewById(R.id.tvDesignPrice);
            tvDesignRating = itemView.findViewById(R.id.tvDesignRating);
        }
    }
}
