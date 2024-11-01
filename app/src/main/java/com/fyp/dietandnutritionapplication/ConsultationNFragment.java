package com.fyp.dietandnutritionapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ConsultationNFragment extends Fragment {

    private Button datePickerButton, timePickerButton, saveButton;
    private RecyclerView recyclerViewSlots;
    private List<String> availableSlotsList;
    private SlotsAdapter slotsAdapter;
    private String selectedDate = "", selectedTime = "", selectedZoom = "";
    private FirebaseFirestore firestore;
    private String clientName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consultation_slots, container, false);

        // Initialize buttons
        Button button_booking_history = view.findViewById(R.id.booking_history);
        Button button_consultationSlot = view.findViewById(R.id.consultation_slot);
        Button button_pendingConsultation = view.findViewById(R.id.pending_consultation);

        firestore = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        // Initialize views
        datePickerButton = view.findViewById(R.id.date_picker_button);
        timePickerButton = view.findViewById(R.id.time_picker_button);
        saveButton = view.findViewById(R.id.save_button);
        recyclerViewSlots = view.findViewById(R.id.recycler_view_slots);
        EditText zoomLinkEditText = view.findViewById(R.id.zoom_link_edit_text);

        // Initialize list and adapter
        availableSlotsList = new ArrayList<>();

        // Add some hard-coded available time slots
//        availableSlotsList.add("10/10/2024, 09:00");
//        availableSlotsList.add("10/10/2024, 10:00");
//        availableSlotsList.add("11/10/2024, 14:00");
//        availableSlotsList.add("12/10/2024, 16:00");

        slotsAdapter = new SlotsAdapter(availableSlotsList);
        recyclerViewSlots.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewSlots.setAdapter(slotsAdapter);

        fetchSlotsFromFirestore();

        // Date Picker
        datePickerButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            // Set the calendar to tomorrow
            calendar.add(Calendar.DAY_OF_YEAR, 1); // Add 1 day to today

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view1, year, month, dayOfMonth) -> {
                        selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        datePickerButton.setText(selectedDate);
                    },
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            // Set the minimum date for the DatePicker
            datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

            datePickerDialog.show();
        });


        // Time Picker
        timePickerButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                    (view12, hourOfDay, minute) -> {
                        selectedTime = hourOfDay + ":" + (minute < 10 ? "0" + minute : minute);
                        timePickerButton.setText(selectedTime);
                    },
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        });

        // Save Button Click
        saveButton.setOnClickListener(v -> {
            String zoomLink = zoomLinkEditText.getText().toString().trim();
            if (TextUtils.isEmpty(zoomLink) || !isValidZoomLink(zoomLink)) {
                Toast.makeText(getContext(), "Please enter a valid Zoom link.", Toast.LENGTH_SHORT).show();
            } else if (!selectedDate.isEmpty() && !selectedTime.isEmpty()) {
                if (currentUser != null) {
                    String userId = currentUser.getUid();

                    // Fetch nutritionist details from Firestore using user ID
                    firestore.collection("Users").document(userId)
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    String consultationId = firestore.collection("Consultation_slots").document().getId(); // Generate unique ID
                                    String date = selectedDate;
                                    String time = selectedTime;
                                    String clientName = documentSnapshot.getString("clientName");
                                    String nutritionistName = documentSnapshot.getString("username");
                                    String status = documentSnapshot.getString("status"); // Default status

                                    saveSlotToFirestore(consultationId, date, time, nutritionistName, clientName, status, zoomLink);
                                    Toast.makeText(getContext(), "Slot added: " + date + " " + time, Toast.LENGTH_SHORT).show();
                                    selectedDate = "";
                                    selectedTime = "";
                                    datePickerButton.setText("Select Date");
                                    timePickerButton.setText("Select Time");
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed to fetch nutritionist details", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Please select both date and time", Toast.LENGTH_SHORT).show();
            }
        });


        button_booking_history.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new BookingHistoryFragment())
                    .addToBackStack(null)  // Add to back stack to enable back navigation
                    .commit();
        });

        button_consultationSlot.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new ConsultationNFragment())
                    .addToBackStack(null)
                    .commit();
        });

        button_pendingConsultation.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new PendingConsultationsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private boolean isValidZoomLink(String link) {
        return Patterns.WEB_URL.matcher(link).matches() && link.contains("zoom.us");
    }

    private void fetchSlotsFromFirestore() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            // Get the logged-in user's ID
            String userId = currentUser.getUid();

            // Fetch the nutritionist's name for comparison
            firestore.collection("Users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String loggedInNutritionistName = documentSnapshot.getString("username");

                            // Query consultation slots where `nutritionistName` matches the logged-in user
                            firestore.collection("Consultation_slots")
                                    .whereEqualTo("nutritionistName", loggedInNutritionistName)
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                        availableSlotsList.clear();
                                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                            String date = document.getString("date");
                                            String time = document.getString("time");
                                            String nutritionistName = document.getString("nutritionistName");
                                            String zoomlink = document.getString("zoomLink");

                                            if (date != null && time != null && nutritionistName != null) {
                                                availableSlotsList.add(date + ", " + time + " - " + nutritionistName );
                                            }
                                        }
                                        slotsAdapter.notifyDataSetChanged();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Failed to fetch slots", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to fetch user details", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveSlotToFirestore(String consultationId, String date, String time, String nutritionistName, String status, String userName, String zoomLink) {
        Slot slot = new Slot(consultationId, date, time, nutritionistName, status, userName, zoomLink);

        // Save slot data to Firestore
        firestore.collection("Consultation_slots").add(slot)
                .addOnSuccessListener(documentReference -> {
                    availableSlotsList.add(date + ", " + time + " - " + nutritionistName + "\n" + zoomLink); // Display the date, time, and nutritionist in the list
                    slotsAdapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Slot added: " + date + ", " + time, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to save slot", Toast.LENGTH_SHORT).show();
                });
    }


    // RecyclerView Adapter for displaying the available slots
    private static class SlotsAdapter extends RecyclerView.Adapter<SlotsAdapter.SlotViewHolder> {

        private final List<String> availableSlots;

        public SlotsAdapter(List<String> availableSlots) {
            this.availableSlots = availableSlots;
        }

        @NonNull
        @Override
        public SlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new SlotViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SlotViewHolder holder, int position) {
            holder.textView.setText(availableSlots.get(position));
        }

        @Override
        public int getItemCount() {
            return availableSlots.size();
        }

        static class SlotViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public SlotViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}
