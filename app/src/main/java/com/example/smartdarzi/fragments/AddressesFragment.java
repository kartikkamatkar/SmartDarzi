package com.example.smartdarzi.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartdarzi.R;
import com.example.smartdarzi.activities.AddAddressActivity;
import com.example.smartdarzi.database.DatabaseHelper;
import com.example.smartdarzi.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class AddressesFragment extends Fragment {

    private RecyclerView rvAddresses;
    private DatabaseHelper db;
    private SessionManager session;
    private ActivityResultLauncher<Intent> addAddressLauncher;
    private View emptyState;

    public AddressesFragment() {
        super(R.layout.fragment_addresses);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addAddressLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        loadAddresses();
                    }
                }
        );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = new DatabaseHelper(requireContext());
        session = new SessionManager(requireContext());

        MaterialToolbar toolbar = view.findViewById(R.id.toolbarAddresses);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
        }

        rvAddresses = view.findViewById(R.id.rvAddresses);
        rvAddresses.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        emptyState = view.findViewById(R.id.layoutAddressEmpty); // Ensure this exists in xml or handle null

        MaterialButton btnAdd = view.findViewById(R.id.btnAddAddress);
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddAddressActivity.class);
            addAddressLauncher.launch(intent);
        });

        loadAddresses();
    }

    private void loadAddresses() {
        int userId = session.getLoggedInUserId();
        List<String[]> addressList = db.getUserAddresses(userId);
        rvAddresses.setAdapter(new AddressAdapter(addressList));
        
        if (emptyState != null) {
            emptyState.setVisibility(addressList.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    private class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressVH> {
        private final List<String[]> addresses;

        AddressAdapter(List<String[]> addresses) {
            this.addresses = addresses;
        }

        @NonNull
        @Override
        public AddressVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address_card, parent, false);
            return new AddressVH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AddressVH holder, int position) {
            String[] addr = addresses.get(position);
            holder.tvLabel.setText(addr[0]);
            holder.tvDetail.setText(addr[1]);
            holder.tvPhone.setText(addr[2]);
            int addressId = Integer.parseInt(addr[3]);

            if (addr[0].toLowerCase().contains("home")) {
                holder.ivIcon.setImageResource(R.drawable.ic_home);
            } else if (addr[0].toLowerCase().contains("office") || addr[0].toLowerCase().contains("work")) {
                holder.ivIcon.setImageResource(R.drawable.ic_bookings);
            } else {
                holder.ivIcon.setImageResource(R.drawable.ic_location);
            }

            holder.ivDelete.setOnClickListener(v -> {
                if (db.deleteAddress(addressId)) {
                    addresses.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, addresses.size());
                    if (addresses.isEmpty() && emptyState != null) {
                        emptyState.setVisibility(View.VISIBLE);
                    }
                    Toast.makeText(requireContext(), "Address removed", Toast.LENGTH_SHORT).show();
                }
            });

            // Make whole card clickable for editing
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), AddAddressActivity.class);
                intent.putExtra("address_id", addressId);
                intent.putExtra("label", addr[0]);
                intent.putExtra("address", addr[1]);
                intent.putExtra("phone", addr[2]);
                addAddressLauncher.launch(intent);
            });
        }

        @Override
        public int getItemCount() {
            return addresses.size();
        }

        class AddressVH extends RecyclerView.ViewHolder {
            TextView tvLabel, tvDetail, tvPhone;
            ImageView ivIcon, ivDelete;
            AddressVH(@NonNull View v) {
                super(v);
                tvLabel = v.findViewById(R.id.tvAddressLabel);
                tvDetail = v.findViewById(R.id.tvAddressDetail);
                tvPhone = v.findViewById(R.id.tvAddressPhone);
                ivIcon = v.findViewById(R.id.ivAddressIcon);
                ivDelete = v.findViewById(R.id.ivDeleteAddress);
            }
        }
    }
}
