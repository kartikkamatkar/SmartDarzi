package com.example.smartdarzi.fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.smartdarzi.R;
import com.example.smartdarzi.database.DatabaseHelper;
import com.example.smartdarzi.models.Booking;
import com.example.smartdarzi.utils.CommonHeaderHelper;
import com.example.smartdarzi.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BookingsFragment extends Fragment {

    private TabLayout tabLayoutBookings;
    private ViewPager2 vpBookings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookings, container, false);

        tabLayoutBookings = view.findViewById(R.id.tabLayoutBookings);
        vpBookings = view.findViewById(R.id.vpBookings);

        CommonHeaderHelper.bind(
            view,
            getString(R.string.my_bookings),
            getString(R.string.header_bookings_subtitle));

        setupViewPager();

        return view;
    }

    private void setupViewPager() {
        BookingsPagerAdapter adapter = new BookingsPagerAdapter(this);
        vpBookings.setAdapter(adapter);

        new TabLayoutMediator(tabLayoutBookings, vpBookings, (tab, position) -> {
            switch (position) {
                case 0: tab.setText(R.string.tab_upcoming); break;
                case 1: tab.setText(R.string.tab_completed); break;
                case 2: tab.setText(R.string.tab_cancelled); break;
            }
        }).attach();
    }

    // --- Inner ViewPager Adapter ---
    private static class BookingsPagerAdapter extends FragmentStateAdapter {
        private static final String STATUS_UPCOMING = "Upcoming";
        private static final String STATUS_COMPLETED = "Completed";
        private static final String STATUS_CANCELLED = "Cancelled";

        BookingsPagerAdapter(Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            String status = STATUS_UPCOMING;
            if (position == 1) status = STATUS_COMPLETED;
            if (position == 2) status = STATUS_CANCELLED;
            return BookingListFragment.newInstance(status);
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }

    // --- Inner Fragment for each Tab ---
    public static class BookingListFragment extends Fragment {
        private static final String ARG_STATUS = "arg_status";
        private String status;
        
        private RecyclerView rvBookings;
        private View emptyStateLayout;
        private Button btnEmptyCta;
        private BookingAdapter adapter;
        private DatabaseHelper dbHelper;
        private SessionManager sessionManager;
        private int userId;

        public static BookingListFragment newInstance(String status) {
            BookingListFragment fragment = new BookingListFragment();
            Bundle args = new Bundle();
            args.putString(ARG_STATUS, status);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                status = getArguments().getString(ARG_STATUS);
            }
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_booking_list, container, false);

            rvBookings = view.findViewById(R.id.rvBookingList);
            emptyStateLayout = view.findViewById(R.id.layoutBookingEmptyState);
            btnEmptyCta = emptyStateLayout.findViewById(R.id.btnEmptyCta);

            dbHelper = new DatabaseHelper(requireContext());
            sessionManager = new SessionManager(requireContext());
            userId = Integer.parseInt(sessionManager.getUserDetails().get(SessionManager.KEY_ID) == null ? "1" : sessionManager.getUserDetails().get(SessionManager.KEY_ID));

            setupList();
            loadBookings();

            return view;
        }

        // FIX: Reload bookings every time this tab becomes visible (e.g. after checkout)
        @Override
        public void onResume() {
            super.onResume();
            if (adapter != null) {
                loadBookings();
            }
        }

        private void setupList() {
            adapter = new BookingAdapter(requireContext(), this::showCancelDialog);
            rvBookings.setLayoutManager(new LinearLayoutManager(requireContext()));
            rvBookings.setAdapter(adapter);
            
            btnEmptyCta.setOnClickListener(v -> {
                BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
                if (bottomNavigationView != null) {
                    bottomNavigationView.setSelectedItemId(R.id.nav_categories);
                } else {
                    Toast.makeText(requireContext(), R.string.redirecting_services, Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void loadBookings() {
            List<Booking> bookings = dbHelper.getBookingsByStatus(userId, status);
            if (bookings.isEmpty()) {
                rvBookings.setVisibility(View.GONE);
                emptyStateLayout.setVisibility(View.VISIBLE);
            } else {
                emptyStateLayout.setVisibility(View.GONE);
                rvBookings.setVisibility(View.VISIBLE);
                adapter.submitList(bookings);
            }
        }

        private void showCancelDialog(Booking booking) {
            new MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                .setTitle(R.string.booking_cancel_title)
                .setMessage(R.string.booking_cancel_message)
                .setPositiveButton(R.string.booking_cancel_positive, (dialog, which) -> {
                    boolean success = dbHelper.updateBookingStatus(booking.getId(), "Cancelled");
                    if (success) {
                        Toast.makeText(requireContext(), R.string.booking_cancelled, Toast.LENGTH_SHORT).show();
                        loadBookings(); // Refresh list to remove it from "Upcoming"
                    }
                })
                .setNegativeButton(R.string.booking_cancel_negative, null)
                .show();
        }
    }

    // --- Inner Advanced Adapter for DiffUtil ---
    private static class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {
        private final Context context;
        private List<Booking> items = new ArrayList<>();
        private final OnCancelClickListener cancelListener;

        interface OnCancelClickListener {
            void onCancelClick(Booking booking);
        }

        BookingAdapter(Context context, OnCancelClickListener listener) {
            this.context = context;
            this.cancelListener = listener;
        }

        public void submitList(List<Booking> newItems) {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() { return items.size(); }
                @Override
                public int getNewListSize() { return newItems.size(); }
                @Override
                public boolean areItemsTheSame(int oldPos, int newPos) {
                    return items.get(oldPos).getId() == newItems.get(newPos).getId();
                }
                @Override
                public boolean areContentsTheSame(int oldPos, int newPos) {
                    return items.get(oldPos).getStatus().equals(newItems.get(newPos).getStatus());
                }
            });
            items.clear();
            items.addAll(newItems);
            diffResult.dispatchUpdatesTo(this);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Booking booking = items.get(position);
            holder.tvBookingServiceName.setText(booking.getServiceName());
            holder.tvBookingDate.setText(context.getString(R.string.appointment_on, booking.getDate()));
            holder.tvBookingTailor.setText(context.getString(
                    R.string.booking_tailor,
                    booking.getTailorName() != null ? booking.getTailorName() : context.getString(R.string.booking_tailor_pending)));
            holder.tvBookingPrice.setText(String.format(Locale.getDefault(), "₹%.0f", booking.getPrice()));
            holder.tvBookingStatus.setText(booking.getStatus());

            // Dynamic Styling based on Status
            int statusColor;
            boolean showCancel;
            boolean showDetails;
            
            switch (booking.getStatus().toLowerCase()) {
                case "completed":
                    statusColor = ContextCompat.getColor(context, R.color.success);
                    showCancel = false;
                    showDetails = true;
                    break;
                case "cancelled":
                    statusColor = ContextCompat.getColor(context, R.color.error);
                    showCancel = false;
                    showDetails = true;
                    break;
                default: // Upcoming
                    statusColor = ContextCompat.getColor(context, R.color.primary);
                    showCancel = true;
                    showDetails = true;
                    holder.layoutProgressTracker.setVisibility(View.VISIBLE);
                    break;
            }

            if (booking.getStatus().equalsIgnoreCase("Completed") || booking.getStatus().equalsIgnoreCase("Cancelled")) {
                holder.layoutProgressTracker.setVisibility(View.GONE);
            }

            holder.viewStatusBorder.setBackgroundTintList(ColorStateList.valueOf(statusColor));
            holder.tvBookingStatus.setBackgroundTintList(ColorStateList.valueOf(statusColor));
            holder.tvBookingStatus.setTextColor(ContextCompat.getColor(context, R.color.on_primary));
            holder.btnCancelBooking.setVisibility(showCancel ? View.VISIBLE : View.GONE);
            holder.btnViewDetails.setVisibility(showDetails ? View.VISIBLE : View.GONE);

            holder.btnCancelBooking.setOnClickListener(v -> cancelListener.onCancelClick(booking));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            View viewStatusBorder, layoutProgressTracker;
            TextView tvBookingServiceName, tvBookingStatus, tvBookingTailor, tvBookingDate, tvBookingPrice;
            MaterialButton btnCancelBooking, btnViewDetails;

            ViewHolder(View itemView) {
                super(itemView);
                viewStatusBorder = itemView.findViewById(R.id.viewStatusBorder);
                layoutProgressTracker = itemView.findViewById(R.id.layoutProgressTracker);
                tvBookingServiceName = itemView.findViewById(R.id.tvBookingServiceName);
                tvBookingStatus = itemView.findViewById(R.id.tvBookingStatus);
                tvBookingTailor = itemView.findViewById(R.id.tvBookingTailor);
                tvBookingDate = itemView.findViewById(R.id.tvBookingDate);
                tvBookingPrice = itemView.findViewById(R.id.tvBookingPrice);
                btnCancelBooking = itemView.findViewById(R.id.btnCancelBooking);
                btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
            }
        }
    }
}
