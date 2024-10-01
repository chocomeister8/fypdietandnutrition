package com.example.dietandnutritionapplication;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class UserDiary {

    // Private fields
    private Timestamp entryDateTime;
    private String mealType;
    private String thoughts;
    private String tags;
    private String username;
    private String entryContent;

    public UserDiary() {
    }

    public UserDiary(String entryContent) {
        this.entryContent = entryContent;
    }

    // Constructor
    public UserDiary(Timestamp entryDateTime, String mealType, String thoughts, String tags, String username) {
        this.entryDateTime = entryDateTime;
        this.mealType = mealType;
        this.thoughts = thoughts;
        this.tags = tags;
        this.username = username;
    }

    // Getter for entryDateTime
    public Timestamp getEntryDateTime() {
        return entryDateTime;
    }

    // Setter for entryDateTime
    public void setEntryDateTime(Timestamp entryDateTime) {
        this.entryDateTime = entryDateTime;
    }

    // Getter for mealType
    public String getMealType() {
        return mealType;
    }

    // Setter for mealType
    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    // Getter for thoughts
    public String getThoughts() {
        return thoughts;
    }

    // Setter for thoughts
    public void setThoughts(String thoughts) {
        this.thoughts = thoughts;
    }

    // Getter for tags
    public String getTags() {
        return tags;
    }

    // Setter for tags
    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getUsername() {
        return username;
    }

    // Setter for tags
    public void setUsername(String username) {
        this.username = username;
    }

    public String getEntryContent() {
        return entryContent;
    }

    public void saveDiaryEntry() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("UserDiaries")
                .add(this)
                .addOnSuccessListener(documentReference -> {
                    // Successfully added
                    Log.d("UserDiary", "Diary entry added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                    Log.w("UserDiary", "Error adding diary entry", e);
                });
    }

    public void fetchDiaryEntries(String username, OnDiaryEntriesFetchedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("UserDiaries")
                .whereEqualTo("username", username) // Assuming you want to fetch entries for the logged-in user
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<UserDiary> diaryEntries = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        UserDiary entry = new UserDiary();
                        entry.setMealType(document.getString("mealType"));
                        entry.setThoughts(document.getString("thoughts"));
                        entry.setTags(document.getString("tags"));

                        // Convert the timestamp
                        Date date = document.getDate("entryDateTime");
                        if (date != null) {
                            entry.setEntryDateTime(new Timestamp(date.getTime()));
                        }
                        diaryEntries.add(entry);
                    }
                    listener.onDiaryEntriesFetched(diaryEntries);
                })
                .addOnFailureListener(e -> {
                    Log.w("UserDiaryController", "Error fetching diary entries", e);
                    listener.onDiaryEntriesFetched(Collections.emptyList());
                });
    }

    public interface OnDiaryEntriesFetchedListener {
        void onDiaryEntriesFetched(List<UserDiary> diaryEntries);
    }
}
