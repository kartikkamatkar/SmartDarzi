package com.example.smartdarzi.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.smartdarzi.R;
import com.example.smartdarzi.activities.MainActivity;
import com.example.smartdarzi.activities.ProductDetailActivity;
import com.example.smartdarzi.adapters.ServiceAdapter;
import com.example.smartdarzi.database.DatabaseHelper;
import com.example.smartdarzi.models.Service;
import com.example.smartdarzi.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private TextView tvGreeting;
    private TextView tvUserName;
    private TextView tvSubtitle;
    private ShapeableImageView ivAvatar;
    private android.widget.EditText etHomeSearch;
    private ViewPager2 bannerViewPager;
    private LinearLayout bannerDots;
    private RecyclerView rvServices;

    private DatabaseHelper dbHelper;

    private final List<CarouselItem> carouselItems = new ArrayList<>();
    private final Handler autoScrollHandler = new Handler(Looper.getMainLooper());
    private final Runnable autoScrollTask = new Runnable() {
        @Override
        public void run() {
            if (!isAdded() || bannerViewPager == null || bannerViewPager.getAdapter() == null) {
                return;
            }
            int count = bannerViewPager.getAdapter().getItemCount();
            if (count <= 1) {
                return;
            }
            int nextItem = (bannerViewPager.getCurrentItem() + 1) % count;
            bannerViewPager.setCurrentItem(nextItem, true);
            autoScrollHandler.postDelayed(this, 3000);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvSubtitle = view.findViewById(R.id.tvSubtitle);
        ivAvatar = view.findViewById(R.id.ivAvatar);
        bannerViewPager = view.findViewById(R.id.bannerViewPager);
        bannerDots = view.findViewById(R.id.bannerDots);
        rvServices = view.findViewById(R.id.rvServices);
        etHomeSearch = view.findViewById(R.id.etHomeSearch);

        setupSearchLogic();
        setGreetingAndUser();
        setupBanner();
        setupCategories(view);
        setupServices();

        TextView tvSeeAllCategories = view.findViewById(R.id.tvSeeAllCategories);
        TextView tvViewAllServices = view.findViewById(R.id.tvViewAllServices);
        tvSeeAllCategories.setOnClickListener(v -> openCategories(null));
        tvViewAllServices.setOnClickListener(v -> openCategories(null));
        
        View cardHomeSearch = view.findViewById(R.id.cardHomeSearch);
        View.OnClickListener launchTailorSearch = v -> {
            startActivity(new Intent(requireContext(), com.example.smartdarzi.activities.FindTailorActivity.class));
        };
        if (cardHomeSearch != null) cardHomeSearch.setOnClickListener(launchTailorSearch);
        if (etHomeSearch != null) {
            etHomeSearch.setFocusable(false);
            etHomeSearch.setOnClickListener(launchTailorSearch);
        }

        View cardHomeAiStyle = view.findViewById(R.id.cardHomeAiStyle);
        View btnHomeAiOutfit = view.findViewById(R.id.btnHomeAiOutfit);

        View.OnClickListener openAiAction = v -> {
            if (requireActivity() instanceof MainActivity) {
                ((MainActivity) requireActivity()).openAiOutfitSuggestion();
            }
        };

        if (cardHomeAiStyle != null) cardHomeAiStyle.setOnClickListener(openAiAction);
        if (btnHomeAiOutfit != null) btnHomeAiOutfit.setOnClickListener(openAiAction);

        return view;
    }

    private void setGreetingAndUser() {
        SessionManager session = new SessionManager(requireContext());
        String name = session.getUserDetails().get(SessionManager.KEY_NAME);
        if (name != null && !name.isEmpty()) {
            tvUserName.setText(name);
        } else {
            tvUserName.setText("Welcome!");
        }
        tvGreeting.setText("Hello,");
        tvSubtitle.setText(getString(R.string.home_subtitle_tailor));
        ivAvatar.setImageResource(R.drawable.ic_profile);
    }

    private void setupBanner() {
        carouselItems.clear();
        carouselItems.add(new CarouselItem("Premium Tailoring", "Bespoke fits crafted to perfection", "#0F172A"));
        carouselItems.add(new CarouselItem("Festive Offer", "Save more on your first custom order", "#111827"));
        carouselItems.add(new CarouselItem("Gold Member Perk", "Free home measurement on select orders", "#1F2937"));

        BannerAdapter adapter = new BannerAdapter(carouselItems);
        bannerViewPager.setAdapter(adapter);

        setupBannerDots(carouselItems.size());
        bannerViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateDots(position);
            }
        });
    }

    private void setupBannerDots(int count) {
        bannerDots.removeAllViews();
        for (int i = 0; i < count; i++) {
            View dot = new View(requireContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(16, 6);
            params.setMargins(4, 0, 4, 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(R.drawable.bg_status_badge);
            dot.setBackgroundTintList(i == 0
                    ? getResources().getColorStateList(R.color.black, null)
                    : getResources().getColorStateList(R.color.gray_300, null));
            bannerDots.addView(dot);
        }
    }

    private void updateDots(int selectedPosition) {
        for (int i = 0; i < bannerDots.getChildCount(); i++) {
            View dot = bannerDots.getChildAt(i);
            dot.setBackgroundTintList(i == selectedPosition
                    ? getResources().getColorStateList(R.color.black, null)
                    : getResources().getColorStateList(R.color.gray_300, null));
        }
    }

    private void setupCategories(@NonNull View root) {
        // Updated icons with better emojis for shirts and pants as requested
        setupCategoryCard(root.findViewById(R.id.catAll), getString(R.string.cat_all), "⭐", null);
        setupCategoryCard(root.findViewById(R.id.catShirt), getString(R.string.cat_shirt), "👕", "Shirts");
        setupCategoryCard(root.findViewById(R.id.catPants), getString(R.string.cat_pants), "👖", "Pants");
        setupCategoryCard(root.findViewById(R.id.catSuit), getString(R.string.cat_suit), "🤵", "Suits");
        setupCategoryCard(root.findViewById(R.id.catKurta), getString(R.string.cat_kurta), "🧥", "Kurtas");
        setupCategoryCard(root.findViewById(R.id.catBlouse), getString(R.string.cat_blouse), "👚", "Blouses");
        setupCategoryCard(root.findViewById(R.id.catLehenga), getString(R.string.cat_lehenga), "👘", "Lehengas");
        setupCategoryCard(root.findViewById(R.id.catSaree), "Saree", "🥻", "Saree");
        setupCategoryCard(root.findViewById(R.id.catWedding), "Sherwani", "🕌", "Wedding Wear");
        setupCategoryCard(root.findViewById(R.id.catAlterations), getString(R.string.cat_alterations), "✂️", "Alterations");
    }

    private void setupSearchLogic() {
        // Search logic moved to FindTailorActivity as per user request
    }

    private void setupCategoryCard(@Nullable View cardRoot, @NonNull String title, @NonNull String iconText, @Nullable String filter) {
        if (cardRoot == null) {
            return;
        }
        TextView tvTitle = cardRoot.findViewById(R.id.tvHomeCategoryTitle);
        TextView tvIcon = cardRoot.findViewById(R.id.tvHomeCategoryIcon);

        if (tvTitle != null) {
            tvTitle.setText(title);
        }
        if (tvIcon != null) {
            tvIcon.setText(iconText);
            tvIcon.setBackgroundResource(R.drawable.bg_category_icon);
        }

        cardRoot.setOnClickListener(v -> openCategories(filter));
    }

    private void openCategories(@Nullable String filter) {
        CategoriesFragment.setPendingFilter(filter);
        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_categories);
        }
    }

    private void setupServices() {
        List<Service> services = dbHelper.getAllServices();
        ServiceAdapter adapter = new ServiceAdapter(requireContext(), services, service -> {
            Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
            intent.putExtra(ProductDetailActivity.EXTRA_SERVICE_ID, service.getId());
            intent.putExtra(ProductDetailActivity.EXTRA_SERVICE_NAME, service.getName());
            intent.putExtra(ProductDetailActivity.EXTRA_SERVICE_PRICE, service.getPrice());
            intent.putExtra(ProductDetailActivity.EXTRA_SERVICE_CATEGORY, service.getCategory());
            startActivity(intent);
        });

        rvServices.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvServices.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        autoScrollHandler.removeCallbacks(autoScrollTask);
        autoScrollHandler.postDelayed(autoScrollTask, 3000);
    }

    @Override
    public void onPause() {
        super.onPause();
        autoScrollHandler.removeCallbacks(autoScrollTask);
    }

    private static class CarouselItem {
        final String title;
        final String subtitle;
        final String colorHex;

        CarouselItem(String title, String subtitle, String colorHex) {
            this.title = title;
            this.subtitle = subtitle;
            this.colorHex = colorHex;
        }
    }

    private class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

        private final List<CarouselItem> items;

        BannerAdapter(List<CarouselItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
            return new BannerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
            CarouselItem item = items.get(position);
            holder.tvBannerTitle.setText(item.title);
            holder.tvBannerSubtitle.setText(item.subtitle);
            holder.bannerRoot.setBackgroundColor(Color.parseColor(item.colorHex));
            holder.btnBannerCta.setOnClickListener(v -> openCategories(null));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class BannerViewHolder extends RecyclerView.ViewHolder {
            final View bannerRoot;
            final TextView tvBannerTitle;
            final TextView tvBannerSubtitle;
            final com.google.android.material.button.MaterialButton btnBannerCta;

            BannerViewHolder(@NonNull View itemView) {
                super(itemView);
                bannerRoot = itemView.findViewById(R.id.bannerRoot);
                tvBannerTitle = itemView.findViewById(R.id.tvBannerTitle);
                tvBannerSubtitle = itemView.findViewById(R.id.tvBannerSubtitle);
                btnBannerCta = itemView.findViewById(R.id.btnBannerCta);
            }
        }
    }
}
