package com.fyp.dietandnutritionapplication;

import android.util.Log;

import java.sql.Timestamp;

public class UserDiaryController {

    private UserDiary userDiary;
    private MealRecord mealRecord;

    // Constructor to initialize the UserDiary instance
    public UserDiaryController() {
        this.userDiary = new UserDiary();
        this.mealRecord = new MealRecord();
    }

    public void handleDiaryEntry(Timestamp entryDateTime, String mealRecordID, String thoughts, String tags, String username, String mealRecordString) {
        if (validateDiaryEntry(entryDateTime, mealRecordID, thoughts)) {
            UserDiary userDiary = new UserDiary(null, entryDateTime, mealRecordID, thoughts, tags, username, mealRecordString);
            userDiary.saveDiaryEntry();
        } else {
            Log.w("UserDiaryController", "Validation failed for diary entry.");
        }
    }

    public void fetchAllMealsLogged(String username, String selectedDateStr, MealRecord.OnMealsFetchedListener listener) {
        mealRecord.fetchDiaryMealsLogged(username, selectedDateStr ,listener);
    }


    public void fetchDiaryEntries(String username, UserDiary.OnDiaryEntriesFetchedListener listener) {
        userDiary.fetchDiaryEntries(username, listener);
    }

    public void deleteDiaryEntry(String diaryID, UserDiaryFragment.OnEntryDeletedListener listener) {
        userDiary.deleteDiaryEntry(diaryID, listener);
    }

    public void updateDiaryEntry(String diaryID, UserDiary entry, UserDiaryFragment.OnEntryUpdatedListener listener) {
        userDiary.updateDiaryEntry(diaryID, entry, listener);
    }


    private boolean validateDiaryEntry(Timestamp entryDateTime, String mealType, String thoughts) {
        return entryDateTime != null && mealType != null && !mealType.isEmpty() && thoughts != null && !thoughts.isEmpty();
    }
}