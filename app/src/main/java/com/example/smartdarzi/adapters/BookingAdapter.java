package com.example.smartdarzi.adapters;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartdarzi.R;
import com.example.smartdarzi.models.Booking;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private final List<Booking> bookings = new ArrayList<>();

    public void submitList(List<Booking> newBookings) {
        bookings.clear();
        bookings.addAll(newBookings);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        String status = booking.getStatus() == null ? "" : booking.getStatus().trim();
        String normalizedStatus = status.toLowerCase(Locale.getDefault());

        holder.tvBookingServiceName.setText(booking.getServiceName());
        holder.tvBookingStatus.setText(status);
        holder.tvBookingDate.setText(holder.itemView.getContext().getString(R.string.appointment_on, booking.getDate()));
        holder.tvBookingTailor.setText(holder.itemView.getContext().getString(
                R.string.booking_tailor,
                booking.getTailorName() != null
                        ? booking.getTailorName()
                        : holder.itemView.getContext().getString(R.string.booking_tailor_pending)));
        holder.tvBookingPrice.setText(String.format(Locale.getDefault(), "₹%.0f", booking.getPrice()));

        int statusColor;
        boolean showCancelButton;
        boolean showViewDetailsButton;

        switch (normalizedStatus) {
            case "completed":
                statusColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.success);
                showCancelButton = false;
                showViewDetailsButton = true;
                break;
            case "cancelled":
                statusColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.error);
                showCancelButton = false;
                showViewDetailsButton = true;
                break;
            default:
                statusColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.primary);
                showCancelButton = true;
                showViewDetailsButton = true;
                break;
        }

        holder.viewStatusBorder.setBackgroundTintList(ColorStateList.valueOf(statusColor));
        holder.tvBookingStatus.setBackgroundTintList(ColorStateList.valueOf(statusColor));
        holder.tvBookingStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
        holder.btnCancelBooking.setVisibility(showCancelButton ? View.VISIBLE : View.GONE);
        holder.btnViewDetails.setVisibility(showViewDetailsButton ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {

        private final View viewStatusBorder;
        private final TextView tvBookingServiceName;
        private final TextView tvBookingTailor;
        private final TextView tvBookingDate;
        private final TextView tvBookingStatus;
        private final TextView tvBookingPrice;
        private final MaterialButton btnCancelBooking;
        private final MaterialButton btnViewDetails;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            viewStatusBorder = itemView.findViewById(R.id.viewStatusBorder);
            tvBookingServiceName = itemView.findViewById(R.id.tvBookingServiceName);
            tvBookingTailor = itemView.findViewById(R.id.tvBookingTailor);
            tvBookingDate = itemView.findViewById(R.id.tvBookingDate);
            tvBookingStatus = itemView.findViewById(R.id.tvBookingStatus);
            tvBookingPrice = itemView.findViewById(R.id.tvBookingPrice);
            btnCancelBooking = itemView.findViewById(R.id.btnCancelBooking);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}

