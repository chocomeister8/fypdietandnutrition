package com.fyp.dietandnutritionapplication;

import android.app.ProgressDialog;
import android.content.Context;

import java.util.ArrayList;

public class AllergyOptionsController {
    private ArrayList<AllergyOptions> allergyOptionsArrayList = new ArrayList<>();

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

}