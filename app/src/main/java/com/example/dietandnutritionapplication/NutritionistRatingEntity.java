package com.example.dietandnutritionapplication;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NutritionistRatingEntity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ArrayList<NutritionistRating> ratingList = new ArrayList<>();
    private ArrayList<AppRatingsReviews> ratingApp = new ArrayList<>();

    private static final String COLLECTION_NAME = "NutritionistRating";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_REVIEW = "review";
    private static final String FIELD_STAR = "star";
    private static final String FIELD_DATE = "date";
    private static final String FIELD_USER = "user";
    private static final String FIELD_NutriName = "nutritionistName";


    public NutritionistRatingEntity() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public interface DataCallback {
        void onSuccess(ArrayList<AppRatingsReviews> ratingList);
        void onFailure(Exception e);
    }
    public void retrieveAndStoreRatings(final NutritionistRatingEntity.DataCallback callback) {
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
                                String user = document.getString(FIELD_USER);
                                String nutriName = document.getString(FIELD_NutriName);

                                NutritionistRating nutritionistRating = new NutritionistRating(
                                        title,
                                        review,
                                        star,
                                        date,
                                        user,
                                        nutriName

                                );
                                AppRatingsReviews appRatingsReviews = new AppRatingsReviews(
                                        title,
                                        review,
                                        star,
                                        date,
                                        user
                                );

                                ratingList.add(nutritionistRating);
                                ratingApp.add(appRatingsReviews);
                            }
                            callback.onSuccess(ratingApp); // Notify success
                        } else {
                            callback.onFailure(new Exception("QuerySnapshot is null")); // Notify failure if QuerySnapshot is null
                        }
                    } else {
                        callback.onFailure(task.getException()); // Notify failure for other errors
                    }
                });
    }

    public void retrieveRatingsByNutritionist(String nutritionistUsername, final NutritionistRatingEntity.DataCallback callback) {
        ratingList.clear(); // Clear the list before fetching new data
        db.collection(COLLECTION_NAME)
                .whereEqualTo(FIELD_NutriName, nutritionistUsername) // Filter by nutritionist username
                .get()
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
                                String user = document.getString(FIELD_USER);
                                String nutriName = document.getString(FIELD_NutriName);

                                NutritionistRating nutritionistRating = new NutritionistRating(
                                        title,
                                        review,
                                        star,
                                        date,
                                        user,
                                        nutriName
                                );

                                AppRatingsReviews appRatingsReviews = new AppRatingsReviews(
                                        title,
                                        review,
                                        star,
                                        date,
                                        user
                                );

                                ratingList.add(nutritionistRating);
                                ratingApp.add(appRatingsReviews);
                            }
                            callback.onSuccess(ratingApp); // Notify success
                        } else {
                            callback.onFailure(new Exception("QuerySnapshot is null")); // Notify failure if QuerySnapshot is null
                        }
                    } else {
                        callback.onFailure(task.getException()); // Notify failure for other errors
                    }
                });
    }
}
