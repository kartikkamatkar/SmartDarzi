package com.example.smartdarzi.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartdarzi.R;
import com.example.smartdarzi.bottomsheets.BasketBottomSheetDialog;
import com.example.smartdarzi.database.DatabaseHelper;
import com.example.smartdarzi.models.Service;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ServicesFragment extends Fragment {

    private TabLayout tabLayoutFilters;
    private RecyclerView rvServicesList;
    private CardView cvViewBasket;
    private MaterialButton btnViewBasket;
    private TextView tvBasketCountBadge, tvBasketTotal;

    private DatabaseHelper dbHelper;
    private AdvServiceAdapter adapter;
    private List<Service> allServices = new ArrayList<>();
    
    // Memory state for basket: Service ID -> Quantity
    private Map<Integer, Integer> basket = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_services, container, false);

        dbHelper = new DatabaseHelper(requireContext());

        initViews(view);
        setupTabs();
        setupServicesList();
        loadAllServices();

        btnViewBasket.setOnClickListener(v -> showBasketBottomSheet());
        cvViewBasket.setOnClickListener(v -> showBasketBottomSheet());

        return view;
    }

    private void initViews(View view) {
        tabLayoutFilters = view.findViewById(R.id.tabLayoutFilters);
        rvServicesList = view.findViewById(R.id.rvServicesList);
        cvViewBasket = view.findViewById(R.id.cvViewBasket);
        tvBasketCountBadge = view.findViewById(R.id.tvBasketCountBadge);
        tvBasketTotal = view.findViewById(R.id.tvBasketTotal);
        btnViewBasket = view.findViewById(R.id.btnViewBasket);
    }

    private void setupTabs() {
        String[] filters = {getString(R.string.cat_all), getString(R.string.cat_shirt),
            getString(R.string.cat_pants), getString(R.string.cat_suit),
            getString(R.string.cat_blouse), getString(R.string.cat_kurta),
            getString(R.string.cat_alterations)};
                          
        for (String filter : filters) {
            tabLayoutFilters.addTab(tabLayoutFilters.newTab().setText(filter));
        }

        tabLayoutFilters.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterList(tab.getText().toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupServicesList() {
        adapter = new AdvServiceAdapter();
        rvServicesList.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvServicesList.setAdapter(adapter);
    }

    private void loadAllServices() {
        allServices.clear();
        allServices.addAll(dbHelper.getAllServices());
        adapter.submitList(new ArrayList<>(allServices));
    }

    private void filterList(String query) {
        if (query.equals(getString(R.string.cat_all))) {
            adapter.submitList(new ArrayList<>(allServices));
            return;
        }

        String normalizedQuery = query.trim().toLowerCase(Locale.getDefault());
        String singularQuery = normalizedQuery.endsWith("s")
                ? normalizedQuery.substring(0, normalizedQuery.length() - 1)
                : normalizedQuery;

        List<Service> filtered = new ArrayList<>();
        for (Service s : allServices) {
            String name = s.getName() == null ? "" : s.getName().toLowerCase(Locale.getDefault());
            String category = s.getCategory() == null ? "" : s.getCategory().toLowerCase(Locale.getDefault());
            if (name.contains(normalizedQuery)
                    || name.contains(singularQuery)
                    || category.contains(normalizedQuery)
                    || category.contains(singularQuery)) {
                filtered.add(s);
            }
        }
        adapter.submitList(filtered);
    }

    private void updateBasketState(Service service, int quantity) {
        if (quantity <= 0) {
            basket.remove(service.getId());
        } else {
            basket.put(service.getId(), quantity);
        }
        refreshStickyBasketBar();
    }

    private void refreshStickyBasketBar() {
        if (basket.isEmpty()) {
            if (cvViewBasket.getVisibility() == View.VISIBLE) {
                Animation slideDown = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down);
                cvViewBasket.startAnimation(slideDown);
                cvViewBasket.setVisibility(View.GONE);
            }
            return;
        }

        if (cvViewBasket.getVisibility() == View.GONE) {
            Animation slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up);
            cvViewBasket.setVisibility(View.VISIBLE);
            cvViewBasket.startAnimation(slideUp);
        }

        int totalItems = 0;
        double totalPrice = 0;

        for (Map.Entry<Integer, Integer> entry : basket.entrySet()) {
            totalItems += entry.getValue();
            // Find service price
            for (Service s : allServices) {
                if (s.getId() == entry.getKey()) {
                    totalPrice += (s.getPrice() * entry.getValue());
                    break;
                }
            }
        }

        tvBasketCountBadge.setText(String.valueOf(totalItems));
        tvBasketTotal.setText(String.format(Locale.getDefault(), "₹%.0f", totalPrice));
    }

    private void showBasketBottomSheet() {
        if (basket.isEmpty()) {
            return;
        }

        BasketBottomSheetDialog bottomSheetDialog = new BasketBottomSheetDialog();
        bottomSheetDialog.setBasketData(new HashMap<>(basket), new ArrayList<>(allServices));
        bottomSheetDialog.setOnProceedListener(() -> {
            Toast.makeText(requireContext(), "Proceeding to checkout...", Toast.LENGTH_SHORT).show();
            basket.clear();
            adapter.notifyDataSetChanged();
            refreshStickyBasketBar();
        });
        bottomSheetDialog.show(getChildFragmentManager(), "basket_sheet");
    }

    // --- Inner Advanced Adapter for DiffUtil animations and state binding --- //
    private class AdvServiceAdapter extends RecyclerView.Adapter<AdvServiceAdapter.ViewHolder> {
        private List<Service> items = new ArrayList<>();

        public void submitList(List<Service> newItems) {
            ServiceDiffCallback diffCallback = new ServiceDiffCallback(this.items, newItems);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
            this.items.clear();
            this.items.addAll(newItems);
            diffResult.dispatchUpdatesTo(this);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Service service = items.get(position);
            holder.tvServiceName.setText(service.getName());
            holder.tvServiceDescription.setText(service.getDescription());
            holder.tvServicePrice.setText(String.format(Locale.getDefault(), "₹%.0f", service.getPrice()));
            holder.tvServiceTurnaround.setText(getString(R.string.turnaround_days, service.getTurnaroundDays()));
            holder.tvServiceRating.setText(String.format(Locale.getDefault(), "%.1f", service.getRating()));

            int qty = basket.containsKey(service.getId()) ? basket.get(service.getId()) : 0;
            if (qty > 0) {
                holder.btnBookService.setText(getString(R.string.view_basket) + " (" + qty + ")");
            } else {
                holder.btnBookService.setText(getString(R.string.book_now));
            }

            holder.btnBookService.setOnClickListener(v -> {
                int currentQty = basket.containsKey(service.getId()) ? basket.get(service.getId()) : 0;
                updateBasketState(service, currentQty + 1);
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(adapterPosition);
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvServiceName, tvServiceDescription, tvServicePrice, tvServiceTurnaround, tvServiceRating;
            MaterialButton btnBookService;

            ViewHolder(View itemView) {
                super(itemView);
                tvServiceName = itemView.findViewById(R.id.tvServiceName);
                tvServiceDescription = itemView.findViewById(R.id.tvServiceDescription);
                tvServicePrice = itemView.findViewById(R.id.tvServicePrice);
                tvServiceTurnaround = itemView.findViewById(R.id.tvServiceTurnaround);
                tvServiceRating = itemView.findViewById(R.id.tvServiceRating);
                btnBookService = itemView.findViewById(R.id.btnBookService);
            }
        }
    }

    private static class ServiceDiffCallback extends DiffUtil.Callback {
        private final List<Service> oldList;
        private final List<Service> newList;

        ServiceDiffCallback(List<Service> oldList, List<Service> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() { return oldList.size(); }

        @Override
        public int getNewListSize() { return newList.size(); }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Service oldItem = oldList.get(oldItemPosition);
            Service newItem = newList.get(newItemPosition);
            return oldItem.getName().equals(newItem.getName());
        }
    }
}
