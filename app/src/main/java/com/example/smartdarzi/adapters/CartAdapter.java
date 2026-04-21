package com.example.smartdarzi.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartdarzi.R;
import com.example.smartdarzi.models.CartModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    // ── Listener ──────────────────────────────────────────────────────────────
    public interface CartListener {
        void onQuantityIncreased(CartModel item, int position);
        void onQuantityDecreased(CartModel item, int position);
        void onItemRemoved(CartModel item, int position);
    }

    // ── State ─────────────────────────────────────────────────────────────────
    private final List<CartModel> items = new ArrayList<>();
    private final CartListener listener;

    public CartAdapter(@NonNull CartListener listener) {
        this.listener = listener;
    }

    // ── Public API ────────────────────────────────────────────────────────────
    public void submitList(@NonNull List<CartModel> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    public void updateItem(int position, int newQuantity) {
        if (position < 0 || position >= items.size()) return;
        items.get(position).setQuantity(newQuantity);
        notifyItemChanged(position);
    }

    public void removeItem(int position) {
        if (position < 0 || position >= items.size()) return;
        items.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, items.size() - position);
    }

    public double calculateTotal() {
        double total = 0;
        for (CartModel item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    // ── RecyclerView.Adapter ──────────────────────────────────────────────────
    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartModel item = items.get(position);

        holder.tvProductName.setText(item.getProductName());

        // FIX: Use local drawable instead of external Glide URL
        int imageRes = getDrawableForProduct(item.getProductName(), position);
        holder.ivProductImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.ivProductImage.setImageResource(imageRes);

        holder.tvUnitPrice.setText(
                String.format(Locale.getDefault(), "Rs. %.0f / unit", item.getUnitPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        holder.tvSubtotal.setText(
                String.format(Locale.getDefault(), "Rs. %.0f", item.getSubtotal()));

        holder.btnDecrease.setEnabled(item.getQuantity() > 1);
        holder.btnDecrease.setAlpha(item.getQuantity() > 1 ? 1f : 0.4f);

        holder.btnIncrease.setOnClickListener(v -> {
            int adapterPos = holder.getBindingAdapterPosition();
            if (adapterPos != RecyclerView.NO_POSITION) {
                listener.onQuantityIncreased(items.get(adapterPos), adapterPos);
            }
        });

        holder.btnDecrease.setOnClickListener(v -> {
            int adapterPos = holder.getBindingAdapterPosition();
            if (adapterPos != RecyclerView.NO_POSITION) {
                listener.onQuantityDecreased(items.get(adapterPos), adapterPos);
            }
        });

        holder.ibtnRemove.setOnClickListener(v -> {
            int adapterPos = holder.getBindingAdapterPosition();
            if (adapterPos != RecyclerView.NO_POSITION) {
                listener.onItemRemoved(items.get(adapterPos), adapterPos);
            }
        });
    }

    /**
     * Maps product name to a local drawable. Tries to match keywords in the name
     * to pick the right category of image.
     */
    private int getDrawableForProduct(String productName, int position) {
        if (productName == null) return R.drawable.shirt1;

        String lower = productName.toLowerCase(Locale.getDefault());
        if (lower.contains("casual")) {
            int[] casuals = {R.drawable.casual1, R.drawable.casual2, R.drawable.casual3, R.drawable.casual4};
            return casuals[position % casuals.length];
        } else if (lower.contains("suit") || lower.contains("formal")) {
            int[] suits = {R.drawable.suit1, R.drawable.formal1, R.drawable.formal2, R.drawable.formal3};
            return suits[position % suits.length];
        } else if (lower.contains("jean") || lower.contains("denim")) {
            int[] jeans = {R.drawable.jeans1, R.drawable.jeans2, R.drawable.jeans3, R.drawable.jeans4};
            return jeans[position % jeans.length];
        } else if (lower.contains("lehenga") || lower.contains("bridal")) {
            int[] bridal = {R.drawable.lehenga1, R.drawable.lehenga2, R.drawable.sherwani1, R.drawable.designerblouse1};
            return bridal[position % bridal.length];
        } else if (lower.contains("blouse")) {
            int[] blouses = {R.drawable.designerblouse1, R.drawable.designerblouse2, R.drawable.designerblouse3, R.drawable.designerblouse4};
            return blouses[position % blouses.length];
        } else if (lower.contains("saree") || lower.contains("sari") || lower.contains("sadi")) {
            return R.drawable.saree1;
        } else if (lower.contains("kurta")) {
            int[] kurtas = {R.drawable.kurta_design1, R.drawable.kurta_design2, R.drawable.kurta_design3, R.drawable.kurta_design4};
            return kurtas[position % kurtas.length];
        } else {
            int[] shirts = {R.drawable.shirt1, R.drawable.shirt2, R.drawable.shirt3, R.drawable.shirt4,
                    R.drawable.shirt5, R.drawable.shirt6, R.drawable.shirt7, R.drawable.shirt8};
            return shirts[position % shirts.length];
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // ── ViewHolder ────────────────────────────────────────────────────────────
    static class CartViewHolder extends RecyclerView.ViewHolder {
        final TextView tvProductName;
        final TextView tvUnitPrice;
        final TextView tvQuantity;
        final TextView tvSubtotal;
        final android.widget.Button btnDecrease;
        final android.widget.Button btnIncrease;
        final ImageButton ibtnRemove;
        final ImageView ivProductImage;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvCartProductName);
            tvUnitPrice   = itemView.findViewById(R.id.tvCartUnitPrice);
            tvQuantity    = itemView.findViewById(R.id.tvCartQuantity);
            tvSubtotal    = itemView.findViewById(R.id.tvCartSubtotal);
            btnDecrease   = itemView.findViewById(R.id.btnDecreaseQty);
            btnIncrease   = itemView.findViewById(R.id.btnIncreaseQty);
            ibtnRemove    = itemView.findViewById(R.id.ibtnRemoveCartItem);
            ivProductImage = itemView.findViewById(R.id.ivCartProductImage);
        }
    }
}
