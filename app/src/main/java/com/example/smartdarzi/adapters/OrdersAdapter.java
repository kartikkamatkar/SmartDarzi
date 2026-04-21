package com.example.smartdarzi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartdarzi.R;
import com.example.smartdarzi.models.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private final List<Order> orders = new ArrayList<>();

    public void submitList(@NonNull List<Order> newOrders) {
        orders.clear();
        orders.addAll(newOrders);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        Context context = holder.itemView.getContext();

        String status = mapStatus(order.getStatus());

        holder.tvOrderId.setText(context.getString(R.string.order_id_label, order.getId()));
        holder.tvOrderDate.setText(context.getString(R.string.order_date_label, order.getOrderDate()));
        holder.tvOrderStatus.setText(context.getString(R.string.order_status_label, status));

        // Subtle visual cue by status
        int statusColorRes = colorForStatus(status);
        holder.tvOrderStatus.setTextColor(ContextCompat.getColor(context, statusColorRes));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    private String mapStatus(String raw) {
        if (raw == null) return "Received";
        String normalized = raw.trim().toLowerCase(Locale.ROOT);
        if (normalized.equals("received") || normalized.equals("pending")) return "Received";
        if (normalized.equals("stitching") || normalized.equals("in progress")) return "Stitching";
        if (normalized.equals("ready") || normalized.equals("pickup scheduled")) return "Ready";
        if (normalized.equals("delivered")) return "Delivered";
        return "Received";
    }

    private int colorForStatus(String status) {
        if ("Delivered".equals(status)) return R.color.primary;
        if ("Ready".equals(status)) return R.color.secondary;
        if ("Stitching".equals(status)) return R.color.on_surface_variant;
        return R.color.on_surface;
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {

        final TextView tvOrderId;
        final TextView tvOrderDate;
        final TextView tvOrderStatus;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
        }
    }
}
