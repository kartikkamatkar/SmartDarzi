package com.example.smartdarzi.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartdarzi.R;
import com.example.smartdarzi.database.DatabaseHelper;
import com.example.smartdarzi.models.Measurement;
import com.example.smartdarzi.models.Service;
import com.example.smartdarzi.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    public static final String EXTRA_SERVICE_ID = "service_id";
    public static final String EXTRA_SERVICE_NAME = "service_name";
    public static final String EXTRA_SERVICE_PRICE = "service_price";
    public static final String EXTRA_SERVICE_CATEGORY = "service_category";

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private final Handler handler = new Handler(Looper.getMainLooper());

    // Views
    private ImageView ivProductDetailImage;
    private TextView tvDetailName;
    private TextView tvDetailCategory;
    private RatingBar rbDetailRating;
    private TextView tvRating;
    private TextView tvRatingCount;
    private TextView tvDetailPrice;
    private TextView tvDetailOriginalPrice;
    private TextView tvDiscount;
    private TextView tvDays;
    private TextView tvDetailDescription;
    private TextView tvShowMore;
    private LinearLayout llThumbnails;
    private LinearLayout llColorSwatches;
    private MaterialButton btnAddToBasketDetail;
    private MaterialButton btnBuyNow;
    private MaterialButton btnEditMeasurements;

    // Measurement display TextViews
    private TextView tvMeasChest;
    private TextView tvMeasWaist;
    private TextView tvMeasHip;
    private TextView tvMeasShoulder;
    private TextView tvMeasSleeve;
    private TextView tvMeasHeight;

    private Service currentService;
    private int userId;
    private boolean descriptionExpanded = false;

    // Color swatch data
    private static final int[] SWATCH_COLORS = {
            0xFF1E293B, // Navy
            0xFF7F1D1D, // Maroon
            0xFF65712B, // Olive
            0xFF3B82F6, // Blue
            0xFFE94560, // Red
            0xFF10B981  // Green
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        userId = sessionManager.getLoggedInUserId();

        bindViews();
        setupToolbar();

        currentService = resolveServiceFromIntent(getIntent());

        bindData(currentService);
        setupThumbnails();
        setupColorSwatches();
        setupShowMore();
        setupMeasurements();
        setupAddToBasket();
        setupBuyNow();
        setupShare();
        setupTailorContact();
    }

    // FIX: Reload measurements when user returns from MeasurementsActivity
    @Override
    protected void onResume() {
        super.onResume();
        setupMeasurements();
    }

    private void bindViews() {
        ivProductDetailImage = findViewById(R.id.ivProductDetailImage);
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailCategory = findViewById(R.id.tvDetailCategory);
        rbDetailRating = findViewById(R.id.rbDetailRating);
        tvRating = findViewById(R.id.tvRating);
        tvRatingCount = findViewById(R.id.tvRatingCount);
        tvDetailPrice = findViewById(R.id.tvDetailPrice);
        tvDetailOriginalPrice = findViewById(R.id.tvDetailOriginalPrice);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvDays = findViewById(R.id.tvDays);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        tvShowMore = findViewById(R.id.tvShowMore);
        llThumbnails = findViewById(R.id.llThumbnails);
        llColorSwatches = findViewById(R.id.llColorSwatches);
        btnAddToBasketDetail = findViewById(R.id.btnAddToBasketDetail);
        btnBuyNow = findViewById(R.id.btnBuyNow);
        btnEditMeasurements = findViewById(R.id.btnEditMeasurements);

        // Measurement grid values
        tvMeasChest = findViewById(R.id.tvMeasChest);
        tvMeasWaist = findViewById(R.id.tvMeasWaist);
        tvMeasHip = findViewById(R.id.tvMeasHip);
        tvMeasShoulder = findViewById(R.id.tvMeasShoulder);
        tvMeasSleeve = findViewById(R.id.tvMeasSleeve);
        tvMeasHeight = findViewById(R.id.tvMeasHeight);

        // Tailor Contact
        findViewById(R.id.btnCallTailorDetail).setOnClickListener(v -> {
            String phone = ((TextView) findViewById(R.id.tvTailorContact)).getText().toString();
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(android.net.Uri.parse("tel:" + phone));
            startActivity(intent);
        });
    }

    private void setupTailorContact() {
        // Additional localized status or dynamic tailor info could go here
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbarDetail);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }

    @NonNull
    private Service resolveServiceFromIntent(@NonNull Intent intent) {
        int serviceId = intent.getIntExtra(EXTRA_SERVICE_ID, -1);
        String serviceName = intent.getStringExtra(EXTRA_SERVICE_NAME);
        double servicePrice = intent.getDoubleExtra(EXTRA_SERVICE_PRICE, 0.0);
        String serviceCategory = intent.getStringExtra(EXTRA_SERVICE_CATEGORY);

        Service dbService = dbHelper.getServiceById(serviceId);
        if (dbService != null) {
            return dbService;
        }

        String fallbackName = TextUtils.isEmpty(serviceName) ? "Custom Service" : serviceName;
        String fallbackCategory = TextUtils.isEmpty(serviceCategory) ? "Tailoring" : serviceCategory;
        double fallbackPrice = servicePrice > 0 ? servicePrice : 499.0;
        return new Service(Math.max(serviceId, 1), fallbackName,
                getString(R.string.default_service_description), fallbackPrice,
                fallbackCategory, 4.5, 3, 1);
    }

    // ===== Drawable Mapping =====

    /**
     * Maps a product category to the best available local drawable resource.
     */
    private int getHeroDrawableForCategory(String category) {
        if (category == null || currentService == null) return R.drawable.shirt1;
        int pos = currentService.getId();
        switch (category.toLowerCase(Locale.getDefault())) {
            case "shirt": case "shirts":
                return getShirtDrawable(pos);
            case "casual": case "casuals":
                int[] casuals = {R.drawable.casual1, R.drawable.casual2, R.drawable.casual3, R.drawable.casual4};
                return casuals[pos % casuals.length];
            case "suit": case "suits": case "formal": case "formals":
                int[] suits = {R.drawable.suit1, R.drawable.formal1, R.drawable.formal2, R.drawable.formal3};
                return suits[pos % suits.length];
            case "pant": case "pants": case "trouser": case "trousers": case "jeans": case "denim":
                int[] jeans = {R.drawable.jeans1, R.drawable.jeans2, R.drawable.jeans3, R.drawable.jeans4};
                return jeans[pos % jeans.length];
            case "lehenga": case "lehengas":
                int[] lehengas = {R.drawable.lehenga1, R.drawable.lehenga2, R.drawable.designerblouse3, R.drawable.designerblouse4};
                return lehengas[pos % lehengas.length];
            case "blouse": case "blouses": case "designer blouse":
                int[] blouses = {R.drawable.designerblouse1, R.drawable.designerblouse2, R.drawable.designerblouse3, R.drawable.designerblouse4};
                return blouses[pos % blouses.length];
            case "wedding wear": case "bridal": case "sherwani":
                int[] wedding = {R.drawable.sherwani1, R.drawable.lehenga1, R.drawable.suit1, R.drawable.designerblouse2};
                return wedding[pos % wedding.length];
            case "kurta": case "kurtas":
                int[] kurtas = {R.drawable.kurta_design1, R.drawable.kurta_design2, R.drawable.kurta_design3, R.drawable.kurta_design4};
                return kurtas[pos % kurtas.length];
            case "saree": case "sari": case "sadi":
                return R.drawable.saree1;
            case "alterations":
                return R.drawable.alteration1;
            default:
                return R.drawable.shirt1;
        }
    }

    private int getShirtDrawable(int pos) {
        int[] shirts = {R.drawable.shirt1, R.drawable.shirt2, R.drawable.shirt3, R.drawable.shirt4,
                R.drawable.shirt5, R.drawable.shirt6, R.drawable.shirt7, R.drawable.shirt8};
        return shirts[pos % shirts.length];
    }

    private int[] getThumbnailsForCategory(String category) {
        if (category == null || currentService == null) return new int[]{R.drawable.shirt1, R.drawable.shirt2, R.drawable.shirt3, R.drawable.shirt4};
        int pos = currentService.getId();
        switch (category.toLowerCase(Locale.getDefault())) {
            case "shirt": case "shirts":
                return new int[]{R.drawable.shirt1, R.drawable.shirt2, R.drawable.shirt3, R.drawable.shirt4};
            case "casual": case "casuals":
                return new int[]{R.drawable.casual1, R.drawable.casual2, R.drawable.casual3, R.drawable.casual4};
            case "suit": case "suits": case "formal": case "formals":
                return new int[]{R.drawable.suit1, R.drawable.formal1, R.drawable.formal2, R.drawable.formal3};
            case "jeans": case "denim": case "pant": case "pants": case "trouser": case "trousers":
                return new int[]{R.drawable.jeans1, R.drawable.jeans2, R.drawable.jeans3, R.drawable.jeans4};
            case "lehenga": case "lehengas":
                return new int[]{R.drawable.lehenga1, R.drawable.lehenga2, R.drawable.designerblouse3, R.drawable.designerblouse4};
            case "blouse": case "blouses": case "designer blouse":
                return new int[]{R.drawable.designerblouse1, R.drawable.designerblouse2, R.drawable.designerblouse3, R.drawable.designerblouse4};
            case "wedding wear": case "bridal": case "sherwani":
                return new int[]{R.drawable.sherwani1, R.drawable.lehenga1, R.drawable.suit1, R.drawable.designerblouse2};
            case "kurta": case "kurtas":
                return new int[]{R.drawable.kurta_design1, R.drawable.kurta_design2, R.drawable.kurta_design3, R.drawable.kurta_design4};
            case "saree": case "sari": case "sadi":
                return new int[]{R.drawable.saree1, R.drawable.lehenga2, R.drawable.designerblouse1, R.drawable.lehenga1};
            default:
                return new int[]{R.drawable.shirt1, R.drawable.shirt2, R.drawable.shirt3, R.drawable.shirt4};
        }
    }

    // ===== Data Binding =====

    private void bindData(@NonNull Service service) {
        // Set product name
        if (tvDetailName != null) {
            tvDetailName.setText(service.getName());
        }

        // Set hero image from local drawables
        if (ivProductDetailImage != null) {
            ivProductDetailImage.setImageResource(getHeroDrawableForCategory(service.getCategory()));
        }

        // Category badge
        if (tvDetailCategory != null) {
            tvDetailCategory.setText(service.getCategory());
        }

        // Rating
        double displayRating = service.getRating() > 0 ? service.getRating() : 4.5;
        if (rbDetailRating != null) {
            rbDetailRating.setRating((float) displayRating);
        }
        if (tvRating != null) {
            tvRating.setText(formatOneDecimal(displayRating));
        }
        if (tvRatingCount != null) {
            tvRatingCount.setText(String.format(Locale.getDefault(), "(%s)", formatOneDecimal(displayRating)));
        }

        // Price
        if (tvDetailPrice != null) {
            tvDetailPrice.setText(String.format(Locale.getDefault(), "₹%.0f", service.getPrice()));
        }

        // Original price (20% markup)
        double originalPrice = service.getPrice() * 1.2;
        if (tvDetailOriginalPrice != null) {
            tvDetailOriginalPrice.setText(String.format(Locale.getDefault(), "₹%.0f", originalPrice));
            tvDetailOriginalPrice.setPaintFlags(tvDetailOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        // Discount badge
        if (tvDiscount != null) {
            tvDiscount.setText(getString(R.string.discount_label));
        }

        // Turnaround days
        if (tvDays != null) {
            tvDays.setText(getString(R.string.turnaround_days, service.getTurnaroundDays()));
        }

        // Description
        if (tvDetailDescription != null) {
            tvDetailDescription.setText(TextUtils.isEmpty(service.getDescription())
                    ? getString(R.string.default_service_description)
                    : service.getDescription());
        }
    }

    // ===== Thumbnails =====

    private void setupThumbnails() {
        if (llThumbnails == null || currentService == null) return;

        int[] thumbIds = getThumbnailsForCategory(currentService.getCategory());
        // Clear existing thumbnails set in XML
        llThumbnails.removeAllViews();

        int sizePx = dpToPx(64);
        int marginPx = dpToPx(6);

        for (int i = 0; i < thumbIds.length; i++) {
            ImageView thumb = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sizePx, sizePx);
            if (i < thumbIds.length - 1) {
                params.setMarginEnd(marginPx);
            }
            thumb.setLayoutParams(params);
            thumb.setScaleType(ImageView.ScaleType.CENTER_CROP);
            thumb.setPadding(dpToPx(2), dpToPx(2), dpToPx(2), dpToPx(2));
            thumb.setImageResource(thumbIds[i]);

            // First thumbnail is selected by default
            if (i == 0) {
                thumb.setBackgroundResource(R.drawable.bg_thumbnail_selected);
            }

            final int drawableRes = thumbIds[i];
            final int index = i;
            thumb.setOnClickListener(v -> {
                // Update hero image
                if (ivProductDetailImage != null) {
                    ivProductDetailImage.setImageResource(drawableRes);
                }
                // Update selection state
                for (int j = 0; j < llThumbnails.getChildCount(); j++) {
                    View child = llThumbnails.getChildAt(j);
                    child.setBackgroundResource(j == index ? R.drawable.bg_thumbnail_selected : 0);
                }
            });

            llThumbnails.addView(thumb);
        }
    }

    // ===== Color Swatches =====

    private void setupColorSwatches() {
        if (llColorSwatches == null) return;

        int sizePx = dpToPx(36);
        int marginPx = dpToPx(8);
        int selectedIndex = 0;

        for (int i = 0; i < SWATCH_COLORS.length; i++) {
            View swatch = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sizePx, sizePx);
            params.setMarginEnd(marginPx);
            swatch.setLayoutParams(params);

            GradientDrawable circle = new GradientDrawable();
            circle.setShape(GradientDrawable.OVAL);
            circle.setColor(SWATCH_COLORS[i]);

            // First is selected
            if (i == selectedIndex) {
                circle.setStroke(dpToPx(3), Color.WHITE);
            }

            swatch.setBackground(circle);

            final int index = i;
            swatch.setOnClickListener(v -> {
                // Update selection ring
                for (int j = 0; j < llColorSwatches.getChildCount(); j++) {
                    View child = llColorSwatches.getChildAt(j);
                    GradientDrawable bg = new GradientDrawable();
                    bg.setShape(GradientDrawable.OVAL);
                    bg.setColor(SWATCH_COLORS[j]);
                    if (j == index) {
                        bg.setStroke(dpToPx(3), Color.WHITE);
                    }
                    child.setBackground(bg);
                }
            });

            llColorSwatches.addView(swatch);
        }
    }

    // ===== Show More / Less =====

    private void setupShowMore() {
        if (tvShowMore == null || tvDetailDescription == null) return;

        tvShowMore.setOnClickListener(v -> {
            if (descriptionExpanded) {
                tvDetailDescription.setMaxLines(4);
                tvShowMore.setText(R.string.show_more);
            } else {
                tvDetailDescription.setMaxLines(Integer.MAX_VALUE);
                tvShowMore.setText(R.string.show_less);
            }
            descriptionExpanded = !descriptionExpanded;
        });
    }

    // ===== Measurements =====

    private void setupMeasurements() {
        try {
            Measurement latest = dbHelper.getLatestMeasurementForUser(userId);
            if (latest != null) {
                setMeasurementText(tvMeasChest, latest.getChest());
                setMeasurementText(tvMeasWaist, latest.getWaist());
                setMeasurementText(tvMeasHip, latest.getHip());
                setMeasurementText(tvMeasShoulder, latest.getShoulder());
                setMeasurementText(tvMeasSleeve, latest.getSleeveLength());
                setMeasurementText(tvMeasHeight, latest.getHeight());
            }
        } catch (Exception e) {
            // SQLiteException or table missing — show empty state
            e.printStackTrace();
        }

        if (btnEditMeasurements != null) {
            btnEditMeasurements.setOnClickListener(v -> {
                // FIX: Launch MeasurementsActivity so user can actually edit measurements
                Intent intent = new Intent(this, MeasurementsActivity.class);
                startActivity(intent);
            });
        }
    }

    private void setMeasurementText(TextView tv, String value) {
        if (tv == null) return;
        if (TextUtils.isEmpty(value)) {
            tv.setText(R.string.not_set);
            tv.setAlpha(0.5f);
        } else {
            tv.setText(value + "\"");
            tv.setAlpha(1.0f);
        }
    }

    // ===== Add to Basket =====

    private void setupAddToBasket() {
        if (btnAddToBasketDetail == null) return;

        com.example.smartdarzi.utils.ClickAnimHelper.applySmoothClick(btnAddToBasketDetail);
        btnAddToBasketDetail.setOnClickListener(v -> {
            int currentUserId = sessionManager.getLoggedInUserId();
            if (currentUserId <= 0) {
                Snackbar.make(v, "Please login again", Snackbar.LENGTH_SHORT).show();
                return;
            }

            boolean added = dbHelper.addToBasket(currentUserId, currentService.getId(), 1);
            if (added) {
                Snackbar.make(v, currentService.getName() + " added to basket!", Snackbar.LENGTH_SHORT).show();
                btnAddToBasketDetail.setEnabled(false);
                btnAddToBasketDetail.setText(R.string.detail_added_to_cart);

                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(() -> {
                    btnAddToBasketDetail.setText(R.string.add_to_cart);
                    btnAddToBasketDetail.setEnabled(true);
                }, 2000);
            } else {
                Snackbar.make(v, "Unable to add to basket", Snackbar.LENGTH_SHORT).show();
            }

            MainActivity activity = MainActivity.getInstance();
            if (activity != null) {
                activity.updateBasketBadge(dbHelper.getBasketCount(currentUserId));
            }
        });
    }

    // ===== Buy Now =====

    private void setupBuyNow() {
        if (btnBuyNow == null) return;

        btnBuyNow.setOnClickListener(v -> {
            int currentUserId = sessionManager.getLoggedInUserId();
            if (currentUserId <= 0) {
                Snackbar.make(v, "Please login again", Snackbar.LENGTH_SHORT).show();
                return;
            }

            // Add to basket
            dbHelper.addToBasket(currentUserId, currentService.getId(), 1);

            MainActivity activity = MainActivity.getInstance();
            if (activity != null) {
                activity.updateBasketBadge(dbHelper.getBasketCount(currentUserId));
                // Navigate to Cart/Basket tab
                activity.setSelectedTab(R.id.nav_basket);
                finish(); // Close detail and show cart in main
            } else {
                Snackbar.make(v, "Proceeding to basket...", Snackbar.LENGTH_SHORT).show();
                finish(); 
            }
        });
    }

    // ===== Share =====

    private void setupShare() {
        ImageView ivShare = findViewById(R.id.ivShare);
        if (ivShare == null) return;

        ivShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareText = "Check out " + currentService.getName()
                    + " on SmartDarzi! ₹" + String.format(Locale.getDefault(), "%.0f", currentService.getPrice());
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
        });
    }

    // ===== Utilities =====

    private String formatOneDecimal(double value) {
        return String.format(Locale.getDefault(), "%.1f", value);
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
