package com.example.smartdarzi.bottomsheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartdarzi.R;
import com.example.smartdarzi.models.Service;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BasketBottomSheetDialog extends BottomSheetDialogFragment {

    private static final double SERVICE_FEE = 49;

    private final Map<Integer, Integer> basket = new HashMap<>();
    private final List<Service> allServices = new ArrayList<>();
    private Runnable onProceedListener;

    public void setBasketData(@Nullable Map<Integer, Integer> basketItems, @Nullable List<Service> services) {
        basket.clear();
        allServices.clear();

        if (basketItems != null) {
            basket.putAll(basketItems);
        }
        if (services != null) {
            allServices.addAll(services);
        }
    }

    public void setOnProceedListener(@Nullable Runnable listener) {
        onProceedListener = listener;
    }

    @Override
    public int getTheme() {
        return R.style.Theme_Design_BottomSheetDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_basket, container, false);

        RecyclerView rvBasketItems = view.findViewById(R.id.rvBasketItems);
        TextView tvSubtotalVal = view.findViewById(R.id.tvSubtotalVal);
        TextView tvFeeVal = view.findViewById(R.id.tvFeeVal);
        TextView tvFinalTotalVal = view.findViewById(R.id.tvFinalTotalVal);
        TextView tvSheetBasketCount = view.findViewById(R.id.tvSheetBasketCount);
        MaterialButton btnProceedToBook = view.findViewById(R.id.btnProceedToBook);

        List<BasketRow> rows = buildRows();
        rvBasketItems.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvBasketItems.setAdapter(new BasketRowsAdapter(rows));

        double subtotal = 0;
        int itemCount = 0;
        for (BasketRow row : rows) {
            subtotal += row.lineTotal;
            itemCount += row.quantity;
        }
        double finalTotal = subtotal + SERVICE_FEE;

        tvSheetBasketCount.setText(getString(R.string.sheet_items_count, itemCount));
        tvSubtotalVal.setText(String.format(Locale.getDefault(), "₹%.0f", subtotal));
        tvFeeVal.setText(String.format(Locale.getDefault(), "₹%.0f", SERVICE_FEE));
        tvFinalTotalVal.setText(String.format(Locale.getDefault(), "₹%.0f", finalTotal));

        btnProceedToBook.setOnClickListener(v -> {
            if (onProceedListener != null) {
                onProceedListener.run();
            }
            dismiss();
        });

        return view;
    }

    private List<BasketRow> buildRows() {
        List<BasketRow> rows = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : basket.entrySet()) {
            int serviceId = entry.getKey();
            int quantity = entry.getValue();
            Service service = findService(serviceId);
            if (service == null) {
                continue;
            }
            rows.add(new BasketRow(service.getName(), quantity, service.getPrice() * quantity));
        }

        return rows;
    }

    @Nullable
    private Service findService(int serviceId) {
        for (Service service : allServices) {
            if (service.getId() == serviceId) {
                return service;
            }
        }
        return null;
    }

    private static class BasketRow {
        final String serviceName;
        final int quantity;
        final double lineTotal;

        BasketRow(String serviceName, int quantity, double lineTotal) {
            this.serviceName = serviceName;
            this.quantity = quantity;
            this.lineTotal = lineTotal;
        }
    }

    private static class BasketRowsAdapter extends RecyclerView.Adapter<BasketRowsAdapter.RowViewHolder> {
        private final List<BasketRow> rows;

        BasketRowsAdapter(List<BasketRow> rows) {
            this.rows = rows;
        }

        @NonNull
        @Override
        public RowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            return new RowViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RowViewHolder holder, int position) {
            BasketRow row = rows.get(position);
            holder.text1.setText(row.serviceName + " × " + row.quantity);
            holder.text2.setText(String.format(Locale.getDefault(), "₹%.0f", row.lineTotal));
        }

        @Override
        public int getItemCount() {
            return rows.size();
        }

        static class RowViewHolder extends RecyclerView.ViewHolder {
            final TextView text1;
            final TextView text2;

            RowViewHolder(@NonNull View itemView) {
                super(itemView);
                text1 = itemView.findViewById(android.R.id.text1);
                text2 = itemView.findViewById(android.R.id.text2);
            }
        }
    }
}
