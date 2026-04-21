package com.example.smartdarzi.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartdarzi.R;
import com.example.smartdarzi.database.DatabaseHelper;
import com.example.smartdarzi.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

public class AddAddressActivity extends AppCompatActivity {

    private TextInputEditText etLabel, etDetailed, etPhone;
    private ChipGroup chipGroup;
    private DatabaseHelper db;
    private SessionManager session;
    private int addressId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        MaterialToolbar toolbar = findViewById(R.id.toolbarAddAddress);
        toolbar.setNavigationOnClickListener(v -> finish());

        etLabel = findViewById(R.id.etAddressLabel);
        etDetailed = findViewById(R.id.etDetailedAddress);
        etPhone = findViewById(R.id.etAddressPhone);
        chipGroup = findViewById(R.id.chipGroupLabels);

        db = new DatabaseHelper(this);
        session = new SessionManager(this);

        // Pre-fill if editing
        if (getIntent().hasExtra("address_id")) {
            addressId = getIntent().getIntExtra("address_id", -1);
            String label = getIntent().getStringExtra("label");
            String address = getIntent().getStringExtra("address");
            String phone = getIntent().getStringExtra("phone");

            etLabel.setText(label);
            etDetailed.setText(address);
            etPhone.setText(phone);
            
            toolbar.setTitle("Edit Address");
            ((TextView) findViewById(R.id.tvAddAddressTitle)).setText("Update your location details");
            ((com.google.android.material.button.MaterialButton) findViewById(R.id.btnSaveAddress)).setText("Update Location");
        }

        findViewById(R.id.btnSaveAddress).setOnClickListener(v -> saveAddress());
    }

    private void saveAddress() {
        String label = etLabel.getText().toString().trim();
        
        // Use chip if text is empty
        if (TextUtils.isEmpty(label)) {
            int checkedId = chipGroup.getCheckedChipId();
            if (checkedId == R.id.chipHome) label = "Home";
            else if (checkedId == R.id.chipWork) label = "Work";
            else if (checkedId == R.id.chipOther) label = "Other";
        }

        if (TextUtils.isEmpty(label)) {
            label = "My Location"; // Default fallback
        }

        String detailed = etDetailed.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (TextUtils.isEmpty(detailed) || TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please provide address and phone", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = session.getLoggedInUserId();
        if (userId == -1) {
            Toast.makeText(this, "User session expired", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success;
        if (addressId == -1) {
            success = db.addAddress(userId, label, detailed, phone) != -1;
        } else {
            success = db.updateAddress(addressId, label, detailed, phone);
        }

        if (success) {
            Toast.makeText(this, addressId == -1 ? "Location saved!" : "Location updated!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Error saving location", Toast.LENGTH_SHORT).show();
        }
    }
}
