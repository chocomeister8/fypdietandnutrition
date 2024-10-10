package com.example.dietandnutritionapplication;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConsultationEntity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ArrayList<Consultation> consultationList = new ArrayList<>();

    public ConsultationEntity() {
        db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
    }

    // Callback interface for data retrieval results
    public interface DataCallback {
        void onSuccess(ArrayList<Consultation> consultationList);
        void onFailure(Exception e);
    }

    // Retrieve all consultations
    public void retrieveConsultationSlots(final DataCallback callback) {
        db.collection("Consultation_slots").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            ArrayList<Consultation> slots = new ArrayList<>();

                            for (QueryDocumentSnapshot document : querySnapshot) {
                                Consultation slot = createSlotFromDocument(document);
                                slots.add(slot);
                            }

                            callback.onSuccess(slots);
                        } else {
                            callback.onFailure(new Exception("QuerySnapshot is null"));
                        }
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    private Consultation createSlotFromDocument(QueryDocumentSnapshot document) {
        Consultation slot = new Consultation();
        slot.setNutritionistName(document.getString("nutritionistName"));
        slot.setConsultationId(document.getString("consultationId"));
        slot.setDate(document.getString("date"));
        slot.setTime(document.getString("time"));
        slot.setStatus(document.getString("booked"));
        return slot;
    }

    public void fetchAccounts(ConsultationEntity.DataCallback callback) {
        retrieveConsultationSlots(callback);
    }

    public void addConsultation(String nutritionistName, String date, String time, String status, String userId, DataCallback callback) {
        // Create a new consultation object
        Consultation consultation = new Consultation();
        consultation.setNutritionistName(nutritionistName);
        consultation.setDate(date);
        consultation.setTime(time);
        consultation.setStatus(status);

        // Store the consultation in Firestore
        db.collection("Consultation_slots").add(consultation)
                .addOnSuccessListener(documentReference -> {
                    // Set the consultation ID in the consultation object
                    consultation.setConsultationId(documentReference.getId());

                    // Optionally: Save the consultation ID back to Firestore under the user's document
                    db.collection("Users").document(userId)
                            .update("consultationIds", FieldValue.arrayUnion(documentReference.getId()))
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    callback.onSuccess(consultationList); // Notify success
                                } else {
                                    callback.onFailure(new Exception("Failed to update user document with consultation ID"));
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(new Exception(e.getMessage())); // Notify failure
                });
    }




//    public void addConsultationSlot(Consultation slot, DataCallback callback) {
//        db.collection("Consultation_slots").add(slot)
//                .addOnSuccessListener(documentReference -> callback.onSuccess())
//                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
//    }

//    // Retrieve consultations by nutritionist
//    public void retrieveConsultationsByNutritionist(String nutritionistUsername, final DataCallback callback) {
//        consultationList.clear(); // Clear the list before fetching new data
//        db.collection(COLLECTION_NAME)
//                .whereEqualTo(FIELD_NUTRITIONIST_NAME, nutritionistUsername) // Filter by nutritionist username
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        QuerySnapshot querySnapshot = task.getResult();
//                        if (querySnapshot != null) {
//                            for (QueryDocumentSnapshot document : querySnapshot) {
//                                // Create Consultation object with document ID as consultationId
//                                Consultation consultation = new Consultation(
//                                        document.getId(), // Firestore document ID
//                                        document.getString(FIELD_NUTRITIONIST_NAME),
//                                        document.getString(FIELD_USER), // Assuming user is clientName
//                                        document.getString(FIELD_DATE),
//                                        document.getString(FIELD_TIME),
//                                        document.getString(FIELD_STATUS)
//                                );
//
//                                // Add to the list
//                                consultationList.add(consultation);
//                            }
//                            callback.onSuccess(consultationList); // Notify success
//                        } else {
//                            callback.onFailure(new Exception("QuerySnapshot is null")); // Notify failure if QuerySnapshot is null
//                        }
//                    } else {
//                        callback.onFailure(task.getException()); // Notify failure for other errors
//                    }
//                });
//    }
//
//
//    // Store a new consultation
//    public void storeNewConsultation(String date, String time, String username, String nutritionistName, String status, final DataCallback callback) {
//        // Create a new consultation object
//        Map<String, Object> newConsultation = new HashMap<>();
//        newConsultation.put(FIELD_DATE, date);
//        newConsultation.put(FIELD_TIME, time);
//        newConsultation.put(FIELD_USER, username);
//        newConsultation.put(FIELD_NUTRITIONIST_NAME, nutritionistName);
//        newConsultation.put(FIELD_STATUS, status);
//
//        // Store the consultation in Firestore
//        db.collection("Consultation_slots").add(newConsultation)
//                .addOnSuccessListener(documentReference -> {
//                    // Option 1: You could create and return a Consultation object here
//                    Consultation consultation = new Consultation(
//                            documentReference.getId(), nutritionistName, username, date, time, status
//                    );
//                    ArrayList<Consultation> consultationList = new ArrayList<>();
//                    consultationList.add(consultation);
//
//                    callback.onSuccess(consultationList); // Notify success and return the created consultation
//                })
//                .addOnFailureListener(e -> {
//                    callback.onFailure(e); // Notify failure
//                });
//    }

}
