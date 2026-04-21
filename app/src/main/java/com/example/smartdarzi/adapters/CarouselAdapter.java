package com.example.smartdarzi.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartdarzi.R;
import com.example.smartdarzi.models.CarouselItem;

import java.util.List;

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder> {

    private final List<CarouselItem> items;

    // Banner images from local drawables — visually diverse mix
    private static final int[] BANNER_IMAGES = {
            R.drawable.designerblouse1,
            R.drawable.formal1,
            R.drawable.casual1,
            R.drawable.shirt1,
            R.drawable.jeans1,
            R.drawable.designerblouse3,
            R.drawable.shirt5,
            R.drawable.casual3
    };

    public CarouselAdapter(List<CarouselItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public CarouselViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CarouselViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_carousel, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselViewHolder holder, int position) {
        CarouselItem item = items.get(position % items.size());
        holder.bind(item, position);
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE; // Infinite carousel
    }

    class CarouselViewHolder extends RecyclerView.ViewHolder {
        private final ImageView carouselImage;
        private final TextView carouselTitle;
        private final TextView carouselSubtitle;

        CarouselViewHolder(@NonNull android.view.View itemView) {
            super(itemView);
            carouselImage = itemView.findViewById(R.id.carouselImage);
            carouselTitle = itemView.findViewById(R.id.carouselTitle);
            carouselSubtitle = itemView.findViewById(R.id.carouselSubtitle);
        }

        void bind(CarouselItem item, int position) {
            // FIX: Use local drawable instead of broken Glide URL
            int imageRes = BANNER_IMAGES[position % BANNER_IMAGES.length];
            carouselImage.setImageResource(imageRes);
            carouselImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

            carouselTitle.setText(item.getTitle());
            carouselSubtitle.setText(item.getSubtitle());
        }
    }
}
