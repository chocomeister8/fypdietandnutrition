package com.example.dietandnutritionapplication;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.google.firebase.Timestamp;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class UserMealRecordController {

    private MealRecord mealRecord;


    public UserMealRecordController() {
        this.mealRecord = new MealRecord();
    }

    public void calculateRemainingCalories(String userId, String selectedDate, MealRecord.OnRemainingCaloriesCalculatedListener listener) {
        mealRecord.calculateRemainingCalories(userId, selectedDate, listener);
    }

    public void fetchMealsLogged(String username, String selectedDateStr, MealRecord.OnMealsFetchedListener listener) {
        mealRecord.fetchMealsLogged(username, selectedDateStr, listener);
    }

    public void storeMealData(String userId,String username, String mealName, String selectedMealType, String servingInfo, double adjustedCalories, double adjustedCarbohydrates, double adjustedProtein, double adjustedFat, String selectedDateStr, String imageURL){
        mealRecord.storeMealData(userId, username, mealName, selectedMealType, servingInfo, adjustedCalories, adjustedCarbohydrates, adjustedProtein, adjustedFat, selectedDateStr, imageURL);;
    }

    public void fetchUsernameAndCalorieLimit(String userId, MealRecord.OnUsernameAndCalorieLimitFetchedListener listener) {
        mealRecord.fetchUsernameAndCalorielimit(userId, listener);
    }

    public void deleteMealRecord(String mealRecordID, MealRecord.OnMealDeletedListener listener){
        mealRecord.deleteMealRecord(mealRecordID, listener);;
    }

    public void updateMealRecord(String mealRecordID, MealRecord mealRecord){
        mealRecord.updateMealRecord(mealRecordID, mealRecord);;
    }

}
