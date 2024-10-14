package com.fyp.dietandnutritionapplication;

import static android.app.PendingIntent.getActivity;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class MealRecord {
    private String mealName;
    private double calories;
    private String imageUrl;
    private String mealType;  // This indicates whether it's breakfast, lunch, dinner, or snack
    private double proteins;
    private double carbs;
    private double fats;
    private Timestamp createdDate; // Date when the meal was logged
    private Timestamp modifiedDate;
    private String servingSize;
    private String username;
    private String mealRecordID;
    private String userId;

    public MealRecord() {
    }

    // Constructor
    public MealRecord(String mealName, double calories, String imageUrl, String mealType,
                      double carbs, double proteins, double fats, Timestamp createdDate,
                      Timestamp modifiedDate, String servingSize, String username, String mealRecordID, String userId) {
        this.mealName = mealName;
        this.calories = calories;
        this.imageUrl = imageUrl;
        this.mealType = mealType;
        this.carbs = carbs;
        this.proteins = proteins;
        this.fats = fats;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.servingSize = servingSize;
        this.username = username;
        this.mealRecordID = mealRecordID;
        this.userId = userId;
    }

    // Getters
    public String getMealName() {
        return mealName;
    }

    public Double getCalories() {
        return calories;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getMealType() {
        return mealType;
    }

    public double getCarbs() {
        return carbs;
    }

    public double getProteins() {
        return proteins;
    }

    public double getFats() {
        return fats;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public Timestamp getModifiedDate() {
        return modifiedDate;
    }

    public String getServingSize() {
        return servingSize;
    }

    public String getUsername() {
        return username;
    }

    public String getUserId() {
        return userId;
    }

    public String getMealRecordID() {return mealRecordID;}

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    public void setProteins(double proteins) {
        this.proteins = proteins;
    }

    public void setFats(double fats) {
        this.fats = fats;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public void setModifiedDate(Timestamp modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public void setServingSize(String servingSize) {
        this.servingSize = servingSize;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setMealRecordID(String mealRecordID) {
        this.mealRecordID = mealRecordID;
    }

    public void fetchMealsLogged(String username, String selectedDateStr, OnMealsFetchedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Log.d("MealLogUFragment", "fetchMealsLogged() date:" + selectedDateStr);

        db.collection("MealRecords")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    Log.d("MealLogUFragment", "fetching username and date " + username);
                    if (task.isSuccessful()) {
                        Log.d("MealLogUFragment", "task.isSuccessful()");
                        List<MealRecord> mealRecords = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            MealRecord mealRecord = document.toObject(MealRecord.class);
                            // Check if createdDate is a String and convert it to Timestamp or Date
                            if (document.contains("createdDate")) {
                                Object createdDateObj = document.get("createdDate");
                                if (createdDateObj instanceof Timestamp) {
                                    mealRecord.setCreatedDate((Timestamp) createdDateObj);
                                } else if (createdDateObj instanceof String) {
                                    // If it's a string, convert to Timestamp
                                    String dateString = (String) createdDateObj;
                                    try {
                                        // Assuming the date is stored in a standard format, e.g., "yyyy-MM-dd"
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                        Date parsedDate = sdf.parse(dateString);
                                        mealRecord.setCreatedDate(new Timestamp(parsedDate)); // Convert to Timestamp
                                    } catch (ParseException e) {
                                        Log.e("MealLogUFragment", "Error parsing date string: " + dateString, e);
                                    }
                                }
                                if (isSameDate(mealRecord.getCreatedDate(), selectedDateStr)) {
                                    mealRecords.add(mealRecord);
                                }
                            }
                            listener.onMealsFetched(mealRecords);
                        }
                        Log.d("MealLogUFragment", "Fetched meal records: " + mealRecords.size());
                    } else {
                        Log.w("MealLogUFragment", "Error getting documents.", task.getException());
                    }
                });
    }

    private boolean isSameDate(Timestamp mealDate, String selectedDateStr) {
        if (mealDate == null) return false;

        try {
            // Convert Timestamp to Date
            Date date = mealDate.toDate();

            // Create a calendar instance for Singapore timezone
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"));
            calendar.setTime(date);

            // Format the date to "yyyy-MM-dd"
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            dateFormat.setTimeZone(calendar.getTimeZone()); // Set the formatter timezone to Singapore
            String mealDateFormatted = dateFormat.format(calendar.getTime());

            // Compare the formatted date with the selected date string
            return selectedDateStr.equals(mealDateFormatted);
        } catch (Exception e) {
            Log.e("MealLogFragment", "Error formatting date: " + e.getMessage());
            return false; // Return false in case of any formatting error
        }
    }

    public void fetchUsernameAndCalorielimit(String userId, OnUsernameAndCalorieLimitFetchedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username");
                        double calorieLimit = documentSnapshot.getDouble("calorieLimit");

                        listener.onDataFetched(username, calorieLimit);
                    } else {
                        Log.d("User", "No such user document exists!");
                        listener.onDataFetched(null, -1.0);
                    }
                });
    }

    public void calculateRemainingCalories(String userId, String selectedDate, OnRemainingCaloriesCalculatedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Log.d("CalorieTracking", "SelectedDate: " + selectedDate);

        TimeZone singaporeTimeZone = TimeZone.getTimeZone("Asia/Singapore");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(singaporeTimeZone);

        // Get the calorie limit for the user
        db.collection("Users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String username = document.getString("username");
                    Log.d("CalorieTracking", "Username: " + username);
                    int calorieLimit = document.getLong("calorieLimit").intValue();
                    Log.d("CalorieTracking", "Daily Calorie Limit: " + calorieLimit);

                    // Retrieve logged meals for today
                    db.collection("MealRecords")
                            .whereEqualTo("username", username)
                            .get()
                            .addOnCompleteListener(mealTask -> {
                                if (mealTask.isSuccessful()) {
                                    int totalCalories = 0;
                                    for (DocumentSnapshot mealDocument : mealTask.getResult()) {
                                        Log.d("CalorieTracking", "Meal Document: " + mealDocument.getId() + " - Calories: " + mealDocument.getDouble("calories") + " - Created Date: " + mealDocument.getTimestamp("createdDate"));
                                        if (mealDocument.contains("calories") && mealDocument.contains("createdDate")) {
                                            Timestamp createdDateTimestamp = mealDocument.getTimestamp("createdDate");
                                            if (createdDateTimestamp != null) {
                                                String createdDate = dateFormat.format(createdDateTimestamp.toDate());
                                                Log.d("CalorieTracking", "Selected Date: " + selectedDate);
                                                Log.d("CalorieTracking", "Meal Date: " + createdDate);
                                                // Compare dates
                                                if (createdDate.equals(selectedDate)) {
                                                    double mealCalories = mealDocument.getDouble("calories");
                                                    totalCalories += mealCalories; // Accumulate total calories
                                                }
                                            }
                                        } else {
                                            Log.w("CalorieTracking", "Calories or createdDate field missing in meal document");
                                        }
                                    }
                                    double remainingCalories = calorieLimit - totalCalories;
                                    Log.d("CalorieTracking", "Total Calories Consumed: " + totalCalories);
                                    Log.d("CalorieTracking", "Remaining Calories: " + remainingCalories);

                                    // Call the listener with the results

                                    if (listener != null) {
                                        listener.onRemainingCaloriesCalculated(calorieLimit, remainingCalories);
                                    }

                                } else {
                                    Log.e("CalorieTracking", "Error retrieving meals: ", mealTask.getException());
                                }
                            });
                } else {
                    Log.e("CalorieTracking", "User document does not exist.");
                }
            } else {
                Log.e("CalorieTracking", "Error fetching user data: ", task.getException());
            }
        });
    }

    public void storeMealData(String userId,String username, String mealName, String selectedMealType, String servingInfo, double adjustedCalories, double adjustedCarbohydrates, double adjustedProtein, double adjustedFat, String selectedDateStr, String imageUrl){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));

        Map<String, Object> mealData = new HashMap<>();
        mealData.put("mealName", mealName);
        mealData.put("mealType", selectedMealType);
        mealData.put("servingSize", servingInfo);
        mealData.put("calories", adjustedCalories);
        mealData.put("carbs", adjustedCarbohydrates);
        mealData.put("proteins", adjustedProtein);
        mealData.put("fats", adjustedFat);
        mealData.put("imageUrl", imageUrl);

        try {
            // Parse the string to a Date object and convert to Timestamp directly
            Timestamp createdTimestamp = new Timestamp(sdf.parse(selectedDateStr));
            mealData.put("createdDate", createdTimestamp);
        } catch (ParseException e) {
            Log.e("MealLogUFragment", "Error parsing date", e);
            // Handle error (e.g., use current date if parsing fails)
            mealData.put("createdDate", new Timestamp(new Date())); // Current date
        }
        mealData.put("modifiedDate", null);
        mealData.put("userId", userId);
        mealData.put("username", username);

        db.collection("MealRecords")
                .add(mealData)
                .addOnSuccessListener(documentReference -> {
                    setMealRecordID(documentReference.getId());
                    db.collection("MealRecords").document(documentReference.getId())
                            .update("mealRecordID", documentReference.getId())
                            .addOnSuccessListener(aVoid -> Log.d("MealRecords", "ID updated successfully"))
                            .addOnFailureListener(e -> Log.w("MealRecords", "Error updating ID", e));

                    Log.d("MealLogUFragment", "Meal added with ID: " + documentReference.getId());
                    //Toast.makeText(activity, "Meal added successfully", Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(e -> {
                    Log.w("MealLogUFragment", "Error adding meal", e);
                });
    }

    public interface OnMealsFetchedListener {
        void onMealsFetched(List<MealRecord> mealRecords);
    }

    public interface OnUsernameAndCalorieLimitFetchedListener {
        void onDataFetched(String username, double calorieLimit);
    }


    public interface OnRemainingCaloriesCalculatedListener {
        void onRemainingCaloriesCalculated(double calorieLimit, double remainingCalories);
    }

    public void deleteMealRecord(String mealRecordID, MealRecord.OnMealDeletedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("MealRecords").document(mealRecordID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Notify that the meal was deleted successfully
                        listener.onMealDeleted();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Notify if there was an error during the delete operation
                        listener.onError(e.getMessage());
                    }
                });
    }

    public interface OnMealDeletedListener {
        void onMealDeleted();
        void onError(String error);
    }


    public void updateMealRecord(String mealRecordID, MealRecord mealRecord) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> mealRecordMap = new HashMap<>();
        mealRecordMap.put("mealType", mealRecord.getMealType());
        mealRecordMap.put("servingSize", mealRecord.getServingSize());
        mealRecordMap.put("calories", mealRecord.getCalories());
        mealRecordMap.put("carbs", mealRecord.getCarbs());
        mealRecordMap.put("proteins", mealRecord.getProteins());
        mealRecordMap.put("fats", mealRecord.getFats());

        Timestamp modifiedTimestamp = new Timestamp(new Date());
        mealRecordMap.put("modifiedDate", modifiedTimestamp);

        // If using Firebase Firestore
        db.collection("MealRecords")
                .document(mealRecordID) // Reference to the specific meal record by ID
                .update(mealRecordMap)
                .addOnSuccessListener(aVoid -> {
                    Log.d("MealLogFragment", "Meal updated successfully: " + mealRecordID);
                    // Toast or other feedback can be added here
                })
                .addOnFailureListener(e -> {
                    Log.w("MealLogFragment", "Error updating meal", e);
                });
    }
}