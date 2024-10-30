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

public class DietPreferenceEntity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public DietPreferenceEntity() {
        db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
    }

    public void insertDietPreference(String dietpreference, ProgressDialog pd, Context context) {
        String id = UUID.randomUUID().toString();
        Map<String, Object> dietPreference = new HashMap<>();
        dietPreference.put("id", id);
        dietPreference.put("diet", dietpreference);

        db.collection("DietOptions").document(id).set(dietPreference)
                .addOnCompleteListener(task -> {
                    pd.dismiss();
                    Toast.makeText(context, "Diet Preference added", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(context, "Failed to add Diet Preference", Toast.LENGTH_SHORT).show();
                });
    }

    public interface DataCallback {
        void onSuccess(ArrayList<DietPreference> dietPreferences);
        void onFailure(Exception e);
    }

    public void retrieveDietPreferences(final DataCallback callback) {
        db.collection("DietOptions").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            ArrayList<DietPreference> dietPreferences = new ArrayList<>();
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                DietPreference dietPreference = createDietPreferenceFromDocument(document);
                                dietPreferences.add(dietPreference);
                            }
                            callback.onSuccess(dietPreferences);
                        } else {
                            callback.onFailure(new Exception("No Diet Preference found"));
                        }
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    private DietPreference createDietPreferenceFromDocument(QueryDocumentSnapshot document) {
        DietPreference dietPreference = new DietPreference();
        dietPreference.setDietPreferenceId(document.getString("id"));
        dietPreference.setDietpreference(document.getString("diet"));

        return dietPreference;
    }

    public void fetchDietPreference(DataCallback callback) {
        retrieveDietPreferences(callback);
    }

}