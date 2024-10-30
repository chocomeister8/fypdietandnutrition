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

public class AllergyOptionsEntity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public AllergyOptionsEntity() {
        db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
    }

    public interface DataCallback {
        void onSuccess(ArrayList<AllergyOptions> allergyOptions);
        void onFailure(Exception e);
    }

    public void retrieveAllergyOptions(final DataCallback callback) {
        db.collection("AllergyOptions").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            ArrayList<AllergyOptions> allergyOptions = new ArrayList<>();
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                AllergyOptions allergyOption = createAllergyOptionFromDocument(document);
                                allergyOptions.add(allergyOption);
                            }
                            callback.onSuccess(allergyOptions);
                        } else {
                            callback.onFailure(new Exception("No Allergy Options found"));
                        }
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    private AllergyOptions createAllergyOptionFromDocument(QueryDocumentSnapshot document) {
        AllergyOptions allergyOptions = new AllergyOptions();
        allergyOptions.setAllergyOptionId(document.getString("id"));
        allergyOptions.setAllergyOption(document.getString("healthPreference"));

        return allergyOptions;
    }

    public void fetchAllergyOptions(DataCallback callback) {
        retrieveAllergyOptions(callback);
    }

    public void insertAllergyOption(String allergyOption, ProgressDialog pd, Context context) {
        String id = UUID.randomUUID().toString();
        Map<String, Object> allergyoption = new HashMap<>();
        allergyoption.put("id", id);
        allergyoption.put("healthPreference", allergyOption);

        db.collection("AllergyOptions").document(id).set(allergyoption)
                .addOnCompleteListener(task -> {
                    pd.dismiss();
                    Toast.makeText(context, "Allergy option added", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(context, "Failed to add Allergy option", Toast.LENGTH_SHORT).show();
                });
    }

}