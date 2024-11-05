package com.fyp.dietandnutritionapplication;

import android.app.AlertDialog;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PendingConsultationFragment extends Fragment {

    private RecyclerView bookingConsultationsRecyclerView;
    private ArrayList<Consultation> consultationList;
    private BookingConsultationsAdapter adapter;

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_npending_consultations, container, false);

        // Initialize buttons
        Button button_booking_history = view.findViewById(R.id.booking_history);
        Button button_consultationSlot = view.findViewById(R.id.consultation_slot);
        Button button_pendingConsultation = view.findViewById(R.id.pending_consultation);

        // Initialize RecyclerView
        bookingConsultationsRecyclerView = view.findViewById(R.id.booking_consultation_recycler_view);
        bookingConsultationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize empty list for consultations and set up adapter
        consultationList = new ArrayList<>();
        adapter = new BookingConsultationsAdapter(consultationList, this);
        bookingConsultationsRecyclerView.setAdapter(adapter);

        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();

        // Fetch consultation slots from Firestore
        fetchConsultationSlots();

        button_booking_history.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new PendingConsultationFragment())
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
                    .replace(R.id.frame_layout, new BookingConsultationsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void fetchConsultationSlots() {
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

                            // Query for consultations assigned to the logged-in nutritionist
                            db.collection("Consultation_slots")
                                    .whereEqualTo("nutritionistName", loggedInNutritionistName)
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                        consultationList.clear(); // Clear list before adding new items

                                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                            String id = document.getString("consultationId");
                                            String date = document.getString("date");
                                            String time = document.getString("time");
                                            String clientName = document.getString("clientName");
                                            String nutritionistName = document.getString("nutritionistName");
                                            String status = document.getString("status");
                                            String zoomLink = document.getString("zoomLink");

                                            if (date != null && time != null && clientName != null &&
                                                    !clientName.isEmpty() && !"completed".equals(status)) {

                                                consultationList.add(new Consultation(id, date, time, clientName, nutritionistName, status, zoomLink));
                                            }
                                        }

                                        // Notify the adapter that data has changed
                                        adapter.notifyDataSetChanged();

                                        // Notify user if there are no consultations
                                        if (consultationList.isEmpty()) {
                                            Toast.makeText(getContext(), "No pending consultations available.", Toast.LENGTH_SHORT).show();
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
        String consultationId;
        String date;
        String time;
        String clientName;
        String nutritionistName;
        String status;
        String zoomLink;

        public Consultation(String ConsultationId, String date, String time, String clientName, String nutritionistName,String status, String zoomLink) {
            this.consultationId = ConsultationId;
            this.date = date;
            this.time = time;
            this.nutritionistName = nutritionistName;
            this.clientName = clientName;
            this.status = status;
            this.zoomLink = zoomLink;
        }
    }

    // RecyclerView Adapter
    static class BookingConsultationsAdapter extends RecyclerView.Adapter<BookingConsultationsAdapter.ConsultationViewHolder> {
        private final List<Consultation> consultations;
        private final Fragment fragment;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        public BookingConsultationsAdapter(ArrayList<Consultation> consultations, Fragment fragment) {
            this.consultations = consultations;
            this.fragment = fragment;
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
            holder.idTextView.setText(consultation.consultationId);
            holder.dateTextView.setText(consultation.date);
            holder.timeTextView.setText(consultation.time);
            holder.clientNameTextView.setText(consultation.clientName);
            holder.nutritionistNameTextView.setText(consultation.nutritionistName);
            holder.statusTextView.setText(consultation.status);
            holder.zoomLinkTextView.setText(consultation.zoomLink);

            holder.itemView.setOnClickListener(v -> {
                RescheduleConsultationFragment editFragment = new RescheduleConsultationFragment();

                // Check if consultation.id is valid
                if (consultation.consultationId == null || consultation.consultationId.isEmpty()) {
                    Log.e("EditConsultationFragment", "Consultation ID is null or empty");
                    Toast.makeText(fragment.getContext(), "Invalid consultation ID", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Pass consultation ID as argument
                Bundle args = new Bundle();
                args.putString("consultationId", consultation.consultationId);
                editFragment.setArguments(args);

                // Start EditConsultationFragment
                if (fragment != null && fragment.isAdded()) {
                    fragment.requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout, editFragment)
                            .addToBackStack(null)
                            .commit();
                } else {
                    Log.e("EditConsultationFragment", "Parent fragment is not attached or is null");
                    Toast.makeText(fragment.getContext(), "Error navigating to edit screen", Toast.LENGTH_SHORT).show();
                }
            });

            holder.completeButton.setOnClickListener(v -> {
                // Show confirmation dialog
                new AlertDialog.Builder(fragment.getContext())
                        .setTitle("Complete Consultation")
                        .setMessage("Is the consultation with " + consultation.clientName + " complete?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Update status to "completed" in Firestore
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            CollectionReference consultationsRef = db.collection("Consultation_slots");

                            consultationsRef.get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
//                                    consultations.clear(); // Clear the list before adding new items

//                                    for (QueryDocumentSnapshot document : task.getResult()) {
//                                        // Get data from the document
//                                        String id = document.getString("consultationId");
//                                        String date = document.getString("date");
//                                        String time = document.getString("time");
//                                        String clientName = document.getString("clientName");
//                                        String status = document.getString("status");
//                                        String zoomLink = document.getString("zoomLink");
//
//                                        // Ensure that clientName is not null or empty
//                                        if (date != null && clientName != null && !clientName.isEmpty()) {
//                                            consultations.add(new PendingConsultationFragment.Consultation(id, date, time, clientName, status, zoomLink));
//                                            document.getReference().update("status", "complete")
//                                                    .addOnSuccessListener(aVoid -> Log.d("FirestoreUpdate", "Status updated to complete"))
//                                                    .addOnFailureListener(e -> Log.e("FirestoreError", "Failed to update status", e));
//
//                                        }
//                                    }
                                    updateConsultationStatus(consultation.consultationId);

                                    // Notify the adapter that data has changed
                                    notifyDataSetChanged();

                                    // Check if there are any pending consultations
                                    if (consultations.isEmpty()) {
                                        Toast.makeText(fragment.getContext(), "No pending consultations available.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // Handle the error
                                    Log.d("FirestoreError", "Error getting documents: ", task.getException());
                                }
                            });
                        })
                        .setNegativeButton("No", null)
                        .show();
            });


            holder.rescheduleButton.setOnClickListener(v -> {
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = auth.getCurrentUser();

                if (currentUser != null) {
                    String userId = currentUser.getUid();

                    // Fetch the current user's username
                    firestore.collection("Users").document(userId)
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    String currentUsername = documentSnapshot.getString("username");

                                    // Validate the consultationId
                                    if (consultation.consultationId != null && !consultation.consultationId.isEmpty()) {
                                        // Query the Consultation_slots collection to ensure the consultationId exists and matches
                                        firestore.collection("Consultation_slots")
                                                .whereEqualTo("consultationId", consultation.consultationId)
                                                .whereEqualTo("nutritionistName", currentUsername)
                                                .get()
                                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                                    if (!queryDocumentSnapshots.isEmpty()) {
                                                        // Proceed with the reschedule process

                                                        RescheduleConsultationFragment rescheduleFragment = new RescheduleConsultationFragment();

                                                        // Pass consultation ID to RescheduleConsultationFragment
                                                        Bundle args = new Bundle();
                                                        args.putString("consultationId", consultation.consultationId);
                                                        rescheduleFragment.setArguments(args);

                                                        // Start RescheduleConsultationFragment
                                                        fragment.requireActivity().getSupportFragmentManager().beginTransaction()
                                                                .replace(R.id.frame_layout, rescheduleFragment)
                                                                .addToBackStack(null)
                                                                .commit();
                                                    } else {
                                                        Log.e("RescheduleConsultation", "No consultation found with the specified ID and nutritionist name.");
                                                        Toast.makeText(fragment.getContext(), "Consultation not found or unauthorized access.", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("RescheduleConsultation", "Error retrieving consultation document", e);
                                                    Toast.makeText(fragment.getContext(), "Failed to verify consultation. Please try again.", Toast.LENGTH_SHORT).show();
                                                });
                                    } else {
                                        Log.e("RescheduleConsultation", "Consultation ID is null or empty");
                                        Toast.makeText(fragment.getContext(), "Invalid consultation ID", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.e("RescheduleConsultation", "User document does not exist.");
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e("RescheduleConsultation", "Error retrieving user document", e);
                                Toast.makeText(fragment.getContext(), "Failed to fetch user details", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Log.e("RescheduleConsultation", "No user is currently logged in.");
                    Toast.makeText(fragment.getContext(), "You must be logged in to reschedule a consultation.", Toast.LENGTH_SHORT).show();
                }
            });


        }

        private void updateConsultationStatus(String consultationId) {

            if (consultationId != null && !consultationId.isEmpty()) {
                firestore.collection("Consultation_slots")
                        .whereEqualTo("consultationId", consultationId)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                String documentId = queryDocumentSnapshots.getDocuments().get(0).getId();

                                // Update the document status to "completed"
                                firestore.collection("Consultation_slots").document(documentId)
                                        .update("status", "completed")
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("FirebaseUpdate", "Status updated successfully");

//                                            // Refresh the consultation list after marking as completed
                                            fetchConsultationSlots();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("FirebaseUpdate", "Error updating status", e);
                                            Toast.makeText(fragment.getContext(), "Failed to update status. Please try again.", Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                Log.e("FirebaseUpdate", "No document found with the specified consultationId.");
                            }
                        })
                        .addOnFailureListener(e -> Log.e("FirebaseUpdate", "Error retrieving document", e));
            } else {
                Log.e("FirebaseUpdate", "Consultation ID is null or empty");
            }
        }

        private void fetchConsultationSlots() {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();

            if (currentUser != null) {
                String userId = currentUser.getUid();

                // Get the logged-in nutritionist's username
                firestore.collection("Users").document(userId)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String loggedInNutritionistName = documentSnapshot.getString("username");

                                // Query for consultations assigned to the logged-in nutritionist
                                firestore.collection("Consultation_slots")
                                        .whereEqualTo("nutritionistName", loggedInNutritionistName)
                                        .get()
                                        .addOnSuccessListener(queryDocumentSnapshots -> {
                                            consultations.clear(); // Clear list before adding new items

                                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                                String id = document.getString("consultationId");
                                                String date = document.getString("date");
                                                String time = document.getString("time");
                                                String clientName = document.getString("clientName");
                                                String nutritionistName = document.getString("nutritionistName");
                                                String status = document.getString("status");
                                                String zoomLink = document.getString("zoomLink");

                                                if (date != null && time != null && clientName != null &&
                                                        !clientName.isEmpty() && !"completed".equals(status)) {

                                                    consultations.add(new Consultation(id, date, time, clientName, nutritionistName, status, zoomLink));
                                                }
                                            }

                                            // Notify the adapter that data has changed
                                            notifyDataSetChanged();

                                            // Notify user if there are no consultations
                                            if (consultations.isEmpty()) {
                                                Toast.makeText(fragment.getContext(), "No pending consultations available.", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(fragment.getContext(), "Failed to fetch consultations.", Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                Toast.makeText(fragment.getContext(), "Failed to retrieve nutritionist details.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(fragment.getContext(), "Failed to fetch user details", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(fragment.getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            }
        }




//        private void viewClientDetails(String clientName) {
//            // Create an instance of HealthReportFragment
//            healthReportFragment healthReportFragment = new healthReportFragment();
//
//            // Create a Bundle to pass the client name or other data
//            Bundle bundle = new Bundle();
//            bundle.putString("clientName", clientName); // Pass the client name
//            healthReportFragment.setArguments(bundle); // Set the arguments for the fragment
//
//            // Replace the current fragment with HealthReportFragment
//            fragment.requireActivity().getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.frame_layout, healthReportFragment) // Assuming R.id.frame_layout is your container ID
//                    .addToBackStack(null) // Optional: add to back stack
//                    .commit();
//        }


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
            TextView zoomLinkTextView;
            Button rescheduleButton;
            Button completeButton;

            public ConsultationViewHolder(@NonNull View itemView) {
                super(itemView);
                idTextView = itemView.findViewById(R.id.consultation_id);
                dateTextView = itemView.findViewById(R.id.consultation_date);
                timeTextView = itemView.findViewById(R.id.consultation_time);
                clientNameTextView = itemView.findViewById(R.id.consultation_client_name);
                nutritionistNameTextView = itemView.findViewById(R.id.consultation_nutritionistName);
                statusTextView = itemView.findViewById(R.id.consultation_status);
                zoomLinkTextView = itemView.findViewById(R.id.zoomLink);
                rescheduleButton = itemView.findViewById(R.id.reschedule);
                completeButton = itemView.findViewById(R.id.complete);
            }
        }
    }
}
