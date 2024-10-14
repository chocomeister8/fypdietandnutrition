package com.fyp.dietandnutritionapplication;

import java.util.HashMap;
import java.util.Map;

public class NutriAccount {
    private Map<String, Nutritionist> nutritionistData = new HashMap<>();

    // Retrieve Nutritionist Profile
    public Nutritionist getProfile(String email) {
        return nutritionistData.get(email);
    }

    public boolean saveProfile(Nutritionist nutritionist) {
        if (nutritionist != null && nutritionistData.get(nutritionist.getEmail()) == null) {
            nutritionistData.put(nutritionist.getEmail(), nutritionist);
            return true;
        }
        return false;
    }

    // Update Nutritionist Profile
    public boolean updateProfile(Nutritionist nutritionist) {
        if (nutritionist != null && nutritionistData.containsKey(nutritionist.getEmail())) {
            nutritionistData.put(nutritionist.getEmail(), nutritionist);
            return true;
        }
        return false;
    }

}

