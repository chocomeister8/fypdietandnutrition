package com.fyp.dietandnutritionapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PendingConsultationsFragment extends Fragment {

    private Button bookingHistoryButton, consultationSlotButton, pendingConsultationButton;
    private ListView pendingConsultationListView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pending_consultations, container, false);

        // Initialize UI components
        bookingHistoryButton = view.findViewById(R.id.booking_history);
        consultationSlotButton = view.findViewById(R.id.consultation_slot);
        pendingConsultationButton = view.findViewById(R.id.pending_consultation);
        pendingConsultationListView = view.findViewById(R.id.pending_consultation_recycler_view);

        bookingHistoryButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new BookingHistoryFragment())
                    .addToBackStack(null)  // Add to back stack to enable back navigation
                    .commit();
        });

        consultationSlotButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new ConsultationNFragment())
                    .addToBackStack(null)
                    .commit();
        });

        pendingConsultationButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new PendingConsultationsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Additional setup, such as fetching pending consultations data and populating the ListView
        setupPendingConsultations();

        return view;
    }

    private void setupPendingConsultations() {
        // Populate ListView with pending consultations (this requires an adapter)
        // Example: pendingConsultationListView.setAdapter(yourAdapter);
    }
}
