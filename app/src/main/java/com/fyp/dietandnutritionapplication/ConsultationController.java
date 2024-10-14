package com.fyp.dietandnutritionapplication;

import android.util.Log;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ConsultationController {
    ArrayList<Profile> profiles = new ArrayList<>();
    ArrayList<Consultation> consultation = new ArrayList<>();
    private String consultationId;

    public ConsultationController() {
        // Constructor
    }

    // Method to retrieve Nutritionists
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

    // Set Consultation ID
    public void setConsultationId(String consultationId) {
        this.consultationId = consultationId;
    }

    // Method to retrieve consultations from Firebase Firestore
    public void retrieveConsultations(final ConsultationEntity.DataCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Adding snapshot listener to the 'Consultation_slots' collection
        db.collection("Consultation_slots").addSnapshotListener((querySnapshot, e) -> {
            // Check if there's an error fetching data
            if (e != null) {
                Log.e("ConsultationController", "Error fetching data: ", e); // Log the error
                callback.onFailure(e); // Pass the error to the callback
                return;
            }

            // Check if querySnapshot is null
            if (querySnapshot == null) {
                Log.e("ConsultationController", "QuerySnapshot is null. No data retrieved.");
                callback.onFailure(new Exception("No data retrieved.")); // Create an exception for null snapshot
                return;
            }

            // Process the retrieved data
            ArrayList<Consultation> consultations = new ArrayList<>();
            if (!querySnapshot.isEmpty()) {
                // Loop through each document in the snapshot and convert it to Consultation object
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    Consultation consultation = document.toObject(Consultation.class);
                    consultations.add(consultation);
                }
                Log.d("ConsultationController", "Successfully retrieved " + consultations.size() + " consultations.");
                callback.onSuccess(consultations); // Success callback with retrieved consultations
            } else {
                Log.d("ConsultationController", "No consultations available in the collection.");
                callback.onSuccess(consultations); // Success callback with empty list
            }
        });
    }
}
