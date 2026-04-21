package com.example.smartdarzi.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartdarzi.R;
import com.example.smartdarzi.activities.CheckoutActivity;
import com.example.smartdarzi.adapters.CartAdapter;
import com.example.smartdarzi.database.DatabaseHelper;
import com.example.smartdarzi.models.CartModel;
import com.example.smartdarzi.utils.CommonHeaderHelper;
import com.example.smartdarzi.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartFragment extends Fragment {

    // ── Views ─────────────────────────────────────────────────────────────────
    private RecyclerView      rvCart;
    private LinearLayout      layoutEmptyCart;
    private MaterialCardView  cardCartTotal;
    private TextView          tvCartTotal;
    private MaterialButton    btnCheckout;

    // ── Data ──────────────────────────────────────────────────────────────────
    private CartAdapter    adapter;
    private DatabaseHelper db;
    private int            userId;

    // ── Constructor ───────────────────────────────────────────────────────────

    public CartFragment() {
        super(R.layout.fragment_cart);
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        CommonHeaderHelper.bind(
                root,
                getString(R.string.my_cart),
                getString(R.string.header_cart_subtitle));

        bindViews(root);

        db     = new DatabaseHelper(requireContext());
        userId = new SessionManager(requireContext()).getLoggedInUserId();

        setupRecyclerView(root);
        loadCart();
        setupCheckout(root);
    }

    /**
     * Reload the cart whenever the fragment becomes visible — this handles the
     * case where the user returns from {@link CheckoutActivity} after a successful
     * order (cart has been cleared in the DB).
     */
    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            loadCart();
        }
    }

    // ── Setup helpers ─────────────────────────────────────────────────────────

    private void bindViews(@NonNull View root) {
        rvCart          = root.findViewById(R.id.rvCart);
        layoutEmptyCart = root.findViewById(R.id.layoutEmptyCart);
        cardCartTotal   = root.findViewById(R.id.cardCartTotal);
        tvCartTotal     = root.findViewById(R.id.tvCartTotal);
        btnCheckout     = root.findViewById(R.id.btnCheckout);
    }

    private void setupRecyclerView(@NonNull View root) {
        adapter = new CartAdapter(new CartAdapter.CartListener() {

            @Override
            public void onQuantityIncreased(CartModel item, int position) {
                int newQty = item.getQuantity() + 1;
                if (db.updateCartItemQuantity(item.getCartId(), newQty)) {
                    adapter.updateItem(position, newQty);
                    refreshTotal();
                }
            }

            @Override
            public void onQuantityDecreased(CartModel item, int position) {
                if (item.getQuantity() <= 1) return;
                int newQty = item.getQuantity() - 1;
                if (db.updateCartItemQuantity(item.getCartId(), newQty)) {
                    adapter.updateItem(position, newQty);
                    refreshTotal();
                }
            }

            @Override
            public void onItemRemoved(CartModel item, int position) {
                if (db.deleteCartItem(item.getCartId())) {
                    adapter.removeItem(position);
                    refreshTotal();
                    toggleEmptyState(adapter.getItemCount() == 0);
                    Snackbar.make(root,
                            getString(R.string.cart_item_removed, item.getProductName()),
                            Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        rvCart.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCart.setAdapter(adapter);
    }

    private void setupCheckout(@NonNull View root) {
        btnCheckout.setOnClickListener(v -> {
            if (adapter.getItemCount() == 0) {
                Snackbar.make(root, R.string.empty_cart_checkout, Snackbar.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(requireContext(), CheckoutActivity.class));
        });
    }

    // ── Data loading ──────────────────────────────────────────────────────────

    /** Fetch all cart rows for the current user and push them to the adapter. */
    private void loadCart() {
        List<ContentValues> rows  = db.getCartItemsForUser(userId);
        List<CartModel>     items = new ArrayList<>(rows.size());

        for (ContentValues row : rows) {
            Integer cartId    = row.getAsInteger("id");
            Integer productId = row.getAsInteger("product_id");
            String  name      = row.getAsString("product_name");
            Double  price     = row.getAsDouble("product_price");
            Integer qty       = row.getAsInteger("quantity");

            if (cartId == null || productId == null || name == null
                    || price == null || qty == null) continue;

            items.add(new CartModel(cartId, productId, name, price, qty));
        }

        adapter.submitList(items);
        toggleEmptyState(items.isEmpty());
        refreshTotal();
    }

    // ── UI helpers ────────────────────────────────────────────────────────────

    /** Show / hide the RecyclerView vs. empty-state panel. */
    private void toggleEmptyState(boolean isEmpty) {
        rvCart.setVisibility(isEmpty ? View.GONE  : View.VISIBLE);
        layoutEmptyCart.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        cardCartTotal.setVisibility(isEmpty ? View.GONE  : View.VISIBLE);

        int count = adapter.getItemCount();
        View root = getView();
        if (root != null) {
            CommonHeaderHelper.updateSubtitle(
                    root,
                    isEmpty
                            ? getString(R.string.header_cart_subtitle)
                            : getResources().getQuantityString(R.plurals.cart_items_count, count, count));
        }
    }

    /** Recalculate total from adapter and update the footer TextView. */
    private void refreshTotal() {
        double total = adapter.calculateTotal();
        tvCartTotal.setText(
                String.format(Locale.getDefault(), "Rs. %.0f", total));
    }
}
