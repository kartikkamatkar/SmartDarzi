package com.example.smartdarzi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartdarzi.R;
import com.example.smartdarzi.models.Service;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> implements Filterable {

    private final Context context;
    private final List<Service> servicesFull;
    private List<Service> services = new ArrayList<>();
    private final OnServiceClickListener listener;

    public interface OnServiceClickListener {
        void onServiceClick(Service service);
    }

    public ServiceAdapter(Context context, List<Service> services, OnServiceClickListener listener) {
        this.context = context;
        this.listener = listener;
        if (services != null) {
            this.services.addAll(services);
            this.servicesFull = new ArrayList<>(services);
        } else {
            this.servicesFull = new ArrayList<>();
        }
    }

    public void submitList(@NonNull List<Service> newServices) {
        services.clear();
        services.addAll(newServices);
        servicesFull.clear();
        servicesFull.addAll(newServices);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_grid, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = services.get(position);
        holder.tvServiceName.setText(service.getName());
        holder.tvServiceCategory.setText(service.getCategory());
        holder.tvServiceRating.setText(context.getString(R.string.service_rating_label, service.getRating()));
        holder.tvServicePrice.setText(String.format(Locale.getDefault(), "₹%.0f", service.getPrice()));

        // FIX: Use local drawable instead of broken Glide URL
        int imageRes = getDrawableForCategory(service.getCategory(), position);
        holder.ivServiceImage.setImageResource(imageRes);

        holder.btnBookService.setOnClickListener(v -> {
            if (listener != null) {
                listener.onServiceClick(service);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onServiceClick(service);
            }
        });
    }

    /**
     * Maps service category to a local drawable resource.
     * Category strings come from DB seed: Shirts, Pants, Suits, Kurtas, Blouses, Lehengas, Wedding Wear, Alterations, Custom Stitching
     */
    private int getDrawableForCategory(String category, int position) {
        if (category == null || category.isEmpty()) {
            return getShirtDrawable(position);
        }
        switch (category.toLowerCase(Locale.getDefault())) {
            case "shirt":
            case "shirts":
                return getShirtDrawable(position);
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
            case "formal":
            case "formals":
                int[] suits = {R.drawable.suit1, R.drawable.formal1, R.drawable.formal2, R.drawable.formal3};
                return suits[position % suits.length];
            case "kurta":
            case "kurtas":
                int[] kurtas = {R.drawable.kurta_design1, R.drawable.kurta_design2, R.drawable.kurta_design3, R.drawable.kurta_design4};
                return kurtas[position % kurtas.length];
            case "blouse":
            case "blouses":
            case "designer blouse":
                int[] blouses = {R.drawable.designerblouse1, R.drawable.designerblouse2, R.drawable.designerblouse3, R.drawable.designerblouse4};
                return blouses[position % blouses.length];
            case "saree":
            case "sari":
            case "sadi":
                return R.drawable.saree1;
            case "lehenga":
            case "lehengas":
                int[] lehengas = {R.drawable.lehenga1, R.drawable.lehenga2, R.drawable.designerblouse3, R.drawable.designerblouse4};
                return lehengas[position % lehengas.length];
            case "wedding wear":
            case "bridal":
                int[] wedding = {R.drawable.sherwani1, R.drawable.lehenga1, R.drawable.suit1, R.drawable.designerblouse2};
                return wedding[position % wedding.length];
            case "casual":
            case "casuals":
                int[] casuals = {R.drawable.casual1, R.drawable.casual2, R.drawable.casual3, R.drawable.casual4};
                return casuals[position % casuals.length];
            case "alterations":
                return R.drawable.alteration1;
            case "custom stitching":
            default:
                // Cycle through varied images for miscellaneous categories
                int[] mixed = {R.drawable.shirt1, R.drawable.casual1, R.drawable.formal1, R.drawable.jeans1,
                        R.drawable.designerblouse1, R.drawable.kurta1, R.drawable.casual2, R.drawable.formal3};
                return mixed[position % mixed.length];
        }
    }

    private int getShirtDrawable(int position) {
        int[] shirts = {R.drawable.shirt1, R.drawable.shirt2, R.drawable.shirt3, R.drawable.shirt4,
                R.drawable.shirt5, R.drawable.shirt6, R.drawable.shirt7, R.drawable.shirt8};
        return shirts[position % shirts.length];
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Service> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(servicesFull);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Service item : servicesFull) {
                        if (item.getName().toLowerCase().contains(filterPattern) ||
                            (item.getCategory() != null && item.getCategory().toLowerCase().contains(filterPattern))) {
                            filteredList.add(item);
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
                services.clear();
                services.addAll((List) results.values);
                notifyDataSetChanged();
            }
        };
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivServiceImage;
        private final TextView tvServiceName;
        private final TextView tvServiceCategory;
        private final TextView tvServiceRating;
        private final TextView tvServicePrice;
        private final MaterialButton btnBookService;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            ivServiceImage = itemView.findViewById(R.id.ivServiceImage);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvServiceCategory = itemView.findViewById(R.id.tvServiceCategory);
            tvServiceRating = itemView.findViewById(R.id.tvServiceRating);
            tvServicePrice = itemView.findViewById(R.id.tvServicePrice);
            btnBookService = itemView.findViewById(R.id.btnBookService);
        }
    }
}
