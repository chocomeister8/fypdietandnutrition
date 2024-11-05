package com.fyp.dietandnutritionapplication;

import android.os.Bundle;
import android.util.Log;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BookingConsultationsFragment extends Fragment {

    private RecyclerView pendingConsultationsRecyclerView;
    private ArrayList<Consultation> pendingConsultationList;
    private PendingConsultationsAdapter adapter;

    private FirebaseFirestore db;
    private String currentNutritionistName; // Add a variable to store the current nutritionist's name

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking_consultations, container, false);

        // Initialize buttons (if you need them for navigation)
        Button buttonBookingHistory = view.findViewById(R.id.booking_history);
        Button buttonConsultationSlot = view.findViewById(R.id.consultation_slot);
        Button button_pendingConsultation = view.findViewById(R.id.pending_consultation);

        // Initialize RecyclerView
        pendingConsultationsRecyclerView = view.findViewById(R.id.pending_consultation_recycler_view);
        pendingConsultationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize empty list for consultations and set up adapter
        pendingConsultationList = new ArrayList<>();
        adapter = new PendingConsultationsAdapter(pendingConsultationList);
        pendingConsultationsRecyclerView.setAdapter(adapter);

        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();

        // Retrieve the current nutritionist's name
        currentNutritionistName = "NutritionistName";

        // Fetch pending consultations from Firestore
        fetchPendingConsultations();

        // Button click listeners (example)
        buttonBookingHistory.setOnClickListener(v -> {
            // Handle navigation to Booking History Fragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new PendingConsultationFragment())
                    .addToBackStack(null)
                    .commit();
        });

        buttonConsultationSlot.setOnClickListener(v -> {
            // Handle navigation to Consultation Slot Fragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new ConsultationNFragment())
                    .addToBackStack(null)
                    .commit();
        });

        button_pendingConsultation.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new BookingConsultationsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void fetchPendingConsultations() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Get the logged-in nutritionist's username
            db.collection("Users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String loggedInNutritionistName = documentSnapshot.getString("username");

                            // Query for completed consultations assigned to the logged-in nutritionist
                            db.collection("Consultation_slots")
                                    .whereEqualTo("nutritionistName", loggedInNutritionistName)
                                    .whereEqualTo("status", "completed")
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                        pendingConsultationList.clear(); // Clear list before adding new items

                                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                            String id = document.getString("consultationId");
                                            String date = document.getString("date");
                                            String time = document.getString("time");
                                            String clientName = document.getString("clientName");
                                            String nutritionistName = document.getString("nutritionistName");
                                            String status = document.getString("status");

                                            if (date != null && clientName != null && !clientName.isEmpty()) {
                                                pendingConsultationList.add(new Consultation(id, date, time, clientName, nutritionistName, status));
                                            }
                                        }

                                        // Notify the adapter that data has changed
                                        adapter.notifyDataSetChanged();

                                        // Notify if there are no completed consultations
                                        if (pendingConsultationList.isEmpty()) {
                                            Toast.makeText(getContext(), "No completed consultations available.", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Failed to fetch consultations.", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(getContext(), "Failed to retrieve nutritionist details.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to fetch user details", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }


    // Consultation data model
    static class Consultation {
        String id;
        String date;
        String time;
        String clientName;
        String nutritionistName;
        String status;

        public Consultation(String id, String date, String time, String clientName, String nutritionistName, String status) {
            this.id = id;
            this.date = date;
            this.time = time;
            this.clientName = clientName;
            this.nutritionistName = nutritionistName;
            this.status = status;
        }
    }

    // RecyclerView Adapter
    static class PendingConsultationsAdapter extends RecyclerView.Adapter<PendingConsultationsAdapter.ConsultationViewHolder> {
        private final List<Consultation> consultations;

        public PendingConsultationsAdapter(ArrayList<Consultation> consultations) {
            this.consultations = consultations;
        }

        @NonNull
        @Override
        public ConsultationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_consultation_book, parent, false);
            return new ConsultationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ConsultationViewHolder holder, int position) {
            Consultation consultation = consultations.get(position);
            holder.idTextView.setText(consultation.id);
            holder.dateTextView.setText(consultation.date);
            holder.timeTextView.setText(consultation.time);
            holder.clientNameTextView.setText(consultation.clientName);
            holder.nutritionistNameTextView.setText(consultation.nutritionistName);
            holder.statusTextView.setText(consultation.status);
        }

        @Override
        public int getItemCount() {
            return consultations.size();
        }

        static class ConsultationViewHolder extends RecyclerView.ViewHolder {
            TextView idTextView;
            TextView dateTextView;
            TextView timeTextView;
            TextView clientNameTextView;
            TextView nutritionistNameTextView;
            TextView statusTextView;

            public ConsultationViewHolder(@NonNull View itemView) {
                super(itemView);
                idTextView = itemView.findViewById(R.id.consultation_id);
                dateTextView = itemView.findViewById(R.id.consultation_date);
                timeTextView = itemView.findViewById(R.id.consultation_time);
                clientNameTextView = itemView.findViewById(R.id.consultation_client_name);
                nutritionistNameTextView = itemView.findViewById(R.id.consultation_nutritionistName);
                statusTextView = itemView.findViewById(R.id.consultation_status);
            }
        }
    }
}
