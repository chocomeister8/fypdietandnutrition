package com.fyp.dietandnutritionapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RescheduleConsultationFragment extends Fragment {

    private Button datePickerButton, timePickerButton, updateButton, deleteButton;
    private EditText zoomLinkEditText;
    private FirebaseFirestore db;
    private String consultationId;
    private String selectedDate = "", selectedTime = "", selectedZoom = "";

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_consultation, container, false);

        db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (getArguments() != null) {
            consultationId = getArguments().getString("consultationId");
        }

        // Initialize views
        datePickerButton = view.findViewById(R.id.date_picker_button);
        timePickerButton = view.findViewById(R.id.time_picker_button);
        updateButton = view.findViewById(R.id.button_update);
        deleteButton = view.findViewById(R.id.delete_button);
        zoomLinkEditText = view.findViewById(R.id.zoom_link_edit_text);

        // Date Picker
        datePickerButton.setOnClickListener(v -> showDatePickerDialog());

        // Time Picker
        timePickerButton.setOnClickListener(v -> showTimePickerDialog());

        updateButton.setOnClickListener(v -> updateConsultation());
        deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog());

        return view;
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1); // Set to tomorrow
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                    datePickerButton.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, hourOfDay, minute) -> {
                    selectedTime = hourOfDay + ":" + (minute < 10 ? "0" + minute : minute);
                    timePickerButton.setText(selectedTime);
                },
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    private boolean isValidZoomLink(String link) {
        return Patterns.WEB_URL.matcher(link).matches() && link.contains("zoom.us");
    }

    private void updateConsultation() {
        String newZoomLink = zoomLinkEditText.getText().toString().trim();

        if (TextUtils.isEmpty(selectedDate) || TextUtils.isEmpty(selectedTime) || TextUtils.isEmpty(newZoomLink) || !isValidZoomLink(newZoomLink)) {
            Toast.makeText(getContext(), "Please select a valid date, time, and Zoom link", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Fetch current user's username
            db.collection("Users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String currentUsername = documentSnapshot.getString("username");

                            // Query to verify consultationId belongs to the current nutritionist
                            db.collection("Consultation_slots")
                                    .whereEqualTo("consultationId", consultationId)
                                    .whereEqualTo("nutritionistName", currentUsername)
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                        if (!queryDocumentSnapshots.isEmpty()) {
                                            // Update the consultation
                                            db.collection("Consultation_slots").document(queryDocumentSnapshots.getDocuments().get(0).getId())
                                                    .update("date", selectedDate, "time", selectedTime, "zoomLink", newZoomLink)
                                                    .addOnSuccessListener(aVoid -> {
                                                        Toast.makeText(getContext(), "Consultation updated successfully", Toast.LENGTH_SHORT).show();
                                                        requireActivity().getSupportFragmentManager().popBackStack();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Log.e("FirestoreError", "Error updating consultation", e);
                                                        Toast.makeText(getContext(), "Error updating consultation", Toast.LENGTH_SHORT).show();
                                                    });
                                        } else {
                                            Toast.makeText(getContext(), "You do not have permission to update this consultation.", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> Log.e("FirestoreError", "Error retrieving consultation document", e));
                        } else {
                            Log.e("FirestoreError", "User document does not exist.");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirestoreError", "Error retrieving user document", e);
                        Toast.makeText(getContext(), "Failed to retrieve user information.", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Log.e("FirebaseError", "No user is currently logged in.");
            Toast.makeText(getContext(), "You must be logged in to update consultation.", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteConsultation() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Fetch current user's username to verify consultation ownership
            db.collection("Users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String currentUsername = documentSnapshot.getString("username");

                            // Query to verify consultationId belongs to the current nutritionist
                            db.collection("Consultation_slots")
                                    .whereEqualTo("consultationId", consultationId)
                                    .whereEqualTo("nutritionistName", currentUsername)
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                        if (!queryDocumentSnapshots.isEmpty()) {
                                            // Proceed with deletion
                                            db.collection("Consultation_slots").document(queryDocumentSnapshots.getDocuments().get(0).getId())
                                                    .delete()
                                                    .addOnSuccessListener(aVoid -> {
                                                        Toast.makeText(getContext(), "Consultation deleted successfully", Toast.LENGTH_SHORT).show();
                                                        requireActivity().getSupportFragmentManager().popBackStack();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Log.e("FirestoreError", "Error deleting consultation", e);
                                                        Toast.makeText(getContext(), "Error deleting consultation", Toast.LENGTH_SHORT).show();
                                                    });
                                        } else {
                                            Toast.makeText(getContext(), "You do not have permission to delete this consultation.", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> Log.e("FirestoreError", "Error retrieving consultation document", e));
                        } else {
                            Log.e("FirestoreError", "User document does not exist.");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirestoreError", "Error retrieving user document", e);
                        Toast.makeText(getContext(), "Failed to retrieve user information.", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Log.e("FirebaseError", "No user is currently logged in.");
            Toast.makeText(getContext(), "You must be logged in to delete consultation.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Consultation")
                .setMessage("Are you sure you want to delete this consultation?")
                .setPositiveButton("Yes", (dialog, which) -> deleteConsultation())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
