package com.example.smartdarzi.utils;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smartdarzi.R;

public final class CommonHeaderHelper {

    private CommonHeaderHelper() {
    }

    public static void bind(@NonNull View root, @NonNull CharSequence title, @Nullable CharSequence subtitle) {
        TextView tvTitle = root.findViewById(R.id.tvCommonHeaderTitle);
        TextView tvSubtitle = root.findViewById(R.id.tvCommonHeaderSubtitle);
        View headerRoot = root.findViewById(R.id.layoutCommonHeaderRoot);

        if (tvTitle != null) {
            tvTitle.setText(title);
        }
        if (tvSubtitle != null) {
            if (subtitle == null || subtitle.toString().trim().isEmpty()) {
                tvSubtitle.setVisibility(View.GONE);
            } else {
                tvSubtitle.setVisibility(View.VISIBLE);
                tvSubtitle.setText(subtitle);
            }
        }
        if (headerRoot != null) {
            applyCutoutSafeTopPadding(headerRoot);
        }
    }

    public static void updateSubtitle(@NonNull View root, @Nullable CharSequence subtitle) {
        TextView tvSubtitle = root.findViewById(R.id.tvCommonHeaderSubtitle);
        if (tvSubtitle == null) {
            return;
        }
        if (subtitle == null || subtitle.toString().trim().isEmpty()) {
            tvSubtitle.setVisibility(View.GONE);
        } else {
            tvSubtitle.setVisibility(View.VISIBLE);
            tvSubtitle.setText(subtitle);
        }
    }

    public static void applyCutoutSafeTopPadding(@NonNull View view) {
        final int initialLeft = view.getPaddingLeft();
        final int initialTop = view.getPaddingTop();
        final int initialRight = view.getPaddingRight();
        final int initialBottom = view.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets topInsets = insets.getInsets(
                    WindowInsetsCompat.Type.statusBars() | WindowInsetsCompat.Type.displayCutout());
            v.setPadding(initialLeft, initialTop + topInsets.top, initialRight, initialBottom);
            return insets;
        });
        ViewCompat.requestApplyInsets(view);
    }
}

