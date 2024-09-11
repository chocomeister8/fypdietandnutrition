package com.example.dietandnutritionapplication;

public class ViewNutriProfileController {
    private NutriAccount nutriAccount;

    public ViewNutriProfileController(NutriAccount nutriAccount) {
        this.nutriAccount = nutriAccount;
    }

    // Fetch Nutritionist profile
    public Nutritionist viewProfile(String email) {
        return nutriAccount.getProfile(email);
    }
}

