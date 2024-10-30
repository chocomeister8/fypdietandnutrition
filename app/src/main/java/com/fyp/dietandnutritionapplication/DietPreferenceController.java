package com.fyp.dietandnutritionapplication;

import android.app.ProgressDialog;
import android.content.Context;

import java.util.ArrayList;

public class DietPreferenceController {
    private ArrayList<DietPreference> allDietPreference = new ArrayList<>();
    private DietPreferenceEntity dietPreferenceEntity;

    public DietPreferenceController() {
        // Ensure the entity instance is created
        this.dietPreferenceEntity = new DietPreferenceEntity();
    }

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

    public void deleteDietPreference(String dietPreferenceId, DietPreferenceEntity.DeleteCallback callback) {
        // Ensure that specializationEntity is not null before calling its methods
        if (dietPreferenceEntity != null) {
            dietPreferenceEntity.deleteDietPreferenceById(dietPreferenceId, new DietPreferenceEntity.DeleteCallback() {
                @Override
                public void onSuccess() {
                    callback.onSuccess();
                }

                @Override
                public void onFailure(Exception e) {
                    callback.onFailure(e);
                }
            });
        } else {
            // Handle the case where specializationEntity is null
            callback.onFailure(new NullPointerException("SpecializationEntity is not initialized"));
        }
    }
}
