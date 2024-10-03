package com.example.dietandnutritionapplication;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ConsultationNFragment extends Fragment {

    private Button datePickerButton, timePickerButton, saveButton;
    private RecyclerView recyclerViewSlots;
    private List<String> availableSlotsList;
    private SlotsAdapter slotsAdapter;
    private String selectedDate = "", selectedTime = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consultation_slots, container, false);

        // Initialize buttons
        Button button_booking_history = view.findViewById(R.id.booking_history);
        Button button_consultationSlot = view.findViewById(R.id.consultation_slot);
        Button button_pendingConsultation = view.findViewById(R.id.pending_consultation);


        // Initialize views
        datePickerButton = view.findViewById(R.id.date_picker_button);
        timePickerButton = view.findViewById(R.id.time_picker_button);
        saveButton = view.findViewById(R.id.save_button);
        recyclerViewSlots = view.findViewById(R.id.recycler_view_slots);

        // Initialize list and adapter
        availableSlotsList = new ArrayList<>();

        // Add some hard-coded available time slots
        availableSlotsList.add("10/10/2024, 09:00");
        availableSlotsList.add("10/10/2024, 10:00");
        availableSlotsList.add("11/10/2024, 14:00");
        availableSlotsList.add("12/10/2024, 16:00");

        slotsAdapter = new SlotsAdapter(availableSlotsList);
        recyclerViewSlots.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewSlots.setAdapter(slotsAdapter);

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
                String slot = selectedDate + ", " + selectedTime;
                availableSlotsList.add(slot);
                slotsAdapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Slot added: " + slot, Toast.LENGTH_SHORT).show();
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