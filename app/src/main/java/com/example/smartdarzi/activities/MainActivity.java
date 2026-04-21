package com.example.smartdarzi.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.smartdarzi.R;
import com.example.smartdarzi.database.DatabaseHelper;
import com.example.smartdarzi.fragments.AiOutfitSuggestionFragment;
import com.example.smartdarzi.fragments.BasketFragment;
import com.example.smartdarzi.fragments.CategoriesFragment;
import com.example.smartdarzi.fragments.HomeFragment;
import com.example.smartdarzi.fragments.OrdersFragment;
import com.example.smartdarzi.fragments.ProfileFragment;
import com.example.smartdarzi.utils.SessionManager;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static MainActivity instance;

    private BottomNavigationView bottomNav;
    private SessionManager sessionManager;
    private DatabaseHelper dbHelper;

    private Fragment homeFragment;
    private Fragment categoriesFragment;
    private Fragment basketFragment;
    private Fragment ordersFragment;
    private Fragment profileFragment;
    private Fragment aiOutfitFragment;
    private Fragment activeFragment;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        dbHelper.seedMockDataIfNeeded();

        bottomNav = findViewById(R.id.bottomNavigationView);

        homeFragment = new HomeFragment();
        categoriesFragment = new CategoriesFragment();
        basketFragment = new BasketFragment();
        ordersFragment = new OrdersFragment();
        profileFragment = new ProfileFragment();

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, profileFragment, "PROFILE").hide(profileFragment)
                    .add(R.id.container, ordersFragment, "ORDERS").hide(ordersFragment)
                    .add(R.id.container, basketFragment, "BASKET").hide(basketFragment)
                    .add(R.id.container, categoriesFragment, "CATEGORIES").hide(categoriesFragment)
                    .add(R.id.container, homeFragment, "HOME")
                    .commit();
            activeFragment = homeFragment;
        } else {
            homeFragment = getSupportFragmentManager().findFragmentByTag("HOME");
            categoriesFragment = getSupportFragmentManager().findFragmentByTag("CATEGORIES");
            basketFragment = getSupportFragmentManager().findFragmentByTag("BASKET");
            ordersFragment = getSupportFragmentManager().findFragmentByTag("ORDERS");
            profileFragment = getSupportFragmentManager().findFragmentByTag("PROFILE");

            if (homeFragment == null) homeFragment = new HomeFragment();
            if (categoriesFragment == null) categoriesFragment = new CategoriesFragment();
            if (basketFragment == null) basketFragment = new BasketFragment();
            if (ordersFragment == null) ordersFragment = new OrdersFragment();
            if (profileFragment == null) profileFragment = new ProfileFragment();

            if (homeFragment.isVisible()) activeFragment = homeFragment;
            else if (categoriesFragment.isVisible()) activeFragment = categoriesFragment;
            else if (basketFragment.isVisible()) activeFragment = basketFragment;
            else if (ordersFragment.isVisible()) activeFragment = ordersFragment;
            else activeFragment = profileFragment;
        }

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                if (homeFragment != null && homeFragment.isVisible()) activeFragment = homeFragment;
                else if (categoriesFragment != null && categoriesFragment.isVisible()) activeFragment = categoriesFragment;
                else if (basketFragment != null && basketFragment.isVisible()) activeFragment = basketFragment;
                else if (ordersFragment != null && ordersFragment.isVisible()) activeFragment = ordersFragment;
                else if (profileFragment != null && profileFragment.isVisible()) activeFragment = profileFragment;
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                    return;
                }

                if (bottomNav != null && bottomNav.getSelectedItemId() != R.id.nav_home) {
                    bottomNav.setSelectedItemId(R.id.nav_home);
                    return;
                }

                finish();
            }
        });

        bottomNav.setOnItemSelectedListener(item -> {
            clearOverlayBackStack();
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                loadFragment(homeFragment, "HOME");
                return true;
            } else if (id == R.id.nav_categories) {
                loadFragment(categoriesFragment, "CATEGORIES");
                return true;
            } else if (id == R.id.nav_basket) {
                loadFragment(basketFragment, "BASKET");
                return true;
            } else if (id == R.id.nav_orders) {
                loadFragment(ordersFragment, "ORDERS");
                return true;
            } else if (id == R.id.nav_profile) {
                loadFragment(profileFragment, "PROFILE");
                return true;
            }
            return false;
        });

        bottomNav.setSelectedItemId(R.id.nav_home);

        int userId = sessionManager.getLoggedInUserId();
        int count = dbHelper.getBasketCount(userId);
        updateBasketBadge(count);
    }

    private void clearOverlayBackStack() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public void openAiOutfitSuggestion() {
        if (activeFragment == null) {
            return;
        }

        if (aiOutfitFragment == null) {
            aiOutfitFragment = getSupportFragmentManager().findFragmentByTag("AI_OUTFIT");
            if (aiOutfitFragment == null) {
                aiOutfitFragment = new AiOutfitSuggestionFragment();
            }
        }

        if (aiOutfitFragment.isVisible()) {
            return;
        }

        if (!aiOutfitFragment.isAdded()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .hide(activeFragment)
                    .add(R.id.container, aiOutfitFragment, "AI_OUTFIT")
                    .addToBackStack("AI_OUTFIT")
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .hide(activeFragment)
                    .show(aiOutfitFragment)
                    .addToBackStack("AI_OUTFIT")
                    .commit();
        }
        activeFragment = aiOutfitFragment;
    }

    private void loadFragment(Fragment target, String tag) {
        if (target == null) {
            return;
        }
        if (activeFragment == target) {
            return;
        }

        if (!target.isAdded()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .hide(activeFragment)
                    .add(R.id.container, target, tag)
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .hide(activeFragment)
                    .show(target)
                    .commit();
        }
        activeFragment = target;
    }

    public void updateBasketBadge(int count) {
        if (bottomNav == null) {
            return;
        }

        BadgeDrawable badge = bottomNav.getOrCreateBadge(R.id.nav_basket);
        if (count > 0) {
            badge.setVisible(true);
            badge.setNumber(count);
            // Using error color for notification badge
            badge.setBackgroundColor(Color.parseColor("#EF4444"));
        } else {
            badge.setVisible(false);
            badge.clearNumber();
        }
    }

    public void setSelectedTab(int itemId) {
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(itemId);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (instance == this) {
            instance = null;
        }
    }
}
