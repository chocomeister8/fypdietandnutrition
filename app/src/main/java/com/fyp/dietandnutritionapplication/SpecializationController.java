package com.fyp.dietandnutritionapplication;

import android.app.ProgressDialog;
import android.content.Context;

import java.util.ArrayList;

public class SpecializationController {
    private ArrayList<Specialization> allspecializations = new ArrayList<>();

    public void getAllSpecializations(SpecializationEntity.DataCallback callback) {
        SpecializationEntity specializationEntity = new SpecializationEntity();
        specializationEntity.fetchSpecialization(new SpecializationEntity.DataCallback() {
            @Override
            public void onSuccess(ArrayList<Specialization> specializations) {
                allspecializations.clear();
                allspecializations.addAll(specializations);
                callback.onSuccess(allspecializations); // Return the fetched FAQs to the callback
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                callback.onFailure(e); // Pass the failure to the callback
            }
        });
    }

    public void insertSpecialization(String name, ProgressDialog pd, Context context){
        SpecializationEntity specializationEntityEntity = new SpecializationEntity();
        specializationEntityEntity.insertSpecialization(name,pd,context);
    }

}