package com.example.smartdarzi.models;

public class CartModel {

    private final int cartId;
    private final int productId;
    private final String productName;
    private final double unitPrice;
    private int quantity;

    public CartModel(int cartId, int productId, String productName,
                     double unitPrice, int quantity) {
        this.cartId      = cartId;
        this.productId   = productId;
        this.productName = productName;
        this.unitPrice   = unitPrice;
        this.quantity    = quantity;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int    getCartId()      { return cartId; }
    public int    getProductId()   { return productId; }
    public String getProductName() { return productName; }
    public double getUnitPrice()   { return unitPrice; }
    public int    getQuantity()    { return quantity; }

    // ── Mutable ──────────────────────────────────────────────────────────────

    public void   setQuantity(int quantity) { this.quantity = quantity; }

    // ── Derived ──────────────────────────────────────────────────────────────

    /** Unit price × quantity */
    public double getSubtotal() { return unitPrice * quantity; }
}

