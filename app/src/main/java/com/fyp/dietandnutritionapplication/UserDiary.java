package com.fyp.dietandnutritionapplication;

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
    private String diaryID;

    public UserDiary() {
    }

    public UserDiary(Timestamp entryDateTime, String mealType, String thoughts, String tags, String username, String diaryID) {
        this.entryDateTime = entryDateTime;
        this.mealType = mealType;
        this.thoughts = thoughts;
        this.tags = tags;
        this.username = username;
        this.diaryID = diaryID;
    }

    public Timestamp getEntryDateTime() {
        return entryDateTime;
    }

    public void setEntryDateTime(Timestamp entryDateTime) {
        this.entryDateTime = entryDateTime;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public String getThoughts() {
        return thoughts;
    }

    public void setThoughts(String thoughts) {
        this.thoughts = thoughts;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDiaryID() {
        return diaryID;
    }

    public void setDiaryID(String diaryID) {
        this.diaryID = diaryID;
    }


    public void saveDiaryEntry() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("UserDiaries")
                .add(this)
                .addOnSuccessListener(documentReference -> {
                    setDiaryID(documentReference.getId());
                    Log.d("UserDiary", "Diary entry added with ID: " + documentReference.getId());
                    db.collection("UserDiaries").document(documentReference.getId())
                            .update("diaryID", documentReference.getId())
                            .addOnSuccessListener(aVoid -> Log.d("UserDiary", "DiaryID updated successfully"))
                            .addOnFailureListener(e -> Log.w("UserDiary", "Error updating diaryID", e));
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
                        entry.setDiaryID(document.getString("diaryID"));

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

    public void deleteDiaryEntry(String diaryID, UserDiaryFragment.OnEntryDeletedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("UserDiaries").document(diaryID)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) {
                        listener.onEntryDeleted(true); // Notify success
                    }
                })
                .addOnFailureListener(e -> {
                    if (listener != null) {
                        listener.onEntryDeleted(false); // Notify failure
                    }
                });
    }

    public void updateDiaryEntry(String diaryID, UserDiary updatedEntry, UserDiaryFragment.OnEntryUpdatedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (diaryID == null) {
            Log.e("UserDiaryController", "Diary ID is null. Cannot update entry.");
            if (listener != null) {
                listener.onEntryUpdated(false);
            }
            return;
        }

        db.collection("UserDiaries")
                .document(diaryID)
                .set(updatedEntry)
                .addOnSuccessListener(aVoid -> {
                    Log.d("UserDiaryController", "Entry updated successfully.");
                    if (listener != null) {
                        listener.onEntryUpdated(true);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("UserDiaryController", "Error updating entry: ", e);
                    if (listener != null) {
                        listener.onEntryUpdated(false);
                    }
                });
    }


}