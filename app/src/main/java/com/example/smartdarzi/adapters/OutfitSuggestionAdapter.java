package com.example.smartdarzi.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartdarzi.R;
import com.example.smartdarzi.models.OutfitSuggestion;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OutfitSuggestionAdapter extends RecyclerView.Adapter<OutfitSuggestionAdapter.OutfitViewHolder> {

    private final List<OutfitSuggestion> items = new ArrayList<>();

    // Map outfit names to local drawable resources
    private static final int[] OUTFIT_IMAGES = {
            R.drawable.ai_portrait_princess,
            R.drawable.ai_portrait_kurti,
            R.drawable.ai_portrait_bandhgala,
            R.drawable.ai_portrait_formal,
            R.drawable.ai_portrait_bridal,
            R.drawable.ai_portrait_sharara,
            R.drawable.ai_portrait_anarkali,
            R.drawable.ai_portrait_gown
    };

    public void submitList(@NonNull List<OutfitSuggestion> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OutfitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_design, parent, false);
        return new OutfitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OutfitViewHolder holder, int position) {
        OutfitSuggestion item = items.get(position);

        // FIX: Use local drawable instead of external Glide URL
        int imageRes = OUTFIT_IMAGES[position % OUTFIT_IMAGES.length];
        holder.ivOutfitImage.setImageResource(imageRes);

        holder.tvOutfitTitle.setText(item.getTitle());
        holder.tvOutfitSubtitle.setText(item.getSubtitle());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class OutfitViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivOutfitImage;
        final TextView tvOutfitTitle;
        final TextView tvOutfitSubtitle;

        OutfitViewHolder(@NonNull View itemView) {
            super(itemView);
            ivOutfitImage = itemView.findViewById(R.id.ivDesignImage);
            tvOutfitTitle = itemView.findViewById(R.id.tvDesignName);
            tvOutfitSubtitle = itemView.findViewById(R.id.tvDesignPrice);
        }
    }
}
