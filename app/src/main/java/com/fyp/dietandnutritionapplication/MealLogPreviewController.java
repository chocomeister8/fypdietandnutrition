package com.fyp.dietandnutritionapplication;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MealLogPreviewController {

    private static final String TAG = "MealLogPreviewController";
    private final MutableLiveData<String> selectedDateLiveData = new MutableLiveData<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());

    public MealLogPreviewController() {
        // Initialize with today's date
        Calendar today = Calendar.getInstance();
        updateDate(today);
    }

    public LiveData<String> getSelectedDate() {
        return selectedDateLiveData;
    }

    public void updateDate(Calendar calendar) {
        String formattedDate = dateFormat.format(calendar.getTime());
        Log.d(TAG, "Updating date to: " + formattedDate);
        selectedDateLiveData.setValue(formattedDate);

        // Fetch and update data for the selected date
        fetchDataForDate(formattedDate);
    }

    private void fetchDataForDate(String date) {
        // This is where you would fetch data from your database or API
        // For example:
        // List<MealLog> mealLogs = mealLogRepository.getMealLogsByDate(date);

        // For demonstration purposes, we're just logging the date
        Log.d(TAG, "Fetching data for date: " + date);

        // Simulate updating UI or notifying observers
        // updateUIWithMealLogs(mealLogs);
    }

    // You can add more methods to interact with your data source as needed
}
