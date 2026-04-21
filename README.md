# Darzi - Tailoring Service App

Android Studio project scaffold for a tailoring service app using Java, XML, Material Components, and local SQLite.

## Tech Stack

- Language: Java
- UI: XML + Material Components
- Database: SQLite (`SQLiteOpenHelper`)
- Minimum SDK: 24
- Target SDK: 36

## Implemented App Flow

1. `SplashActivity`
2. `LoginActivity`
3. `RegisterActivity`
4. `MainActivity` with Bottom Navigation (`Home`, `Services`, `Bookings`, `Profile`)

## Package Structure

`app/src/main/java/com/example/smartdarzi/`

- `activities/`
  - `SplashActivity.java`
  - `LoginActivity.java`
  - `RegisterActivity.java`
  - `MainActivity.java`
- `fragments/`
  - `HomeFragment.java`
  - `ServicesFragment.java`
  - `BookingsFragment.java`
  - `ProfileFragment.java`
- `adapters/`
  - `ServiceAdapter.java`
  - `BookingAdapter.java`
- `models/`
  - `User.java`
  - `TailorService.java`
  - `Booking.java`
  - `Measurement.java`
- `database/`
  - `DatabaseContract.java`
  - `DatabaseHelper.java`
- `utils/`
  - `SessionManager.java`
  - `ValidationUtils.java`

## Key Resources

- Layouts: `app/src/main/res/layout/`
  - `activity_splash.xml`, `activity_login.xml`, `activity_register.xml`, `activity_main.xml`
  - `fragment_home.xml`, `fragment_services.xml`, `fragment_bookings.xml`, `fragment_profile.xml`
  - `item_service.xml`, `item_booking.xml`
- Menu: `app/src/main/res/menu/bottom_nav_menu.xml`
- Colors: `app/src/main/res/values/colors.xml`
- Theme: `app/src/main/res/values/themes.xml`, `app/src/main/res/values-night/themes.xml`
- Strings: `app/src/main/res/values/strings.xml`

## Dependencies

Configured in `gradle/libs.versions.toml` and consumed in `app/build.gradle`:

- `androidx.appcompat:appcompat`
- `com.google.android.material:material`
- `androidx.activity:activity`
- `androidx.constraintlayout:constraintlayout`
- `androidx.recyclerview:recyclerview`
- `androidx.cardview:cardview`

## Run

Open in Android Studio and run on an emulator/device.

Gradle command (from project root):

```powershell
.\gradlew.bat :app:assembleDebug
```

If you run from terminal outside Android Studio, ensure `JAVA_HOME` is set.

