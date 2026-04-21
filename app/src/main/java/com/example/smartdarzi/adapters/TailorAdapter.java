package com.example.smartdarzi.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartdarzi.R;
import com.example.smartdarzi.models.TailorShop;
import com.example.smartdarzi.utils.CommonHeaderHelper;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TailorAdapter extends RecyclerView.Adapter<TailorAdapter.TailorViewHolder> implements Filterable {

    private final Context context;
    private final List<TailorShop> tailorShopsFull;
    private List<TailorShop> tailorShops;
    private final TailorListener listener;

    public interface TailorListener {
        void onFilterResult(boolean isEmpty);
    }

    public TailorAdapter(Context context, List<TailorShop> tailorShops, TailorListener listener) {
        this.context = context;
        this.tailorShops = new ArrayList<>(tailorShops);
        this.tailorShopsFull = new ArrayList<>(tailorShops);
        this.listener = listener;
    }

    @NonNull
    @Override
    public TailorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tailor_card, parent, false);
        return new TailorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TailorViewHolder holder, int position) {
        TailorShop shop = tailorShops.get(position);
        holder.tvTailorName.setText(shop.getName());
        holder.tvTailorAddress.setText(shop.getAddress());

        holder.btnCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + shop.getPhone()));
            context.startActivity(intent);
        });

        holder.btnLocation.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(shop.getMapQuery()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return tailorShops.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<TailorShop> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(tailorShopsFull);
                } else {
                    String pattern = constraint.toString().toLowerCase(Locale.getDefault()).trim();
                    for (TailorShop shop : tailorShopsFull) {
                        if (shop.getName().toLowerCase().contains(pattern) ||
                            shop.getAddress().toLowerCase().contains(pattern) ||
                            shop.getChowk().toLowerCase().contains(pattern)) {
                            filteredList.add(shop);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence constraint, FilterResults results) {
                tailorShops.clear();
                tailorShops.addAll((List) results.values);
                notifyDataSetChanged();
                if (listener != null) listener.onFilterResult(tailorShops.isEmpty());
            }
        };
    }

    static class TailorViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTailorName;
        final TextView tvTailorAddress;
        final MaterialButton btnCall;
        final MaterialButton btnLocation;

        TailorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTailorName = itemView.findViewById(R.id.tvTailorName);
            tvTailorAddress = itemView.findViewById(R.id.tvTailorAddress);
            btnCall = itemView.findViewById(R.id.btnCallTailor);
            btnLocation = itemView.findViewById(R.id.btnLocationTailor);
        }
    }
}
