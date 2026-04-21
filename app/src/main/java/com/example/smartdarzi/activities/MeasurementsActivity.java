package com.example.smartdarzi.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartdarzi.R;
import com.example.smartdarzi.fragments.MeasurementsFragment;

/**
 * Thin wrapper Activity that hosts MeasurementsFragment.
 * Launched from ProductDetailActivity's "Edit Measurements" button
 * so the user can edit and save measurements without leaving the product detail flow.
 */
public class MeasurementsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurements);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerMeasurements, new MeasurementsFragment())
                    .commit();
        }
    }
}
