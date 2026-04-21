package com.example.smartdarzi.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.smartdarzi.R;
import com.example.smartdarzi.activities.LoginActivity;
import com.example.smartdarzi.database.DatabaseHelper;
import com.example.smartdarzi.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.HashMap;

public class ProfileFragment extends Fragment {

    private TextView tvProfileName, tvProfileEmail, tvProfilePhone;
    private TextView tvStatOrders, tvStatMeasurements, tvStatSaved;
    private TextView tvAvatarInitials;
    private MaterialButton btnLogout, btnDeleteAccount;
    private View menuEditProfile, menuMeasurements, menuAddresses, menuSettings;
    private View menuHelp, menuPrivacy, menuAbout;

    private SessionManager sessionManager;
    private DatabaseHelper dbHelper;
    private int currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sessionManager = new SessionManager(requireContext());
        dbHelper  = new DatabaseHelper(requireContext());

        currentUserId = sessionManager.getLoggedInUserId();
        if (currentUserId <= 0) currentUserId = 1;

        initViews(view);
        setupUserData();
        setupMenuOptions();

        btnLogout.setOnClickListener(v -> showLogoutDialog());
        btnDeleteAccount.setOnClickListener(v -> showDeleteAccountDialog());

        return view;
    }

    // FIX: Reload profile stats every time fragment becomes visible (e.g. after edit or order)
    @Override
    public void onResume() {
        super.onResume();
        if (tvStatOrders != null) {
            setupUserData(); // Refresh name, email, stats
        }
    }

    private void initViews(View view) {
        tvProfileName  = view.findViewById(R.id.tvProfileName);
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        tvAvatarInitials = view.findViewById(R.id.tvAvatarInitials);
        tvStatOrders = view.findViewById(R.id.tvStatOrders);
        tvStatMeasurements = view.findViewById(R.id.tvStatMeasurements);
        tvStatSaved  = view.findViewById(R.id.tvStatSaved);

        menuEditProfile  = view.findViewById(R.id.menuEditProfile);
        menuMeasurements = view.findViewById(R.id.menuMeasurements);
        menuAddresses    = view.findViewById(R.id.menuAddresses);
        menuSettings     = view.findViewById(R.id.menuSettings);
        menuHelp         = view.findViewById(R.id.menuHelp);
        menuPrivacy      = view.findViewById(R.id.menuPrivacy);
        menuAbout        = view.findViewById(R.id.menuAbout);

        btnLogout        = view.findViewById(R.id.btnLogout);
        btnDeleteAccount = view.findViewById(R.id.btnDeleteAccount);

        // FIX: Set gradient background on avatar circle
        View viewAvatarBorder = view.findViewById(R.id.viewAvatarBorder);
        if (viewAvatarBorder != null) {
            GradientDrawable grad = new GradientDrawable(
                    GradientDrawable.Orientation.TL_BR,
                    new int[]{Color.parseColor("#6C63FF"), Color.parseColor("#E94560")});
            grad.setShape(GradientDrawable.OVAL);
            viewAvatarBorder.setBackground(grad);
        }
    }

    private void setupUserData() {
        HashMap<String, String> userDetails = sessionManager.getUserDetails();

        String name  = userDetails.get(SessionManager.KEY_NAME);
        String email = userDetails.get(SessionManager.KEY_EMAIL);

        // Name + initials
        if (name != null && !name.isEmpty()) {
            tvProfileName.setText(name);
            String[] parts = name.split(" ");
            StringBuilder initials = new StringBuilder();
            for (String part : parts) {
                if (!part.isEmpty() && initials.length() < 2) {
                    initials.append(part.charAt(0));
                }
            }
            tvAvatarInitials.setText(initials.toString().toUpperCase());
        } else {
            tvProfileName.setText("Guest User");
            tvAvatarInitials.setText("G");
        }

        if (email != null && !email.isEmpty()) {
            tvProfileEmail.setText(email);
        } else {
            tvProfileEmail.setText("No email set");
        }

        // FIX: Load real order count from DatabaseHelper (bookings table used by BasketFragment)
        try {
            int bookingCount = dbHelper.getUserOrderCount(currentUserId);
            tvStatOrders.setText(String.valueOf(bookingCount));
        } catch (Exception e) {
            tvStatOrders.setText("0");
        }

        // FIX: Load measurement count
        try {
            int measCount = dbHelper.getUserMeasurementCount(currentUserId);
            tvStatMeasurements.setText(String.valueOf(measCount));
        } catch (Exception e) {
            tvStatMeasurements.setText("0");
        }

        // Basket item count as "Saved"
        try {
            int basketCount = dbHelper.getBasketCount(currentUserId);
            tvStatSaved.setText(String.valueOf(basketCount));
        } catch (Exception e) {
            tvStatSaved.setText("0");
        }
    }

    private void setupMenuOptions() {
        // Edit Profile
        setMenu(menuEditProfile, R.string.menu_edit_profile, R.drawable.ic_profile,
                () -> openFragment(new EditProfileFragment()));

        // Measurements
        setMenu(menuMeasurements, R.string.menu_measurements, R.drawable.ic_services,
                () -> openFragment(new MeasurementsFragment()));

        // Addresses
        setMenu(menuAddresses, R.string.menu_addresses, android.R.drawable.ic_menu_mylocation,
                () -> openFragment(new AddressesFragment()));

        // Settings
        setMenu(menuSettings, R.string.menu_settings, android.R.drawable.ic_menu_preferences,
                () -> openFragment(new SettingsFragment()));

        // Help
        setMenu(menuHelp, R.string.help_support, android.R.drawable.ic_menu_help,
                () -> openFragment(new HelpSupportFragment()));

        // Privacy
        setMenu(menuPrivacy, R.string.privacy_policy, android.R.drawable.ic_menu_view,
                () -> openFragment(new PrivacyPolicyFragment()));

        // About
        setMenu(menuAbout, R.string.about_us, android.R.drawable.ic_menu_info_details,
                () -> openFragment(new AboutUsFragment()));
    }

    /** Helper to set title, icon, and click on a menu row safely */
    private void setMenu(View menuView, int titleRes, int iconRes, Runnable action) {
        if (menuView == null) return;
        TextView tv = menuView.findViewById(R.id.tvMenuTitle);
        ImageView iv = menuView.findViewById(R.id.ivMenuIcon);
        if (tv != null) tv.setText(titleRes);
        if (iv != null) iv.setImageResource(iconRes);
        menuView.setOnClickListener(v -> action.run());
    }

    private void openFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void showLogoutDialog() {
        new MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                .setTitle(R.string.logout)
                .setMessage(R.string.logout_confirm)
                .setPositiveButton("Logout", (dialog, which) -> {
                    sessionManager.clearSession();
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteAccountDialog() {
        new MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    try {
                        dbHelper.deleteUser(currentUserId);
                        sessionManager.clearSession();
                        Intent intent = new Intent(requireActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        requireActivity().finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Failed to delete account", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
