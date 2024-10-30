package com.fyp.dietandnutritionapplication;

import android.app.ProgressDialog;
import android.content.Context;

import java.util.ArrayList;

public class SpecializationController {
    private ArrayList<Specialization> allspecializations = new ArrayList<>();
    private SpecializationEntity specializationEntity;

    public SpecializationController() {
        // Ensure the entity instance is created
        this.specializationEntity = new SpecializationEntity();
    }

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

    public void deleteSpecialization(String specializationId, SpecializationEntity.DeleteCallback callback) {
        // Ensure that specializationEntity is not null before calling its methods
        if (specializationEntity != null) {
            specializationEntity.deleteSpecializationById(specializationId, new SpecializationEntity.DeleteCallback() {
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