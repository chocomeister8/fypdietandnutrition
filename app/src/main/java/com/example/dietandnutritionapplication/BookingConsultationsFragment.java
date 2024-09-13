package com.example.dietandnutritionapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BookingConsultationsFragment extends Fragment {

    private RecyclerView bookingConsultationsRecyclerView;
    private List<Consultation> consultationList;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking_consultations, container, false);

        // Initialize RecyclerView
        bookingConsultationsRecyclerView = view.findViewById(R.id.booking_consultation_recycler_view);
        bookingConsultationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Hardcoded data for the prototype
        consultationList = new ArrayList<>();
        consultationList.add(new Consultation("6 Oct 2024 (Sun), 3:30 PM", "Ms. Tee Zhi Xi", "Confirmed"));
        consultationList.add(new Consultation("20 Nov 2024 (Sun), 7:30 PM", "Mr. Goh Xiao Ming", "Confirmed"));
        consultationList.add(new Consultation("17 Nov 2024 (Sun), 4:30 PM", "Mr. Alex", "Confirmed"));
        consultationList.add(new Consultation("8 Dec 2024 (Sun), 10:30 AM", "Ms. Vivian", "Confirmed"));

        // Set up adapter
        BookingConsultationsAdapter adapter = new BookingConsultationsAdapter(consultationList);
        bookingConsultationsRecyclerView.setAdapter(adapter);

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
    private static class BookingConsultationsAdapter extends RecyclerView.Adapter<BookingConsultationsAdapter.ConsultationViewHolder> {
        private final List<Consultation> consultations;

        public BookingConsultationsAdapter(List<Consultation> consultations) {
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
