package com.fyp.dietandnutritionapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ConsultationNFragment extends Fragment {

    private Button datePickerButton, timePickerButton, saveButton;
    private RecyclerView recyclerViewSlots;
    private List<String> availableSlotsList;
    private SlotsAdapter slotsAdapter;
    private String selectedDate = "", selectedTime = "";
    private FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consultation_slots, container, false);

        // Initialize buttons
        Button button_booking_history = view.findViewById(R.id.booking_history);
        Button button_consultationSlot = view.findViewById(R.id.consultation_slot);
        Button button_pendingConsultation = view.findViewById(R.id.pending_consultation);

        firestore = FirebaseFirestore.getInstance();

        // Initialize views
        datePickerButton = view.findViewById(R.id.date_picker_button);
        timePickerButton = view.findViewById(R.id.time_picker_button);
        saveButton = view.findViewById(R.id.save_button);
        recyclerViewSlots = view.findViewById(R.id.recycler_view_slots);

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
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view1, year, month, dayOfMonth) -> {
                        selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        datePickerButton.setText(selectedDate);
                    },
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
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
            if (!selectedDate.isEmpty() && !selectedTime.isEmpty()) {
                String consultationId = firestore.collection("Consultation_slots").document().getId(); // Generate unique ID
                String date = selectedDate;
                String time = selectedTime;
                String nutritionistName = "Dr. Jane Doe"; // Replace with dynamic value based on logged-in user
                String status = "Pending"; // Default status

                saveSlotToFirestore(consultationId, date, time, nutritionistName, status);
//                availableSlotsList.add(slot);
//                slotsAdapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Slot added: " + date + time, Toast.LENGTH_SHORT).show();
                selectedDate = "";
                selectedTime = "";
                datePickerButton.setText("Select Date");
                timePickerButton.setText("Select Time");
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
                    .replace(R.id.frame_layout, new BookingHistoryFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void fetchSlotsFromFirestore() {
        firestore.collection("Consultation_slots").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    availableSlotsList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String date = document.getString("date");
                        String time = document.getString("time");
                        String nutritionistName = document.getString("nutritionistName");
                        if (date != null && time != null && nutritionistName != null) {
                            availableSlotsList.add(date + ", " + time + " - " + nutritionistName);
                        }
                    }
                    slotsAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to fetch slots", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveSlotToFirestore(String consultationId, String date, String time, String nutritionistName, String status) {
        Slot slot = new Slot(consultationId, date, time, nutritionistName, status);

        // Save slot data to Firestore
        firestore.collection("Consultation_slots").add(slot)
                .addOnSuccessListener(documentReference -> {
                    availableSlotsList.add(date + ", " + time + " - " + nutritionistName); // Display the date, time, and nutritionist in the list
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
