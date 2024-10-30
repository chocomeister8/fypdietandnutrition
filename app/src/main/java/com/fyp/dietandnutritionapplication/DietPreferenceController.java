package com.fyp.dietandnutritionapplication;

import android.app.ProgressDialog;
import android.content.Context;

import java.util.ArrayList;

public class DietPreferenceController {
    private ArrayList<DietPreference> allDietPreference = new ArrayList<>();

    public void getAllDietPreference(DietPreferenceEntity.DataCallback callback) {
        DietPreferenceEntity dietPreferenceEntity = new DietPreferenceEntity();
        dietPreferenceEntity.fetchDietPreference(new DietPreferenceEntity.DataCallback(){
            @Override
            public void onSuccess(ArrayList<DietPreference> dietPreferences) {
                allDietPreference.clear();
                allDietPreference.addAll(dietPreferences);
                callback.onSuccess(dietPreferences); // Return the fetched FAQs to the callback
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                callback.onFailure(e); // Pass the failure to the callback
            }
        });
    }

    public void insertDietPreference(String dietpreference, ProgressDialog pd, Context context){
        DietPreferenceEntity dietPreferenceEntity = new DietPreferenceEntity();
        dietPreferenceEntity.insertDietPreference(dietpreference,pd,context);
    }
}
