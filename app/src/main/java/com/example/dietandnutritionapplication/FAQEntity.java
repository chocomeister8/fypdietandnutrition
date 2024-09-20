package com.example.dietandnutritionapplication;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FAQEntity {
    private FirebaseFirestore db;
    private ArrayList<FAQ> faqs = new ArrayList<>();

    FirebaseAuth mAuth;

    public FAQEntity() {

        db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
    }

    public interface DataCallback {
        void onSuccess(ArrayList<FAQ> faqs);
        void onFailure(Exception e);
    }

    public void retrieveFAQs(final FAQEntity.DataCallback callback) {
        db.collection("FAQ").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            faqs.clear();
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                FAQ faq = createFAQFromDocument(document);
                                faqs.add(faq);
                            }
                        } else {
                            callback.onFailure(task.getException());
                        }
                        callback.onSuccess(faqs);
                    }
                });
    }

    private FAQ createFAQFromDocument(QueryDocumentSnapshot document) {
        FAQ faq = new FAQ();
        faq.setFaqId(document.getString("id"));
        faq.setTitle(document.getString("title"));
        faq.setQuestion(document.getString("question"));
        faq.setAnswer(document.getString("answer"));
        faq.setDateCreated(document.getString("date"));

        return faq;
    }
    public void fetchFAQ(FAQEntity.DataCallback callback) {
        retrieveFAQs(callback);
    }


}
