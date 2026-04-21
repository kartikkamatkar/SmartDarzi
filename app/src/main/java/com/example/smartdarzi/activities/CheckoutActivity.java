package com.example.smartdarzi.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartdarzi.R;
import com.example.smartdarzi.database.DatabaseHelper;
import com.example.smartdarzi.models.CartModel;
import com.example.smartdarzi.utils.CommonHeaderHelper;
import com.example.smartdarzi.utils.SessionManager;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private AppBarLayout appBarLayout;
    private LinearLayout orderItemsContainer;
    private TextView totalPriceText;
    private TextInputLayout addressInputLayout;
    private TextInputEditText addressEditText;
    private MaterialButton placeOrderButton;

    private LinearLayout successOverlay;
    private TextView orderIdText;
    private TextView deliveryAddressText;
    private MaterialButton viewOrdersButton;
    private MaterialButton continueShoppingButton;

    private DatabaseHelper db;
    private int userId;
    private final List<CartModel> cartItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        CommonHeaderHelper.bind(
                findViewById(android.R.id.content),
                getString(R.string.checkout_title),
                getString(R.string.header_checkout_subtitle));

        db = new DatabaseHelper(this);
        userId = new SessionManager(this).getLoggedInUserId();

        bindViews();
        setupToolbar();
        setupBackHandler();
        setupActions();
        loadOrderData();
    }

    private void bindViews() {
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appBarLayout);
        orderItemsContainer = findViewById(R.id.orderItemsContainer);
        totalPriceText = findViewById(R.id.totalPriceText);
        addressInputLayout = findViewById(R.id.addressInputLayout);
        addressEditText = findViewById(R.id.addressEditText);
        placeOrderButton = findViewById(R.id.placeOrderButton);

        successOverlay = findViewById(R.id.successOverlay);
        orderIdText = findViewById(R.id.orderIdText);
        deliveryAddressText = findViewById(R.id.deliveryAddressText);
        viewOrdersButton = findViewById(R.id.viewOrdersButton);
        continueShoppingButton = findViewById(R.id.continueShoppingButton);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupBackHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (successOverlay.getVisibility() == View.VISIBLE) {
                    goToMain();
                    return;
                }
                finish();
            }
        });
    }

    private void setupActions() {
        placeOrderButton.setOnClickListener(v -> placeOrder());
        viewOrdersButton.setOnClickListener(v -> goToMain());
        continueShoppingButton.setOnClickListener(v -> goToMain());
    }

    private void loadOrderData() {
        cartItems.clear();
        orderItemsContainer.removeAllViews();

        List<ContentValues> rows = db.getCartItemsForUser(userId);
        double total = 0d;

        for (ContentValues row : rows) {
            Integer cartId = row.getAsInteger("id");
            Integer productId = row.getAsInteger("product_id");
            String name = row.getAsString("product_name");
            Double price = row.getAsDouble("product_price");
            Integer qty = row.getAsInteger("quantity");

            if (cartId == null || productId == null || name == null || price == null || qty == null) {
                continue;
            }

            CartModel item = new CartModel(cartId, productId, name, price, qty);
            cartItems.add(item);
            total += item.getSubtotal();
            addOrderRow(item);
        }

        totalPriceText.setText(getString(R.string.rupee_format, String.format(Locale.getDefault(), "%.0f", total)));

        if (cartItems.isEmpty()) {
            placeOrderButton.setEnabled(false);
            Snackbar.make(placeOrderButton, R.string.empty_cart_checkout, Snackbar.LENGTH_LONG).show();
        } else {
            placeOrderButton.setEnabled(true);
        }
    }

    private void addOrderRow(@NonNull CartModel item) {
        View row = LayoutInflater.from(this)
                .inflate(R.layout.item_checkout_order_row, orderItemsContainer, false);

        ((TextView) row.findViewById(R.id.tvRowProductName)).setText(item.getProductName());
        ((TextView) row.findViewById(R.id.tvRowUnitPrice)).setText(
                getString(
                        R.string.price_per_unit_format,
                        getString(R.string.rupee_format, String.format(Locale.getDefault(), "%.0f", item.getUnitPrice()))));
        ((TextView) row.findViewById(R.id.tvRowQty)).setText(
                String.format(Locale.getDefault(), "x%d", item.getQuantity()));
        ((TextView) row.findViewById(R.id.tvRowSubtotal)).setText(
                getString(R.string.rupee_format, String.format(Locale.getDefault(), "%.0f", item.getSubtotal())));

        orderItemsContainer.addView(row);
    }

    private void placeOrder() {
        String address = addressEditText.getText() != null
                ? addressEditText.getText().toString().trim()
                : "";

        if (address.isEmpty()) {
            addressInputLayout.setError(getString(R.string.address_required));
            addressEditText.requestFocus();
            return;
        }

        addressInputLayout.setError(null);
        placeOrderButton.setEnabled(false);
        placeOrderButton.setText(R.string.placing_order);

        long firstOrderId = db.placeOrderFromCart(userId, address);
        if (firstOrderId > 0) {
            showSuccess(firstOrderId, address);
        } else {
            placeOrderButton.setEnabled(true);
            placeOrderButton.setText(R.string.place_order);
            Snackbar.make(placeOrderButton, R.string.order_place_failed, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void showSuccess(long orderId, @NonNull String address) {
        successOverlay.setVisibility(View.VISIBLE);
        placeOrderButton.setVisibility(View.GONE);
        appBarLayout.setLiftOnScroll(false);

        orderIdText.setText(getString(R.string.order_id_label, orderId));
        deliveryAddressText.setText(address);
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
