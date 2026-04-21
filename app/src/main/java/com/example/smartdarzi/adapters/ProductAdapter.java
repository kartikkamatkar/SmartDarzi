package com.example.smartdarzi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartdarzi.R;
import com.example.smartdarzi.models.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onAddToCartClick(Product product);
    }

    private final List<Product> products = new ArrayList<>();
    private final OnProductClickListener listener;

    public ProductAdapter(@NonNull OnProductClickListener listener) {
        this.listener = listener;
    }

    public void submitList(@NonNull List<Product> newProducts) {
        products.clear();
        products.addAll(newProducts);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        Context ctx = holder.itemView.getContext();

        holder.tvProductName.setText(product.getName());
        holder.tvProductDescription.setText(product.getCategoryName());
        holder.tvProductPrice.setText(
                ctx.getString(R.string.rupee_format, String.format(Locale.getDefault(), "%.0f", product.getPrice())));
        holder.tvProductRating.setText(
                ctx.getString(R.string.service_rating_label, product.getRating()));

        holder.btnAddToCart.setEnabled(product.getStockQuantity() > 0);
        holder.btnAddToCart.setText(product.getStockQuantity() > 0
                ? ctx.getString(R.string.book_now)
                : ctx.getString(R.string.out_of_stock));
        holder.btnAddToCart.setAlpha(product.getStockQuantity() > 0 ? 1f : 0.6f);

        // FIX: Use local drawable images instead of external Glide URL
        int imageRes = getDrawableForCategory(product.getCategoryName(), position);
        holder.ivProductImage.setImageResource(imageRes);

        holder.itemView.setOnClickListener(v -> listener.onProductClick(product));
        com.example.smartdarzi.utils.ClickAnimHelper.applySmoothClick(holder.itemView);
        holder.btnAddToCart.setOnClickListener(v -> listener.onAddToCartClick(product));
        com.example.smartdarzi.utils.ClickAnimHelper.applySmoothClick(holder.btnAddToCart);
    }

    /**
     * Maps product category to a local drawable resource.
     * Uses position to cycle through available variants.
     */
    private int getDrawableForCategory(String category, int position) {
        if (category == null || category.isEmpty()) {
            return getShirtDrawable(position);
        }
        switch (category.toLowerCase(Locale.getDefault())) {
            case "shirt":
            case "shirts":
                return getShirtDrawable(position);
            case "casual":
            case "casuals":
                int[] casuals = {R.drawable.casual1, R.drawable.casual2, R.drawable.casual3, R.drawable.casual4};
                return casuals[position % casuals.length];
            case "formal":
            case "formals":
                int[] formals = {R.drawable.formal1, R.drawable.formal2, R.drawable.formal3, R.drawable.formal4};
                return formals[position % formals.length];
            case "pant":
            case "pants":
            case "trouser":
            case "trousers":
            case "jeans":
            case "denim":
                int[] jeans = {R.drawable.jeans1, R.drawable.jeans2, R.drawable.jeans3, R.drawable.jeans4};
                return jeans[position % jeans.length];
            case "suit":
            case "suits":
                int[] suits = {R.drawable.suit1, R.drawable.blzer1, R.drawable.blzer2, R.drawable.blzer3};
                return suits[position % suits.length];
            case "blouse":
            case "blouses":
            case "designer blouse":
                int[] blouses = {R.drawable.designerblouse1, R.drawable.designerblouse2, R.drawable.designerblouse3, R.drawable.designerblouse4};
                return blouses[position % blouses.length];
            case "kurta":
            case "kurtas":
                int[] kurtas = {R.drawable.kurta_design1, R.drawable.kurta_design2, R.drawable.kurta_design3, R.drawable.kurta_design4};
                return kurtas[position % kurtas.length];
            case "lehenga":
            case "lehengas":
                int[] lehengas = {R.drawable.lehenga1, R.drawable.lehenga2, R.drawable.ai_portrait_bridal, R.drawable.ai_portrait_sharara};
                return lehengas[position % lehengas.length];
            case "saree":
            case "sari":
            case "sadi":
                return R.drawable.saree1;
            case "wedding wear":
            case "bridal":
            case "sherwani":
                int[] wedding = {R.drawable.sherwani1, R.drawable.ai_portrait_bridal, R.drawable.suit1, R.drawable.ai_portrait_bandhgala};
                return wedding[position % wedding.length];
            case "alterations":
                return R.drawable.alteration1;
            case "custom stitching":
            default:
                return getShirtDrawable(position);
        }
    }

    private int getShirtDrawable(int position) {
        int[] shirts = {R.drawable.shirt1, R.drawable.shirt2, R.drawable.shirt3, R.drawable.shirt4,
                R.drawable.shirt5, R.drawable.shirt6, R.drawable.shirt7, R.drawable.shirt8};
        return shirts[position % shirts.length];
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {

        final ImageView ivProductImage;
        final TextView tvProductName;
        final TextView tvProductDescription;
        final TextView tvProductPrice;
        final TextView tvProductRating;
        final Button btnAddToCart;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage    = itemView.findViewById(R.id.ivProductImage);
            tvProductName     = itemView.findViewById(R.id.tvProductName);
            tvProductDescription = itemView.findViewById(R.id.tvProductDescription);
            tvProductPrice    = itemView.findViewById(R.id.tvProductPrice);
            tvProductRating   = itemView.findViewById(R.id.tvProductRating);
            btnAddToCart      = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}
