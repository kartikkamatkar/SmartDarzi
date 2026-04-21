package com.example.smartdarzi.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartdarzi.R;
import com.example.smartdarzi.activities.MainActivity;
import com.example.smartdarzi.database.DatabaseHelper;
import com.example.smartdarzi.models.CartModel;
import com.example.smartdarzi.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BasketFragment extends Fragment {

    private TextView tvBasketCount;
    private RecyclerView rvBasketItems;
    private LinearLayout layoutBasketEmpty;
    private LinearLayout layoutBottomSummary;
    private TextView tvSubtotal;
    private TextView tvServiceFee;
    private TextView tvTotal;
    private MaterialButton btnCheckout;

    private final List<CartModel> basketItems = new ArrayList<>();
    private BasketAdapter adapter;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private int userId;

    public BasketFragment() {
        super(R.layout.fragment_basket);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DatabaseHelper(requireContext());
        sessionManager = new SessionManager(requireContext());
        userId = sessionManager.getLoggedInUserId();

        tvBasketCount = view.findViewById(R.id.tvBasketCount);
        rvBasketItems = view.findViewById(R.id.rvBasketItems);
        layoutBasketEmpty = view.findViewById(R.id.layoutBasketEmpty);
        layoutBottomSummary = view.findViewById(R.id.layoutBottomSummary);
        tvSubtotal = view.findViewById(R.id.tvSubtotal);
        tvServiceFee = view.findViewById(R.id.tvServiceFee);
        tvTotal = view.findViewById(R.id.tvTotal);
        btnCheckout = view.findViewById(R.id.btnCheckout);

        MaterialButton btnBrowseServices = view.findViewById(R.id.btnBrowseServices);
        btnBrowseServices.setOnClickListener(v -> {
            BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
            if (bottomNavigationView != null) {
                bottomNavigationView.setSelectedItemId(R.id.nav_categories);
            }
        });

        adapter = new BasketAdapter();
        rvBasketItems.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvBasketItems.setAdapter(adapter);

        btnCheckout.setOnClickListener(v -> proceedCheckout());

        loadBasket();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBasket();
    }

    private void loadBasket() {
        basketItems.clear();
        basketItems.addAll(dbHelper.getBasketItems(userId));
        adapter.notifyDataSetChanged();

        int count = 0;
        for (CartModel item : basketItems) {
            count += item.getQuantity();
        }
        tvBasketCount.setText(getString(R.string.items_count_simple, count));

        boolean empty = basketItems.isEmpty();
        layoutBasketEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        rvBasketItems.setVisibility(empty ? View.GONE : View.VISIBLE);
        layoutBottomSummary.setVisibility(empty ? View.GONE : View.VISIBLE);

        updateTotals();
        updateBadge();
    }

    private void updateTotals() {
        double subtotal = 0;
        for (CartModel item : basketItems) {
            subtotal += item.getSubtotal();
        }
        double fee = subtotal * 0.05;
        double total = subtotal + fee;

        tvSubtotal.setText(getString(R.string.rupee_format, String.format(Locale.getDefault(), "%.0f", subtotal)));
        tvServiceFee.setText(getString(R.string.rupee_format, String.format(Locale.getDefault(), "%.0f", fee)));
        tvTotal.setText(getString(R.string.rupee_format, String.format(Locale.getDefault(), "%.0f", total)));
    }

    private void updateBadge() {
        MainActivity activity = MainActivity.getInstance();
        if (activity != null) {
            activity.updateBasketBadge(dbHelper.getBasketCount(userId));
        }
    }

    private void proceedCheckout() {
        if (basketItems.isEmpty()) {
            Snackbar.make(requireView(), R.string.empty_cart_checkout, Snackbar.LENGTH_SHORT).show();
            return;
        }

        new MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                .setTitle(R.string.checkout_confirm_title)
                .setMessage(R.string.checkout_confirm_message)
                .setPositiveButton(R.string.proceed_to_book, (dialog, which) -> {
                    // FIX: Create bookings in DatabaseHelper (shown in Bookings tab)
                    // and also clear the basket atomically
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String today = sdf.format(new java.util.Date());
                    boolean anyBooked = false;
                    for (CartModel item : basketItems) {
                        boolean ok = dbHelper.createBooking(
                                userId,
                                item.getProductName(),
                                today,
                                item.getSubtotal());
                        if (ok) anyBooked = true;
                    }
                    dbHelper.clearBasket(userId); // FIX: clear basket after booking
                    loadBasket();
                    if (anyBooked) {
                        // Navigate to Orders/Bookings tab so user sees their booking
                        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottomNavigationView);
                        if (bottomNav != null) bottomNav.setSelectedItemId(R.id.nav_orders);
                    }
                    Snackbar.make(requireView(), R.string.order_placed_success, Snackbar.LENGTH_LONG).show();
                })
                .setNegativeButton(R.string.booking_cancel_negative, null)
                .show();
    }

    private class BasketAdapter extends RecyclerView.Adapter<BasketAdapter.BasketVH> {

        @NonNull
        @Override
        public BasketVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_basket, parent, false);
            return new BasketVH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BasketVH holder, int position) {
            CartModel item = basketItems.get(position);
            holder.tvBasketItemName.setText(item.getProductName());
            holder.tvBasketItemCategory.setText(getString(R.string.category_label_unknown));
            holder.tvBasketItemPrice.setText(getString(R.string.rupee_format,
                    String.format(Locale.getDefault(), "%.0f", item.getSubtotal())));
            holder.tvQty.setText(String.valueOf(item.getQuantity()));
            // FIX: Use category-based drawable instead of generic icon
            holder.ivBasketItem.setImageResource(getDrawableForItem(item.getProductName(), position));
            holder.ivBasketItem.setScaleType(ImageView.ScaleType.CENTER_CROP);

            holder.btnPlusQty.setOnClickListener(v -> {
                int nextQty = item.getQuantity() + 1;
                // FIX: use dbHelper2 (DatabaseHelper) for basket operations
                if (dbHelper.updateBasketQuantity(userId, item.getProductId(), nextQty)) {
                    item.setQuantity(nextQty);
                    notifyItemChanged(position);
                    updateTotals();
                    updateBadge();
                }
            });

            holder.btnMinusQty.setOnClickListener(v -> {
                int nextQty = Math.max(1, item.getQuantity() - 1);
                // FIX: use dbHelper2 (DatabaseHelper) for basket operations
                if (dbHelper.updateBasketQuantity(userId, item.getProductId(), nextQty)) {
                    item.setQuantity(nextQty);
                    notifyItemChanged(position);
                    updateTotals();
                    updateBadge();
                }
            });

            holder.btnRemoveItem.setOnClickListener(v -> {
                // FIX: use dbHelper2 (DatabaseHelper) for basket remove
                if (dbHelper.removeFromBasket(userId, item.getProductId())) {
                    basketItems.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, basketItems.size() - position);
                    loadBasket();
                }
            });
        }

        /** Maps product name keywords to a local drawable for visual feedback */
        private int getDrawableForItem(String name, int pos) {
            if (name == null) return R.drawable.shirt1;
            String l = name.toLowerCase(Locale.getDefault());
            if (l.contains("lehenga") || l.contains("bridal"))
                return new int[]{R.drawable.lehenga1, R.drawable.lehenga2, R.drawable.sherwani1, R.drawable.designerblouse1}[pos % 4];
            if (l.contains("blouse"))
                return new int[]{R.drawable.designerblouse1, R.drawable.designerblouse2, R.drawable.designerblouse3, R.drawable.designerblouse4}[pos % 4];
            if (l.contains("saree") || l.contains("sari") || l.contains("sadi"))
                return R.drawable.saree1;
            if (l.contains("suit") || l.contains("formal") || l.contains("trouser") || l.contains("pant"))
                return new int[]{R.drawable.suit1, R.drawable.formal1, R.drawable.formal2, R.drawable.formal3}[pos % 4];
            if (l.contains("jean") || l.contains("denim"))
                return new int[]{R.drawable.jeans1, R.drawable.jeans2, R.drawable.jeans3, R.drawable.jeans4}[pos % 4];
            if (l.contains("kurta"))
                return new int[]{R.drawable.kurta_design1, R.drawable.kurta_design2, R.drawable.kurta_design3, R.drawable.kurta_design4}[pos % 4];
            if (l.contains("casual"))
                return new int[]{R.drawable.casual1, R.drawable.casual2, R.drawable.casual3, R.drawable.casual4}[pos % 4];
            return new int[]{R.drawable.shirt1, R.drawable.shirt2, R.drawable.shirt3, R.drawable.shirt4,
                    R.drawable.shirt5, R.drawable.shirt6, R.drawable.shirt7, R.drawable.shirt8}[pos % 8];
        }

        @Override
        public int getItemCount() {
            return basketItems.size();
        }

        class BasketVH extends RecyclerView.ViewHolder {
            final ImageView ivBasketItem;
            final TextView tvBasketItemName;
            final TextView tvBasketItemCategory;
            final TextView tvBasketItemPrice;
            final TextView tvQty;
            final TextView btnMinusQty;
            final TextView btnPlusQty;
            final ImageView btnRemoveItem;

            BasketVH(@NonNull View itemView) {
                super(itemView);
                ivBasketItem = itemView.findViewById(R.id.ivBasketItem);
                tvBasketItemName = itemView.findViewById(R.id.tvBasketItemName);
                tvBasketItemCategory = itemView.findViewById(R.id.tvBasketItemCategory);
                tvBasketItemPrice = itemView.findViewById(R.id.tvBasketItemPrice);
                tvQty = itemView.findViewById(R.id.tvQty);
                btnMinusQty = itemView.findViewById(R.id.btnMinusQty);
                btnPlusQty = itemView.findViewById(R.id.btnPlusQty);
                btnRemoveItem = itemView.findViewById(R.id.btnRemoveItem);
            }
        }
    }
}
