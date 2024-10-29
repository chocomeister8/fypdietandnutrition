package com.fyp.dietandnutritionapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpecializationEntity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public SpecializationEntity() {
        db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
    }

    public interface DataCallback {
        void onSuccess(ArrayList<Specialization> specializations);
        void onFailure(Exception e);
    }

    public void retrieveSpecializations(final DataCallback callback) {
        db.collection("Specializations").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            ArrayList<Specialization> specializations = new ArrayList<>();
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                Specialization specialization = createSpecializationFromDocument(document);
                                specializations.add(specialization);
                            }
                            callback.onSuccess(specializations);
                        } else {
                            callback.onFailure(new Exception("No Specializations found"));
                        }
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    private Specialization createSpecializationFromDocument(QueryDocumentSnapshot document) {
        Specialization specialization = new Specialization();
        specialization.setSpecId(document.getString("id"));
        specialization.setName(document.getString("specialization"));

        return specialization;
    }

    public void fetchSpecialization(DataCallback callback) {
        retrieveSpecializations(callback);
    }

    public void updateFAQ() {
        // Implementation for updating FAQ
    }

    public void insertSpecialization(String name, ProgressDialog pd, Context context) {
        String id = UUID.randomUUID().toString();
        Map<String, Object> specialization = new HashMap<>();
        specialization.put("id", id);
        specialization.put("specialization", name);

        db.collection("Specializations").document(id).set(specialization)
                .addOnCompleteListener(task -> {
                    pd.dismiss();
                    Toast.makeText(context, "Specialization added", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(context, "Failed to add Specialization", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchSpecializations() {

    }
}