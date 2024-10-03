package com.example.dietandnutritionapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PendingConsultationsFragment extends Fragment {

    private RecyclerView pendingConsultationsRecyclerView;
    private List<Consultation> consultationList;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_consultations, container, false);

        // Initialize buttons
        Button button_booking_history = view.findViewById(R.id.booking_history);
        Button button_consultationSlot = view.findViewById(R.id.consultation_slot);
        Button button_pendingConsultation = view.findViewById(R.id.pending_consultation);

        // Initialize RecyclerView
        pendingConsultationsRecyclerView = view.findViewById(R.id.pending_consultation_recycler_view);
        pendingConsultationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        // Set up adapter
        PendingConsultationsAdapter adapter = new PendingConsultationsAdapter(consultationList);
        pendingConsultationsRecyclerView.setAdapter(adapter);

        button_booking_history.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new BookingHistoryFragment())
                    .addToBackStack(null)
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

    // Consultation data model
    private static class Consultation {
        String dateTime;
        String clientName;
        String status;

        public Consultation(String dateTime, String clientName, String status) {
            this.dateTime = dateTime;
            this.clientName = clientName;
            this.status = status;
        }
    }

    // RecyclerView Adapter
    private static class PendingConsultationsAdapter extends RecyclerView.Adapter<PendingConsultationsAdapter.ConsultationViewHolder> {
        private final List<Consultation> consultations;

        public PendingConsultationsAdapter(List<Consultation> consultations) {
            this.consultations = consultations;
        }

        @NonNull
        @Override
        public ConsultationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_consultation, parent, false);
            return new ConsultationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ConsultationViewHolder holder, int position) {
            Consultation consultation = consultations.get(position);
            holder.dateTimeTextView.setText(consultation.dateTime);
            holder.clientNameTextView.setText(consultation.clientName);
            holder.statusTextView.setText(consultation.status);
        }

        @Override
        public int getItemCount() {
            return consultations.size();
        }

        static class ConsultationViewHolder extends RecyclerView.ViewHolder {
            TextView dateTimeTextView;
            TextView clientNameTextView;
            TextView statusTextView;

            public ConsultationViewHolder(@NonNull View itemView) {
                super(itemView);
                dateTimeTextView = itemView.findViewById(R.id.consultation_date_time);
                clientNameTextView = itemView.findViewById(R.id.consultation_client_name);
                statusTextView = itemView.findViewById(R.id.consultation_status);
            }
        }
    }
}