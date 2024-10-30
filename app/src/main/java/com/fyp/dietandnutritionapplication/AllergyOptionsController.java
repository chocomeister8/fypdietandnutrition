package com.fyp.dietandnutritionapplication;

import android.app.ProgressDialog;
import android.content.Context;

import java.util.ArrayList;

public class AllergyOptionsController {
    private ArrayList<AllergyOptions> allergyOptionsArrayList = new ArrayList<>();
    private AllergyOptionsEntity allergyOptionEntity;

    public AllergyOptionsController() {
        // Ensure the entity instance is created
        this.allergyOptionEntity = new AllergyOptionsEntity();
    }

    public void getAllAllergyOptions(AllergyOptionsEntity.DataCallback callback) {
        AllergyOptionsEntity allergyOptionsEntity = new AllergyOptionsEntity();
        allergyOptionsEntity.fetchAllergyOptions(new AllergyOptionsEntity.DataCallback() {
            @Override
            public void onSuccess(ArrayList<AllergyOptions> allergyOptions) {
                allergyOptionsArrayList.clear();
                allergyOptionsArrayList.addAll(allergyOptions);
                callback.onSuccess(allergyOptions); // Return the fetched FAQs to the callback
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                callback.onFailure(e); // Pass the failure to the callback
            }
        });
    }

    public void insertAllergyOptions(String allergyOption, ProgressDialog pd, Context context){
        AllergyOptionsEntity allergyOptionsEntity = new AllergyOptionsEntity();
        allergyOptionsEntity.insertAllergyOption(allergyOption,pd,context);
    }

    public void deleteAllergyOption(String allergyOptionId, AllergyOptionsEntity.DeleteCallback callback) {
        // Ensure that specializationEntity is not null before calling its methods
        if (allergyOptionId != null) {
            allergyOptionEntity.deleteAllergyOptionsById(allergyOptionId, new AllergyOptionsEntity.DeleteCallback() {
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
            callback.onFailure(new NullPointerException("AllergyOptionEntity is not initialized"));
        }
    }
}