package com.example.smartdarzi.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartdarzi.R;
import com.example.smartdarzi.activities.MainActivity;
import com.example.smartdarzi.activities.ProductDetailActivity;
import com.example.smartdarzi.adapters.ProductAdapter;
import com.example.smartdarzi.database.DatabaseHelper;
import com.example.smartdarzi.models.Product;
import com.example.smartdarzi.models.Service;
import com.example.smartdarzi.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductListFragment extends Fragment {

    private static final String ARG_CATEGORY_NAME = "category_name";

    public ProductListFragment() {
        super(R.layout.fragment_product_list);
    }

    public static ProductListFragment newInstance(@NonNull String categoryName) {
        ProductListFragment fragment = new ProductListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY_NAME, categoryName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String categoryName = getArguments() != null
                ? getArguments().getString(ARG_CATEGORY_NAME, "")
                : "";

        MaterialToolbar toolbar = view.findViewById(R.id.toolbarProductList);
        toolbar.setTitle(categoryName.isEmpty() ? getString(R.string.header_catalog_title) : categoryName);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());

        RecyclerView rvProductList = view.findViewById(R.id.rvProductList);
        TextView tvNoProducts = view.findViewById(R.id.tvNoProducts);

        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        SessionManager sessionManager = new SessionManager(requireContext());
        int userId = sessionManager.getLoggedInUserId();

        List<Service> services;
        if (categoryName.isEmpty() || categoryName.equalsIgnoreCase(getString(R.string.cat_all))) {
            services = dbHelper.getAllServices();
        } else {
            services = dbHelper.getServicesByCategory(categoryName);
            if (services.isEmpty()) {
                services = filterLoose(dbHelper.getAllServices(), categoryName);
            }
        }

        List<Product> products = mapServicesToProducts(services, categoryName);
        tvNoProducts.setVisibility(products.isEmpty() ? View.VISIBLE : View.GONE);

        ProductAdapter adapter = new ProductAdapter(new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
                intent.putExtra(ProductDetailActivity.EXTRA_SERVICE_ID, product.getId());
                intent.putExtra(ProductDetailActivity.EXTRA_SERVICE_NAME, product.getName());
                intent.putExtra(ProductDetailActivity.EXTRA_SERVICE_PRICE, product.getPrice());
                intent.putExtra(ProductDetailActivity.EXTRA_SERVICE_CATEGORY, categoryName);
                startActivity(intent);
            }

            @Override
            public void onAddToCartClick(Product product) {
                boolean added = dbHelper.addToBasket(userId, product.getId(), 1);
                if (added) {
                    Snackbar.make(view, getString(R.string.added_to_cart, product.getName()), Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(view, getString(R.string.already_in_cart, product.getName()), Snackbar.LENGTH_SHORT).show();
                }

                MainActivity activity = MainActivity.getInstance();
                if (activity != null) {
                    activity.updateBasketBadge(dbHelper.getBasketCount(userId));
                }
            }
        });

        com.facebook.shimmer.ShimmerFrameLayout shimmer = view.findViewById(R.id.shimmerProductList);

        adapter.submitList(products);
        rvProductList.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvProductList.setAdapter(adapter);

        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            if (shimmer != null) {
                shimmer.stopShimmer();
                shimmer.setVisibility(View.GONE);
            }
            if (!products.isEmpty()) {
                rvProductList.setVisibility(View.VISIBLE);
            }
        }, 1200);
    }

    private List<Service> filterLoose(@NonNull List<Service> services, @NonNull String categoryName) {
        List<Service> filtered = new ArrayList<>();
        String q = categoryName.toLowerCase(Locale.getDefault());
        for (Service service : services) {
            String name = service.getName() == null ? "" : service.getName().toLowerCase(Locale.getDefault());
            String category = service.getCategory() == null ? "" : service.getCategory().toLowerCase(Locale.getDefault());
            if (name.contains(q) || category.contains(q)) {
                filtered.add(service);
            }
        }
        return filtered;
    }

    private List<Product> mapServicesToProducts(@NonNull List<Service> services, @NonNull String categoryName) {
        List<Product> products = new ArrayList<>();
        for (Service service : services) {
            // FIX: use service.getCategory() so ProductAdapter can pick correct drawable
            String cat = (categoryName.isEmpty() || categoryName.equalsIgnoreCase(getString(R.string.cat_all)))
                    ? service.getCategory()    // show each product's real category
                    : categoryName;            // use the browsed category for consistency
            products.add(new Product(
                    service.getId(),
                    service.getName(),
                    service.getDescription(),
                    service.getPrice(),
                    R.drawable.ic_services,    // placeholder; ProductAdapter overrides with getCategory()
                    cat,
                    service.getTurnaroundDays(),
                    (float) service.getRating(),
                    service.getStockQuantity()
            ));
        }
        return products;
    }
}
