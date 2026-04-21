package com.example.smartdarzi.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.smartdarzi.models.Booking;
import com.example.smartdarzi.models.CartModel;
import com.example.smartdarzi.models.Measurement;
import com.example.smartdarzi.models.Service;
import com.example.smartdarzi.models.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Unified Database Helper for SmartDarzi.
 * Handles Users, Services, Bookings (Orders), Basket (Cart), and Measurements.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "darzi.db";
    private static final int DATABASE_VERSION = 13; // Bumped for addresses table

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_SERVICES = "services";
    private static final String TABLE_BOOKINGS = "bookings";
    private static final String TABLE_BASKET = "basket";
    private static final String TABLE_MEASUREMENTS = "measurements";
    private static final String TABLE_ADDRESSES = "addresses";

    // SQL Create Queries
    private static final String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "full_name TEXT, " +
            "email TEXT UNIQUE, " +
            "phone TEXT UNIQUE, " +
            "password TEXT, " +
            "created_at TEXT DEFAULT CURRENT_TIMESTAMP)";

    private static final String CREATE_SERVICES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_SERVICES + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT, " +
            "description TEXT, " +
            "price REAL, " +
            "category TEXT, " +
            "rating REAL DEFAULT 0, " +
            "turnaroundDays INTEGER DEFAULT 0, " +
            "stockQuantity INTEGER DEFAULT 0)";

    private static final String CREATE_BOOKINGS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_BOOKINGS + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER, " +
            "service_name TEXT, " +
            "appointment_date TEXT, " +
            "status TEXT, " +
            "total_price REAL, " +
            "tailor_name TEXT DEFAULT 'Pending')";

    private static final String CREATE_BASKET_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_BASKET + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER NOT NULL, " +
            "service_id INTEGER NOT NULL, " +
            "quantity INTEGER NOT NULL DEFAULT 1, " +
            "added_at TEXT DEFAULT CURRENT_TIMESTAMP, " +
            "UNIQUE(user_id, service_id))";

    private static final String CREATE_MEASUREMENTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_MEASUREMENTS + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER NOT NULL, " +
            "chest TEXT, " +
            "waist TEXT, " +
            "hip TEXT, " +
            "shoulder TEXT, " +
            "sleeveLength TEXT, " +
            "height TEXT, " +
            "notes TEXT, " +
            "created_at TEXT DEFAULT CURRENT_TIMESTAMP)";

    private static final String CREATE_ADDRESSES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ADDRESSES + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER NOT NULL, " +
            "label TEXT, " +
            "detailed_address TEXT, " +
            "phone TEXT, " +
            "is_default INTEGER DEFAULT 0)";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_SERVICES_TABLE);
        db.execSQL(CREATE_BOOKINGS_TABLE);
        db.execSQL(CREATE_BASKET_TABLE);
        db.execSQL(CREATE_MEASUREMENTS_TABLE);
        db.execSQL(CREATE_ADDRESSES_TABLE);
        seedAllCategoriesWithServices(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 12) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEASUREMENTS);
            db.execSQL(CREATE_MEASUREMENTS_TABLE);
        }
        if (oldVersion < 13) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADDRESSES);
            db.execSQL(CREATE_ADDRESSES_TABLE);
        }
    }

    // ── Address Methods ──────────────────────────────────────────────────────────

    public long addAddress(int userId, String label, String address, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("user_id", userId);
        cv.put("label", label);
        cv.put("detailed_address", address);
        cv.put("phone", phone);
        return db.insert(TABLE_ADDRESSES, null, cv);
    }

    public boolean updateAddress(int addressId, String label, String address, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("label", label);
        cv.put("detailed_address", address);
        cv.put("phone", phone);
        return db.update(TABLE_ADDRESSES, cv, "id=?", new String[]{String.valueOf(addressId)}) > 0;
    }

    public boolean deleteAddress(int addressId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_ADDRESSES, "id=?", new String[]{String.valueOf(addressId)}) > 0;
    }

    public List<String[]> getUserAddresses(int userId) {
        List<String[]> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_ADDRESSES, null, "user_id=?", new String[]{String.valueOf(userId)}, null, null, "id DESC")) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(new String[]{
                            cursor.getString(cursor.getColumnIndexOrThrow("label")),
                            cursor.getString(cursor.getColumnIndexOrThrow("detailed_address")),
                            cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                            String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("id")))
                    });
                } while (cursor.moveToNext());
            }
        }
        return list;
    }

    // --- USER METHODS ---

    public boolean addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("full_name", user.getFullName());
        values.put("email", user.getEmail());
        values.put("phone", user.getPhone());
        values.put("password", user.getPassword());
        
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public User authenticateUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_USERS, null, "email=? AND password=?", new String[]{email, password}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                return new User(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("full_name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                        cursor.getString(cursor.getColumnIndexOrThrow("password"))
                );
            }
        }
        return null;
    }

    public boolean isEmailRegistered(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_USERS, new String[]{"id"}, "email=?", new String[]{email}, null, null, null)) {
            return (cursor != null && cursor.moveToFirst());
        }
    }

    public boolean deleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BOOKINGS, "user_id=?", new String[]{String.valueOf(userId)});
        db.delete(TABLE_BASKET, "user_id=?", new String[]{String.valueOf(userId)});
        db.delete(TABLE_MEASUREMENTS, "user_id=?", new String[]{String.valueOf(userId)});
        return db.delete(TABLE_USERS, "id=?", new String[]{String.valueOf(userId)}) > 0;
    }

    // --- SERVICE METHODS ---

    public List<Service> getAllServices() {
        List<Service> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_SERVICES, null, null, null, null, null, "id ASC")) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(new Service(
                            cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("name")),
                            cursor.getString(cursor.getColumnIndexOrThrow("description")),
                            cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                            cursor.getString(cursor.getColumnIndexOrThrow("category")),
                            cursor.getDouble(cursor.getColumnIndexOrThrow("rating")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("turnaroundDays")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("stockQuantity"))
                    ));
                } while (cursor.moveToNext());
            }
        }
        return list;
    }

    public List<Service> getServicesByCategory(String category) {
        List<Service> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_SERVICES, null, "category=?", new String[]{category}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(new Service(
                            cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("name")),
                            cursor.getString(cursor.getColumnIndexOrThrow("description")),
                            cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                            cursor.getString(cursor.getColumnIndexOrThrow("category")),
                            cursor.getDouble(cursor.getColumnIndexOrThrow("rating")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("turnaroundDays")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("stockQuantity"))
                    ));
                } while (cursor.moveToNext());
            }
        }
        return list;
    }

    public int getServiceCountByCategory(String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_SERVICES + " WHERE category LIKE ?";
        try (Cursor c = db.rawQuery(query, new String[]{"%" + category + "%"})) {
            if (c != null && c.moveToFirst()) {
                return c.getInt(0);
            }
        }
        return 0;
    }

    public Service getServiceById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_SERVICES, null, "id=?", new String[]{String.valueOf(id)}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                return new Service(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                        cursor.getString(cursor.getColumnIndexOrThrow("category")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("rating")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("turnaroundDays")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("stockQuantity"))
                );
            }
        }
        return null;
    }

    // --- BASKET METHODS ---

    public boolean addToBasket(int userId, int serviceId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_BASKET, new String[]{"quantity"}, "user_id=? AND service_id=?", new String[]{String.valueOf(userId), String.valueOf(serviceId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int existing = cursor.getInt(0);
            cursor.close();
            ContentValues update = new ContentValues();
            update.put("quantity", existing + quantity);
            return db.update(TABLE_BASKET, update, "user_id=? AND service_id=?", new String[]{String.valueOf(userId), String.valueOf(serviceId)}) > 0;
        }
        if (cursor != null) cursor.close();

        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("service_id", serviceId);
        values.put("quantity", quantity);
        return db.insert(TABLE_BASKET, null, values) != -1;
    }

    public List<CartModel> getBasketItems(int userId) {
        List<CartModel> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_BASKET, null, "user_id=?", new String[]{String.valueOf(userId)}, null, null, "id DESC")) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int cartId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    int serviceId = cursor.getInt(cursor.getColumnIndexOrThrow("service_id"));
                    int qty = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                    Service s = getServiceById(serviceId);
                    if (s != null) {
                        items.add(new CartModel(cartId, serviceId, s.getName(), s.getPrice(), qty));
                    }
                } while (cursor.moveToNext());
            }
        }
        return items;
    }

    public List<ContentValues> getCartItemsForUser(int userId) {
        List<ContentValues> rows = new ArrayList<>();
        List<CartModel> items = getBasketItems(userId);
        for (CartModel m : items) {
            ContentValues cv = new ContentValues();
            cv.put("id", m.getCartId());
            cv.put("product_id", m.getProductId());
            cv.put("product_name", m.getProductName());
            cv.put("product_price", m.getUnitPrice());
            cv.put("quantity", m.getQuantity());
            rows.add(cv);
        }
        return rows;
    }

    public boolean updateBasketQuantity(int userId, int serviceId, int qty) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("quantity", Math.max(1, qty));
        return db.update(TABLE_BASKET, v, "user_id=? AND service_id=?", new String[]{String.valueOf(userId), String.valueOf(serviceId)}) > 0;
    }

    public boolean removeFromBasket(int userId, int serviceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_BASKET, "user_id=? AND service_id=?", new String[]{String.valueOf(userId), String.valueOf(serviceId)}) > 0;
    }

    public boolean updateCartItemQuantity(int cartId, int qty) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("quantity", Math.max(1, qty));
        return db.update(TABLE_BASKET, v, "id=?", new String[]{String.valueOf(cartId)}) > 0;
    }

    public boolean deleteCartItem(int cartId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_BASKET, "id=?", new String[]{String.valueOf(cartId)}) > 0;
    }

    public int getBasketCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor c = db.rawQuery("SELECT SUM(quantity) FROM " + TABLE_BASKET + " WHERE user_id=?", new String[]{String.valueOf(userId)})) {
            if (c != null && c.moveToFirst()) {
                return c.getInt(0);
            }
        }
        return 0;
    }

    public void clearBasket(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BASKET, "user_id=?", new String[]{String.valueOf(userId)});
    }

    // --- BOOKING / ORDER METHODS ---

    public boolean createBooking(int userId, String serviceName, String date, double price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("user_id", userId);
        v.put("service_name", serviceName);
        v.put("appointment_date", date);
        v.put("status", "Upcoming");
        v.put("total_price", price);
        return db.insert(TABLE_BOOKINGS, null, v) != -1;
    }

    public long placeOrderFromCart(int userId, String address) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(new java.util.Date());
        List<CartModel> items = getBasketItems(userId);
        if (items.isEmpty()) return -1;

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            long firstId = -1;
            for (CartModel item : items) {
                ContentValues v = new ContentValues();
                v.put("user_id", userId);
                v.put("service_name", item.getProductName());
                v.put("appointment_date", today);
                v.put("status", "Upcoming");
                v.put("total_price", item.getSubtotal());
                long id = db.insert(TABLE_BOOKINGS, null, v);
                if (firstId == -1) firstId = id;
            }
            db.delete(TABLE_BASKET, "user_id=?", new String[]{String.valueOf(userId)});
            db.setTransactionSuccessful();
            return firstId;
        } finally {
            db.endTransaction();
        }
    }

    private long dbInsertBooking(int userId, String name, String date, double price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("user_id", userId);
        v.put("service_name", name);
        v.put("appointment_date", date);
        v.put("status", "Upcoming");
        v.put("total_price", price);
        return db.insert(TABLE_BOOKINGS, null, v);
    }

    public List<Booking> getBookingsByStatus(int userId, String status) {
        List<Booking> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_BOOKINGS, null, "user_id=? AND status=?", new String[]{String.valueOf(userId), status}, null, null, "id DESC")) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Booking b = new Booking(
                            cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("service_name")),
                            cursor.getString(cursor.getColumnIndexOrThrow("appointment_date")),
                            cursor.getString(cursor.getColumnIndexOrThrow("status")),
                            cursor.getDouble(cursor.getColumnIndexOrThrow("total_price"))
                    );
                    int tailorIdx = cursor.getColumnIndex("tailor_name");
                    if (tailorIdx != -1) b.setTailorName(cursor.getString(tailorIdx));
                    list.add(b);
                } while (cursor.moveToNext());
            }
        }
        return list;
    }

    public boolean updateBookingStatus(int id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("status", status);
        return db.update(TABLE_BOOKINGS, v, "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public int getUserOrderCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_BOOKINGS + " WHERE user_id=?", new String[]{String.valueOf(userId)})) {
            if (c != null && c.moveToFirst()) {
                return c.getInt(0);
            }
        }
        return 0;
    }

    // --- MEASUREMENT METHODS ---

    public Measurement getLatestMeasurementForUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor c = db.query(TABLE_MEASUREMENTS, null, "user_id=?", new String[]{String.valueOf(userId)}, null, null, "id DESC LIMIT 1")) {
            if (c != null && c.moveToFirst()) {
                return new Measurement(
                        c.getInt(c.getColumnIndexOrThrow("id")),
                        c.getInt(c.getColumnIndexOrThrow("user_id")),
                        c.getString(c.getColumnIndexOrThrow("chest")),
                        c.getString(c.getColumnIndexOrThrow("waist")),
                        c.getString(c.getColumnIndexOrThrow("hip")),
                        c.getString(c.getColumnIndexOrThrow("shoulder")),
                        c.getString(c.getColumnIndexOrThrow("sleeveLength")),
                        c.getString(c.getColumnIndexOrThrow("height")),
                        c.getString(c.getColumnIndexOrThrow("notes"))
                );
            }
        }
        return null;
    }

    public int getUserMeasurementCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_MEASUREMENTS + " WHERE user_id=?", new String[]{String.valueOf(userId)})) {
            if (c != null && c.moveToFirst()) {
                return c.getInt(0);
            }
        }
        return 0;
    }

    public long insertMeasurement(Measurement m) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("user_id", m.getUserId());
        v.put("chest", m.getChest());
        v.put("waist", m.getWaist());
        v.put("hip", m.getHip());
        v.put("shoulder", m.getShoulder());
        v.put("sleeveLength", m.getSleeveLength());
        v.put("height", m.getHeight());
        v.put("notes", m.getNotes());
        return db.insert(TABLE_MEASUREMENTS, null, v);
    }

    public boolean updateMeasurement(Measurement m) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("chest", m.getChest());
        v.put("waist", m.getWaist());
        v.put("hip", m.getHip());
        v.put("shoulder", m.getShoulder());
        v.put("sleeveLength", m.getSleeveLength());
        v.put("height", m.getHeight());
        v.put("notes", m.getNotes());
        return db.update(TABLE_MEASUREMENTS, v, "id=?", new String[]{String.valueOf(m.getId())}) > 0;
    }

    // --- SEEDING ---

    public void seedMockDataIfNeeded() {
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor c = db.query(TABLE_SERVICES, new String[]{"id"}, null, null, null, null, "id LIMIT 1")) {
            if (c == null || c.getCount() == 0) {
                seedAllCategoriesWithServices(this.getWritableDatabase());
            }
        }
    }

    private void seedAllCategoriesWithServices(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            insertService(db, "Custom Formal Shirt", "Premium formal shirt with collar customization", 499.0, "Shirts", 4.5, 3, 20);
            insertService(db, "Casual Shirt Design", "Cotton casual shirt with your choice of fit", 399.0, "Shirts", 4.4, 3, 25);
            insertService(db, "Formal Trousers", "Perfectly fitted trousers for formal wear", 699.0, "Pants", 4.6, 3, 18);
            insertService(db, "Jeans Alteration", "Hemming, tapering, and fit adjustments", 299.0, "Pants", 4.3, 1, 40);
            insertService(db, "Three Piece Suit", "Premium bespoke hand-stitched suit", 2099.0, "Suits", 4.8, 7, 8);
            insertService(db, "Wedding Tuxedo", "Special wedding tuxedo design", 2499.0, "Suits", 4.9, 10, 6);
            insertService(db, "Designer Kurta", "Contemporary kurta with churidar set", 1099.0, "Kurtas", 4.6, 4, 14);
            insertService(db, "Chikankari Kurta", "Hand-embroidered traditional kurta", 1299.0, "Kurtas", 4.7, 5, 12);
            insertService(db, "Silk Saree Blouse", "Premium silk blouse with custom back design", 899.0, "Blouses", 4.7, 4, 12);
            insertService(db, "Padded Blouse", "Designer padded blouse for weddings", 1099.0, "Blouses", 4.8, 5, 10);
            insertService(db, "Bridal Lehenga", "Custom bridal lehenga with heavy embroidery", 4999.0, "Lehengas", 5.0, 14, 3);
            insertService(db, "Party Wear Lehenga", "Lightweight designer lehenga for events", 2499.0, "Lehengas", 4.6, 7, 8);
            insertService(db, "Groom Sherwani", "Royal sherwani for wedding ceremonies", 3499.0, "Wedding Wear", 4.9, 12, 5);
            insertService(db, "Bridal Gown", "Custom white/red bridal gown design", 4299.0, "Wedding Wear", 4.8, 14, 4);
            insertService(db, "Banarasi Silk Saree", "Premium silk saree with custom stitching", 2999.0, "Saree", 4.9, 7, 10);
            insertService(db, "Designer Sadi", "Modern sadi design for parties", 1599.0, "Saree", 4.7, 5, 15);
            insertService(db, "Quick Hemming", "Same-day pant hemming and length fix", 199.0, "Alterations", 4.4, 1, 50);
            insertService(db, "Waist Adjustment", "Tighten or loosen garment waist", 299.0, "Alterations", 4.5, 2, 30);
            insertService(db, "Bespoke Design", "Create your own outfit with our experts", 1499.0, "Custom Stitching", 4.7, 7, 10);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void insertService(SQLiteDatabase db, String name, String desc, double price, String cat, double rating, int days, int stock) {
        ContentValues v = new ContentValues();
        v.put("name", name);
        v.put("description", desc);
        v.put("price", price);
        v.put("category", cat);
        v.put("rating", rating);
        v.put("turnaroundDays", days);
        v.put("stockQuantity", stock);
        db.insert(TABLE_SERVICES, null, v);
    }
}
