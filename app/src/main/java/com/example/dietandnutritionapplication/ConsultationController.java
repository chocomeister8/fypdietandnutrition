package com.example.dietandnutritionapplication;

import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ConsultationController {
    private FirebaseFirestore db;
    private List<Consultation> consultationList;

    public ConsultationController(){
        db = FirebaseFirestore.getInstance();
        consultationList = new ArrayList<>();

    }
    // Method to retrieve consultations from Firestore
    public void retrieveNutri(final UserAccountEntity.DataCallback callback) {
        db.collection("consultations")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        consultationList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Assuming consultation fields: date, clientName, and status
                            String date = document.getString("date");
                            String clientName = document.getString("clientName");
                            String status = document.getString("status");

                            Consultation consultation = new Consultation(date, clientName, status);
                            consultationList.add(consultation);
                        }
                        //callback.onSuccess(consultationList);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    public interface ConsultationCallback {
        void onSuccess(List<Consultation> consultations);
        void onFailure(Exception e);
    }
}
