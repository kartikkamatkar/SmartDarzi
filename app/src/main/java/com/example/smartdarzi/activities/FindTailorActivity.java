package com.example.smartdarzi.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartdarzi.R;
import com.example.smartdarzi.adapters.TailorAdapter;
import com.example.smartdarzi.models.TailorShop;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class FindTailorActivity extends AppCompatActivity {

    private RecyclerView rvTailorShops;
    private TailorAdapter adapter;
    private LinearLayout layoutNoTailors;
    private EditText etTailorSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_tailor);

        MaterialToolbar toolbar = findViewById(R.id.toolbarFindTailor);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        rvTailorShops = findViewById(R.id.rvTailorShops);
        layoutNoTailors = findViewById(R.id.layoutNoTailors);
        etTailorSearch = findViewById(R.id.etTailorSearch);

        setupTailorList();
        setupSearchLogic();
    }

    private void setupTailorList() {
        List<TailorShop> tailorShops = new ArrayList<>();
        
        // Detailed realistic tailor data for Yavatmal, Maharashtra
        tailorShops.add(new TailorShop("Shree Ganesh Tailors", "9823456789", "Near Gandhi Chowk, Main Road, Yavatmal", "Gandhi Chowk", "geo:0,0?q=Gandhi+Chowk+Yavatmal"));
        tailorShops.add(new TailorShop("Modern Men's Wear", "9422883344", "Chhatrapati Shivaji Maharaj Chowk, Yavatmal", "Chhatrapati Shivaji Maharaj Chowk", "geo:0,0?q=Chhatrapati+Shivaji+Maharaj+Chowk+Yavatmal"));
        tailorShops.add(new TailorShop("Perfect Fit Tailoring", "9145678901", "Ambedkar Chowk, Near Bus Stand, Yavatmal", "Ambedkar Chowk", "geo:0,0?q=Ambedkar+Chowk+Yavatmal"));
        tailorShops.add(new TailorShop("Royal Stitching Studio", "9970123456", "Tagore Chowk, Civil Lines, Yavatmal", "Tagore Chowk", "geo:0,0?q=Tagore+Chowk+Yavatmal"));
        tailorShops.add(new TailorShop("Shanti Ladies Tailors", "9850123477", "Near Datta Chowk, Arni Road, Yavatmal", "Datta Chowk", "geo:0,0?q=Datta+Chowk+Yavatmal"));
        tailorShops.add(new TailorShop("Smart Cut Tailors", "9123456780", "Wani Road Chowk, Yavatmal", "Wani Road Chowk", "geo:0,0?q=Wani+Road+Chowk+Yavatmal"));
        tailorShops.add(new TailorShop("Arni Selection Tailoring", "9011223344", "Arni Road Chowk, Opp Bank, Yavatmal", "Arni Road Chowk", "geo:0,0?q=Arni+Road+Chowk+Yavatmal"));
        tailorShops.add(new TailorShop("Professional Fits", "9234567812", "SBI Road Chowk, Beside ATM, Yavatmal", "SBI Road Chowk", "geo:0,0?q=SBI+Road+Chowk+Yavatmal"));
        tailorShops.add(new TailorShop("Wani Fashion Hub", "9345678923", "Wani Bus Stand Area, Yavatmal", "Wani Bus Stand Area", "geo:0,0?q=Wani+Bus+Stand+Yavatmal"));
        tailorShops.add(new TailorShop("Gajanan Tailoring House", "9456789034", "Rajiv Gandhi Chowk, Main Market, Yavatmal", "Rajiv Gandhi Chowk", "geo:0,0?q=Rajiv+Gandhi+Chowk+Yavatmal"));

        adapter = new TailorAdapter(this, tailorShops, isEmpty -> {
            layoutNoTailors.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            rvTailorShops.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        });

        rvTailorShops.setLayoutManager(new LinearLayoutManager(this));
        rvTailorShops.setAdapter(adapter);
    }

    private void setupSearchLogic() {
        etTailorSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}
