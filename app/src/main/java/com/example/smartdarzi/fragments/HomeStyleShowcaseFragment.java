package com.example.smartdarzi.fragments;

import android.os.Bundle;
import android.view.View;
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
import java.util.Locale;

public class HomeStyleShowcaseFragment extends Fragment {

    public interface Callbacks {
        void onExploreAllServicesRequested();
    }

    private RecyclerView rvWedding, rvFormal, rvDaily, rvEvent;
    private android.widget.LinearLayout sectionWedding, sectionFormal, sectionDaily, sectionEvent;
    private TextView tvEmptyState;

    public HomeStyleShowcaseFragment() {
        super(R.layout.fragment_home_style_showcase);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvEmptyState = view.findViewById(R.id.tvHomeStylesEmpty);
        
        sectionWedding = view.findViewById(R.id.sectionWedding);
        sectionFormal = view.findViewById(R.id.sectionFormal);
        sectionDaily = view.findViewById(R.id.sectionDaily);
        sectionEvent = view.findViewById(R.id.sectionEvent);

        rvWedding = view.findViewById(R.id.rvWeddingStyles);
        rvFormal = view.findViewById(R.id.rvFormalStyles);
        rvDaily = view.findViewById(R.id.rvDailyStyles);
        rvEvent = view.findViewById(R.id.rvEventStyles);

        setupRecycler(rvWedding);
        setupRecycler(rvFormal);
        setupRecycler(rvDaily);
        setupRecycler(rvEvent);

        loadInitialData();
    }

    private void setupRecycler(RecyclerView rv) {
        rv.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rv.setAdapter(new DesignItemAdapter());
    }

    private void loadInitialData() {
        tvEmptyState.setVisibility(View.GONE);
        
        // Populate wedding
        List<DesignItem> weddings = new ArrayList<>();
        weddings.add(new DesignItem(R.drawable.lehenga1, "Royal Sangeet Lehenga", 6800, 4.9f, "AI Suggested: Trending for Weddings"));
        weddings.add(new DesignItem(R.drawable.sherwani1, "White Classic Sherwani", 5200, 4.8f, "AI Suggested: Traditional Choice"));
        ((DesignItemAdapter)rvWedding.getAdapter()).submitList(weddings);
        sectionWedding.setVisibility(View.VISIBLE);

        // Populate formal
        List<DesignItem> formals = new ArrayList<>();
        formals.add(new DesignItem(R.drawable.suit1, "Navy Executive Suit", 4500, 4.7f, "AI Suggested: Formal Perfection"));
        ((DesignItemAdapter)rvFormal.getAdapter()).submitList(formals);
        sectionFormal.setVisibility(View.VISIBLE);

        // Populate daily
        List<DesignItem> daily = new ArrayList<>();
        daily.add(new DesignItem(R.drawable.kurta1, "Daily Comfort Kurti", 1200, 4.6f, "AI Suggested: Daily Elegance"));
        ((DesignItemAdapter)rvDaily.getAdapter()).submitList(daily);
        sectionDaily.setVisibility(View.VISIBLE);

        // Populate Events
        List<DesignItem> events = new ArrayList<>();
        events.add(new DesignItem(R.drawable.blzer1, "Evening Party Blazer", 3100, 4.8f, "AI Suggested: Best for Night Events"));
        ((DesignItemAdapter)rvEvent.getAdapter()).submitList(events);
        sectionEvent.setVisibility(View.VISIBLE);
    }

    private List<DesignItem> findMatches(@NonNull List<DesignItem> source, @NonNull String query) {
        List<DesignItem> matches = new ArrayList<>();
        String normalized = query.trim().toLowerCase(java.util.Locale.getDefault());
        if (normalized.isEmpty()) {
            matches.addAll(source);
            return matches;
        }

        for (DesignItem item : source) {
            if (item.getName().toLowerCase(java.util.Locale.getDefault()).contains(normalized)) {
                matches.add(item);
            }
        }
        return matches;
    }
}
