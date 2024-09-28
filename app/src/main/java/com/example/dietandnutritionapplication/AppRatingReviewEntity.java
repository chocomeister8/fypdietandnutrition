package com.example.dietandnutritionapplication;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AppRatingReviewEntity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ArrayList<AppRatingsReviews> ratingList = new ArrayList<>();

    // Define constants for Firestore collection and field names
    private static final String COLLECTION_NAME = "AppRating";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_REVIEW = "review";
    private static final String FIELD_STAR = "star";
    private static final String FIELD_DATE = "date";
    private static final String FIELD_USERNAME = "username";

    public AppRatingReviewEntity() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public interface DataCallback {
        void onSuccess(ArrayList<AppRatingsReviews> ratingList);
        void onFailure(Exception e);
    }

    public void retrieveAndStoreRatings(final DataCallback callback) {
        ratingList.clear(); // Clear the list before fetching new data
        db.collection(COLLECTION_NAME).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                // Retrieve data with null checks
                                String title = document.getString(FIELD_TITLE);
                                String review = document.getString(FIELD_REVIEW);
                                Float star = document.getDouble(FIELD_STAR) != null ? document.getDouble(FIELD_STAR).floatValue() : 0.0f; // Default to 0 if null
                                String date = document.getString(FIELD_DATE);
                                String username = document.getString(FIELD_USERNAME);

                                AppRatingsReviews appRatingsReviews = new AppRatingsReviews(
                                        title,
                                        review,
                                        star,
                                        date,
                                        username
                                );
                                ratingList.add(appRatingsReviews);
                            }
                            callback.onSuccess(ratingList); // Notify success
                        } else {
                            callback.onFailure(new Exception("QuerySnapshot is null")); // Notify failure if QuerySnapshot is null
                        }
                    } else {
                        callback.onFailure(task.getException()); // Notify failure for other errors
                    }
                });
    }

    public void storeNewReview(String title, String review, float star, String date, String username, final DataCallback callback) {
        // Create a new review object
        Map<String, Object> newReview = new HashMap<>();
        newReview.put(FIELD_TITLE, title);
        newReview.put(FIELD_REVIEW, review);
        newReview.put(FIELD_STAR, star);
        newReview.put(FIELD_DATE, date);
        newReview.put(FIELD_USERNAME, username);

        // Store the review in Firestore
        db.collection(COLLECTION_NAME).add(newReview)
                .addOnSuccessListener(documentReference -> {
                    callback.onSuccess(null); // Notify success
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e); // Notify failure
                });
    }


}