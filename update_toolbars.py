import os
import re

LAYOUTS_DIR = r"d:\SmartDarzi\SmartDarzi\app\src\main\res\layout"

files_to_update = [
    "fragment_settings.xml",
    "fragment_privacy_policy.xml",
    "fragment_measurements.xml",
    "fragment_edit_profile.xml",
    "fragment_help_support.xml",
    "fragment_addresses.xml",
    "fragment_about_us.xml",
    "activity_category_segment.xml",
    "activity_product_detail.xml"
]

toolbar_pattern = re.compile(
    r'(<com\.google\.android\.material\.appbar\.MaterialToolbar[^>]+?/>)', 
    re.DOTALL
)

def wrap_toolbar_in_card(match):
    toolbar_str = match.group(1)
    
    # Inject titleTextColor and navigationIconTint if missing
    if 'app:titleTextColor' not in toolbar_str:
        toolbar_str = toolbar_str.replace('/>', ' app:titleTextColor="@color/white"\n        app:navigationIconTint="@color/white" />')
    
    return f'''<com.google.android.material.card.MaterialCardView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_margin="16dp"
        app:cardBackgroundColor="#0F172A"
        app:cardCornerRadius="16dp"
        app:cardElevation="4dp">

    {toolbar_str}

    </com.google.android.material.card.MaterialCardView>'''

for filename in files_to_update:
    filepath = os.path.join(LAYOUTS_DIR, filename)
    if os.path.exists(filepath):
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()

        new_content = toolbar_pattern.sub(wrap_toolbar_in_card, content)

        if new_content != content:
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(new_content)
            print(f"Updated {filename}")
        else:
            print(f"No changes for {filename}")
    else:
        print(f"File not found: {filename}")
