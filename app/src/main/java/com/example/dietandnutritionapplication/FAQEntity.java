package com.example.dietandnutritionapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
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

public class FAQEntity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public FAQEntity() {
        db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
    }

    public interface DataCallback {
        void onSuccess(ArrayList<FAQ> faqs);
        void onFailure(Exception e);
    }

    public void retrieveFAQs(final DataCallback callback) {
        db.collection("FAQ").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            ArrayList<FAQ> faqs = new ArrayList<>();
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                FAQ faq = createFAQFromDocument(document);
                                faqs.add(faq);
                            }
                            callback.onSuccess(faqs);
                        } else {
                            callback.onFailure(new Exception("No FAQs found"));
                        }
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    private FAQ createFAQFromDocument(QueryDocumentSnapshot document) {
        FAQ faq = new FAQ();
        faq.setFaqId(document.getString("id"));
        faq.setTitle(document.getString("title"));
        faq.setCategory(document.getString("category"));
        faq.setQuestion(document.getString("question"));
        faq.setAnswer(document.getString("answer"));
        faq.setDateCreated(document.getString("date"));
        return faq;
    }

    public void fetchFAQ(DataCallback callback) {
        retrieveFAQs(callback);
    }

    public void updateFAQ() {
        // Implementation for updating FAQ
    }

    public void insertFAQ(String title, String category, String question, String answer, String date, ProgressDialog pd, Context context) {
        String id = UUID.randomUUID().toString();
        Map<String, Object> faq = new HashMap<>();
        faq.put("id", id);
        faq.put("title", title);
        faq.put("category", category);
        faq.put("question", question);
        faq.put("answer", answer);
        faq.put("date", date);

        db.collection("FAQ").document(id).set(faq)
                .addOnCompleteListener(task -> {
                    pd.dismiss();
                    Toast.makeText(context, "FAQ added", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(context, "Failed to add FAQ", Toast.LENGTH_SHORT).show();
                });
    }

    public void updateFAQInFirestore(String id, String updatedTitle, String updatedCategory, String updatedQuestion, String updatedAnswer, String updatedDate, Context context) {
        if (id != null) {
            Map<String, Object> updatedFields = new HashMap<>();
            updatedFields.put("date", updatedDate);
            updatedFields.put("title", updatedTitle);
            updatedFields.put("category", updatedCategory);
            updatedFields.put("question", updatedQuestion);
            updatedFields.put("answer", updatedAnswer);

            db.collection("FAQ").document(id)
                    .update(updatedFields)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "FAQ updated successfully", Toast.LENGTH_SHORT).show();

                        FAQ updatedFAQ = new FAQ(id, updatedTitle, updatedCategory, updatedQuestion, updatedAnswer, updatedDate);
                        FAQDetailsFragment viewFAQDetailsFragment = new FAQDetailsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("selectedFAQ", updatedFAQ);
                        viewFAQDetailsFragment.setArguments(bundle);

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Failed to update FAQ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(context, "Error: FAQ ID is null", Toast.LENGTH_SHORT).show();
        }
    }
}