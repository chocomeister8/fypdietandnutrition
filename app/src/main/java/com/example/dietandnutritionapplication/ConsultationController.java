package com.example.dietandnutritionapplication;

import java.util.ArrayList;

public class ConsultationController {
    ArrayList<Profile> profiles = new ArrayList<>();
    public ConsultationController(){

    }
    public void retrieveNutri(final UserAccountEntity.DataCallback callback) {
        UserAccountEntity userAccountEntity = new UserAccountEntity();
        userAccountEntity.retrieveNutritionists(new UserAccountEntity.DataCallback() {
            @Override
            public void onSuccess(ArrayList<Profile> accounts) {
                profiles.clear();
                profiles.addAll(accounts);
                callback.onSuccess(profiles);
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                callback.onFailure(e);
            }
        });
    }
}