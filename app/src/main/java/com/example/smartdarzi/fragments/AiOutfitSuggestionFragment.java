package com.example.smartdarzi.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartdarzi.R;
import com.example.smartdarzi.adapters.DesignItemAdapter;
import com.example.smartdarzi.models.DesignItem;

import java.util.ArrayList;
import java.util.List;

public class AiOutfitSuggestionFragment extends Fragment {

    private com.example.smartdarzi.database.DatabaseHelper dbHelper;
    private com.example.smartdarzi.utils.SessionManager session;

    private RecyclerView rvWedding, rvFormal, rvDaily, rvEvent;
    private LinearLayout sectionWedding, sectionFormal, sectionDaily, sectionEvent;
    private TextView tvLoading;

    public AiOutfitSuggestionFragment() {
        super(R.layout.fragment_home_style_showcase);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new com.example.smartdarzi.database.DatabaseHelper(requireContext());
        session = new com.example.smartdarzi.utils.SessionManager(requireContext());

        tvLoading = view.findViewById(R.id.tvHomeStylesEmpty);
        
        // Sections
        sectionWedding = view.findViewById(R.id.sectionWedding);
        sectionFormal = view.findViewById(R.id.sectionFormal);
        sectionDaily = view.findViewById(R.id.sectionDaily);
        sectionEvent = view.findViewById(R.id.sectionEvent);

        // RecyclerViews
        rvWedding = view.findViewById(R.id.rvWeddingStyles);
        rvFormal = view.findViewById(R.id.rvFormalStyles);
        rvDaily = view.findViewById(R.id.rvDailyStyles);
        rvEvent = view.findViewById(R.id.rvEventStyles);

        setupRecycler(rvWedding);
        setupRecycler(rvFormal);
        setupRecycler(rvDaily);
        setupRecycler(rvEvent);

        View btnRegenerate = view.findViewById(R.id.tvExploreAllServices);
        btnRegenerate.setOnClickListener(v -> generateAll());

        generateAll();
    }

    private void setupRecycler(RecyclerView rv) {
        rv.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rv.setAdapter(new DesignItemAdapter());
    }

    private void generateAll() {
        tvLoading.setVisibility(View.VISIBLE);
        sectionWedding.setVisibility(View.GONE);
        sectionFormal.setVisibility(View.GONE);
        sectionDaily.setVisibility(View.GONE);
        sectionEvent.setVisibility(View.GONE);

        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            if (!isAdded()) return;
            
            int userId = session.getLoggedInUserId();
            com.example.smartdarzi.models.Measurement m = dbHelper.getLatestMeasurementForUser(userId);
            String mExtra = (m != null && m.getChest() != null) ? "Tailored for your " + m.getChest() + "\" frame" : "Ideal for your fit";

            // Marriage / Wedding Pool
            List<DesignItem> weddingPool = new ArrayList<>();
            weddingPool.add(new DesignItem(R.drawable.sherwani1, "Royal Ivory Sherwani", 4500, 4.9f, "AI Suggested: Perfect for Marriage • " + mExtra));
            weddingPool.add(new DesignItem(R.drawable.lehenga1, "Zardosi Bridal Lehenga", 8900, 4.8f, "AI Suggested: Most Liked Festive Wear"));
            weddingPool.add(new DesignItem(R.drawable.lehenga2, "Pastel Floral Lehenga", 7500, 4.7f, "AI Suggested: Trending for Sangeet"));
            java.util.Collections.shuffle(weddingPool);
            ((DesignItemAdapter)rvWedding.getAdapter()).submitList(weddingPool.subList(0, Math.min(2, weddingPool.size())));
            sectionWedding.setVisibility(View.VISIBLE);

            // Formal Pool
            List<DesignItem> formalPool = new ArrayList<>();
            formalPool.add(new DesignItem(R.drawable.suit1, "Charcoal Three Piece Suite", 3500, 4.7f, "AI Suggested: Formal Sharpness • " + mExtra));
            formalPool.add(new DesignItem(R.drawable.shirt1, "Slim-fit Cotton White", 950, 4.5f, "AI Suggested: Daily Professional Wear"));
            formalPool.add(new DesignItem(R.drawable.shirt3, "Oxford Blue Solid", 1050, 4.6f, "AI Suggested: Business Essential"));
            java.util.Collections.shuffle(formalPool);
            ((DesignItemAdapter)rvFormal.getAdapter()).submitList(formalPool.subList(0, Math.min(2, formalPool.size())));
            sectionFormal.setVisibility(View.VISIBLE);

            // Daily / Casual Pool
            List<DesignItem> dailyPool = new ArrayList<>();
            dailyPool.add(new DesignItem(R.drawable.kurta1, "Linen Comfort Kurta", 1100, 4.6f, "AI Suggested: Reliable Daily Wear"));
            dailyPool.add(new DesignItem(R.drawable.shirt7, "Checkered Casual Shirt", 750, 4.3f, "AI Suggested: Practical & Breathable"));
            dailyPool.add(new DesignItem(R.drawable.casual2, "Denim Utility Jacket", 1850, 4.5f, "AI Suggested: Weekend Casual"));
            java.util.Collections.shuffle(dailyPool);
            ((DesignItemAdapter)rvDaily.getAdapter()).submitList(dailyPool.subList(0, Math.min(2, dailyPool.size())));
            sectionDaily.setVisibility(View.VISIBLE);

            // Event / Party Pool
            List<DesignItem> eventPool = new ArrayList<>();
            eventPool.add(new DesignItem(R.drawable.blzer1, "Midnight Blue Party Blazer", 3200, 4.8f, "AI Suggested: Event Eye-Catcher"));
            eventPool.add(new DesignItem(R.drawable.designerblouse2, "Silk Halter Blouse", 1850, 4.7f, "AI Suggested: Unique Evening Style • " + mExtra));
            eventPool.add(new DesignItem(R.drawable.blzer2, "Velvet Evening Tux", 4200, 4.9f, "AI Suggested: Grand Event Fit"));
            java.util.Collections.shuffle(eventPool);
            ((DesignItemAdapter)rvEvent.getAdapter()).submitList(eventPool.subList(0, Math.min(2, eventPool.size())));
            sectionEvent.setVisibility(View.VISIBLE);

            tvLoading.setVisibility(View.GONE);
        }, 1200);
    }
}

