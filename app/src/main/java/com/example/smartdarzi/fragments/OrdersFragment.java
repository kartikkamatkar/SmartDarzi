package com.example.smartdarzi.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.smartdarzi.R;
import com.example.smartdarzi.database.DatabaseHelper;
import com.example.smartdarzi.models.Booking;
import com.example.smartdarzi.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrdersFragment extends Fragment {

    private MaterialButton btnTabUpcoming;
    private MaterialButton btnTabCompleted;
    private MaterialButton btnTabCancelled;
    private ViewPager2 ordersViewPager;

    public OrdersFragment() {
        super(R.layout.fragment_orders);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnTabUpcoming = view.findViewById(R.id.btnTabUpcoming);
        btnTabCompleted = view.findViewById(R.id.btnTabCompleted);
        btnTabCancelled = view.findViewById(R.id.btnTabCancelled);
        ordersViewPager = view.findViewById(R.id.ordersViewPager);

        ordersViewPager.setAdapter(new OrdersPagerAdapter(this));

        btnTabUpcoming.setOnClickListener(v -> ordersViewPager.setCurrentItem(0, true));
        btnTabCompleted.setOnClickListener(v -> ordersViewPager.setCurrentItem(1, true));
        btnTabCancelled.setOnClickListener(v -> ordersViewPager.setCurrentItem(2, true));

        ordersViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateTabStyles(position);
            }
        });

        updateTabStyles(0);
    }

    private void updateTabStyles(int selected) {
        styleTab(btnTabUpcoming, selected == 0);
        styleTab(btnTabCompleted, selected == 1);
        styleTab(btnTabCancelled, selected == 2);
    }

    private void styleTab(@NonNull MaterialButton button, boolean selected) {
        button.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), selected ? R.color.premium_button : R.color.premium_card));
        button.setTextColor(ContextCompat.getColor(requireContext(), selected ? R.color.white : R.color.gray_600));
    }

    private static class OrdersPagerAdapter extends FragmentStateAdapter {

        OrdersPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 1) {
                return OrdersPageFragment.newInstance("Completed");
            }
            if (position == 2) {
                return OrdersPageFragment.newInstance("Cancelled");
            }
            return OrdersPageFragment.newInstance("Upcoming");
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }

    public static class OrdersPageFragment extends Fragment {

        private static final String ARG_STATUS = "arg_status";

        private String status;
        private DatabaseHelper dbHelper;
        private int userId;
        private RecyclerView rvOrderPage;
        private TextView tvOrderPageEmpty;

        public static OrdersPageFragment newInstance(@NonNull String status) {
            OrdersPageFragment fragment = new OrdersPageFragment();
            Bundle args = new Bundle();
            args.putString(ARG_STATUS, status);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            status = getArguments() != null ? getArguments().getString(ARG_STATUS, "Upcoming") : "Upcoming";
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_orders_page, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            rvOrderPage = view.findViewById(R.id.rvOrderPage);
            tvOrderPageEmpty = view.findViewById(R.id.tvOrderPageEmpty);

            dbHelper = new DatabaseHelper(requireContext());
            userId = new SessionManager(requireContext()).getLoggedInUserId();

            rvOrderPage.setLayoutManager(new LinearLayoutManager(requireContext()));
            loadOrders();
        }

        @Override
        public void onResume() {
            super.onResume();
            loadOrders();
        }

        private void loadOrders() {
            List<Booking> bookings = dbHelper.getBookingsByStatus(userId, status);
            rvOrderPage.setAdapter(new OrderCardAdapter(bookings));
            tvOrderPageEmpty.setVisibility(bookings.isEmpty() ? View.VISIBLE : View.GONE);
        }

        private class OrderCardAdapter extends RecyclerView.Adapter<OrderCardAdapter.OrderVH> {

            private final List<Booking> items;

            OrderCardAdapter(List<Booking> items) {
                this.items = items == null ? new ArrayList<>() : items;
            }

            @NonNull
            @Override
            public OrderVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_card, parent, false);
                return new OrderVH(view);
            }

            @Override
            public void onBindViewHolder(@NonNull OrderVH holder, int position) {
                Booking booking = items.get(position);
                holder.tvOrderCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.premium_card));
                holder.tvOrderId.setText(getString(R.string.order_id_label, booking.getId()));
                holder.tvOrderStatusBadge.setText(booking.getStatus());
                holder.tvOrderServiceName.setText(booking.getServiceName());
                String tailorName = booking.getTailorName() == null || booking.getTailorName().trim().isEmpty()
                        ? getString(R.string.booking_tailor_pending)
                        : booking.getTailorName();
                holder.tvOrderTailorName.setText(getString(R.string.booking_tailor, tailorName));

                String dateStr = booking.getDate();
                try {
                    long timestamp = Long.parseLong(dateStr);
                    Date date = new Date(timestamp);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                    holder.tvOrderDate.setText(sdf.format(date));
                } catch (NumberFormatException e) {
                    holder.tvOrderDate.setText(dateStr);
                }

                holder.tvOrderPrice.setText(getString(R.string.rupee_format, String.format(Locale.getDefault(), "%.0f", booking.getPrice())));

                int statusColor;
                String normalized = booking.getStatus().toLowerCase(Locale.getDefault());
                if ("completed".equals(normalized)) {
                    statusColor = R.color.gray_700;
                } else if ("cancelled".equals(normalized)) {
                    statusColor = R.color.gray_500;
                } else {
                    statusColor = R.color.premium_button;
                }
                holder.tvOrderStatusBadge.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), statusColor));

                holder.tvOrderId.setVisibility(View.GONE);
                holder.tvOrderTailorName.setVisibility(View.GONE);
                holder.tvOrderTime.setVisibility(View.GONE);

                boolean isUpcoming = "upcoming".equalsIgnoreCase(booking.getStatus());
                holder.btnOrderCancel.setVisibility(isUpcoming ? View.VISIBLE : View.GONE);
                holder.btnOrderViewDetails.setVisibility(View.VISIBLE);

                holder.btnOrderCancel.setOnClickListener(v -> {
                    new MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                            .setTitle(R.string.booking_cancel_title)
                            .setMessage(R.string.booking_cancel_message)
                            .setPositiveButton(R.string.booking_cancel_positive, (dialog, which) -> {
                                dbHelper.updateBookingStatus(booking.getId(), "Cancelled");
                                loadOrders();
                            })
                            .setNegativeButton(R.string.booking_cancel_negative, null)
                            .show();
                });
            }

            @Override
            public int getItemCount() {
                return items.size();
            }

            class OrderVH extends RecyclerView.ViewHolder {
                final CardView tvOrderCard;
                final TextView tvOrderId;
                final TextView tvOrderStatusBadge;
                final TextView tvOrderServiceName;
                final TextView tvOrderTailorName;
                final TextView tvOrderDate;
                final TextView tvOrderTime;
                final TextView tvOrderPrice;
                final MaterialButton btnOrderViewDetails;
                final MaterialButton btnOrderCancel;

                OrderVH(@NonNull View itemView) {
                    super(itemView);
                    tvOrderCard = itemView.findViewById(R.id.cardOrderRoot);
                    tvOrderId = itemView.findViewById(R.id.tvOrderId);
                    tvOrderStatusBadge = itemView.findViewById(R.id.tvOrderStatusBadge);
                    tvOrderServiceName = itemView.findViewById(R.id.tvOrderServiceName);
                    tvOrderTailorName = itemView.findViewById(R.id.tvOrderTailorName);
                    tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
                    tvOrderTime = itemView.findViewById(R.id.tvOrderTime);
                    tvOrderPrice = itemView.findViewById(R.id.tvOrderPrice);
                    btnOrderViewDetails = itemView.findViewById(R.id.btnOrderViewDetails);
                    btnOrderCancel = itemView.findViewById(R.id.btnOrderCancel);
                }
            }
        }
    }
}
