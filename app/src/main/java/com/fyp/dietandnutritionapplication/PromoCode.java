package com.fyp.dietandnutritionapplication;

public class PromoCode {
    private String promocode;
    private double discountValue;

    // Default constructor required for calls to DataSnapshot.getValue(PromoCode.class)
    public PromoCode() {
    }

    public PromoCode(String promocode, double discountValue) {
        this.promocode = promocode;
        this.discountValue = discountValue;
    }

    public String getPromocode() {
        return promocode;
    }

    public double getDiscountValue() {
        return discountValue;
    }
}
