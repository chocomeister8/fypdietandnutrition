package com.fyp.dietandnutritionapplication;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class UpdateConsultationStatusWorker extends Worker {

    public UpdateConsultationStatusWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Logic to change status to "inactive"
        updateInactiveConsultations();
        return Result.success();
    }

    private void updateInactiveConsultations() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        long currentTimeMillis = System.currentTimeMillis();

        // Query consultations where the booking time has passed
        db.collection("Consultation_slots")
                .whereLessThan("bookingTime", currentTimeMillis)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Update the status to "inactive"
                        db.collection("Consultation_slots").document(document.getId())
                                .update("status", "inactive")
                                .addOnSuccessListener(aVoid -> Log.d("ConsultationManager", "Updated status to inactive for consultation ID: " + document.getId()))
                                .addOnFailureListener(e -> Log.e("ConsultationManager", "Error updating status", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("ConsultationManager", "Error retrieving consultations", e));
    }
}
