package com.fyp.dietandnutritionapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.util.Locale;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.SharedPreferences;
import android.content.Context;


public class ConsultationsFragment extends Fragment {

    private Consultation selectedConsultation;
    private ListView consultationListView;
    private ArrayList<Consultation> consultationList = new ArrayList<>();
    private ArrayList<Consultation> consultationList2 = new ArrayList<>();
    private FirebaseFirestore db;
    private ConsultationsFragmentAdapter_u_consult consultationAdapter;
    private ArrayList<Consultation> originalConsultations = new ArrayList<>(); // unfilter list
    private Button viewNutriButton, viewConsultationButton;
    ArrayList<Profile> nutriAccounts = new ArrayList<>();
    private ProfileAdapter adapter;
    private NutriProfileAdapter nutriProfileAdapter;
    private NutriAdapterTest nutriAdapterTest;
    ArrayList<Profile> originalProfiles = new ArrayList<>();
    private EditText searchBar;
    private String selectedRole = "All Users";    private TextView consultationIdTextView;
    private EditText clientNameEditText;
    private EditText statusEditText;
    private String searchText = "";

    public ConsultationsFragment() {

    }

    public static ConsultationsFragment newInstance(String param1, String param2) {
        ConsultationsFragment fragment = new ConsultationsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_u_book_consultation, container, false);

        // Initialize the ListView
        consultationListView = view.findViewById(R.id.available_slots_list_view);
        searchBar = view.findViewById(R.id.search_bar);
        viewNutriButton = view.findViewById(R.id.view_nutri);
        viewConsultationButton = view.findViewById(R.id.booking_consultation);

        db = FirebaseFirestore.getInstance();

//        Consultation consultation1 = new Consultation("123","John Wick","ahmoytan1956","123456","time","pending");
//        consultationList.add(consultation1);
        // Set up the ConsultationAdapter to bind data to the ListView
        if (getArguments() != null){
            selectedConsultation = (Consultation) getArguments().getSerializable("selectedConsultation");

            if (selectedConsultation != null) {
                consultationIdTextView.setText(selectedConsultation.getConsultationId());

                clientNameEditText.setText(selectedConsultation.getClientName());
                statusEditText.setText(selectedConsultation.getStatus());
            }
        }

        consultationAdapter = new ConsultationsFragmentAdapter_u_consult(requireContext(), consultationList);
        consultationListView.setAdapter(consultationAdapter);

        fetchConsultationsFromFirestore();

        String currentUserId = getCurrentUserId();

        ConsultationEntity consultationEntity = new ConsultationEntity();
        consultationEntity.retrieveCons(new ConsultationEntity.DataCallback() {
            @Override
            public void onSuccess(ArrayList<Consultation> consultationList) {
                if (!consultationList.isEmpty()) {
//                    // No consultations found for the user, show a toast message
//                    Toast.makeText(getContext(), "You haven't booked any consultation yet, our nutritionist is always there for you.", Toast.LENGTH_LONG).show();
//                } else {
                    // Success, consultations found, update the list
                    Toast.makeText(getContext(), "Success to load your consultations.", Toast.LENGTH_SHORT).show();
                    consultationList.clear();
                    consultationList.addAll(consultationList);
                    consultationAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed to load accounts.", Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch consultations from Firebase Firestore
//        ConsultationController consultationController = new ConsultationController();
//        consultationController.retrieveConsultations(new ConsultationEntity.DataCallback() {
//            @Override
//            public void onSuccess(ArrayList<Consultation> consultations) {
//                consultationList.clear(); // Clear the current list
//                consultationList.addAll(consultations); // Add the fetched consultations
//                originalConsultations.clear();
//                originalConsultations.addAll(consultations);
//                consultationAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onFailure(Exception e) {
//                Toast.makeText(getContext(), "Failed to load consultations.", Toast.LENGTH_SHORT).show();
//            }
//        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchText = s.toString(); // Update search text
                filterProfiles(); // Apply combined filter
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Handle consultation item clicks
//        consultationListView.setOnItemClickListener((parent, view1, position, id) -> {
//            Consultation selectedConsultation = consultationList.get(position);
//
//            // Create a new instance of ViewConsultationDetailsFragment and pass the selected consultation
//            ViewConsultationDetailsFragment consultationDetailsFragment = new ViewConsultationDetailsFragment();
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("selectedConsultation", selectedConsultation);
//            consultationDetailsFragment.setArguments(bundle);
//
//            // Replace the current fragment with ViewConsultationDetailsFragment
//            getActivity().getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.frame_layout, consultationDetailsFragment)
//                    .addToBackStack(null) // Add to back stack so you can navigate back
//                    .commit();
//        });

        viewNutriButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new ConsultationsUFragment())
                    .addToBackStack(null)
                    .commit();
        });

        viewConsultationButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new ConsultationsFragment())
                    .addToBackStack(null)
                    .commit();
        });


        return view;
    }

    private void filterProfiles() {
        ArrayList<Profile> filteredProfiles = new ArrayList<>();

        Log.d("SearchDebug", "Searching for: " + searchText);

        if (originalProfiles.isEmpty()) {
            Log.d("SearchDebug", "No original profiles available to filter.");
            return; // No profiles to filter
        }

        for (Profile profile : originalProfiles) {
            boolean matchesRole = selectedRole.equals("All Users") ||
                    (profile instanceof Admin && selectedRole.equals("Admin")) ||
                    (profile instanceof Nutritionist && selectedRole.equals("Nutritionist")) ||
                    (profile instanceof User && selectedRole.equals("User"));

            boolean matchesName = searchText.isEmpty() || (
                    (profile instanceof Admin && ((Admin) profile).getUsername().toLowerCase().contains(searchText.toLowerCase())) ||
                            (profile instanceof Nutritionist && ((Nutritionist) profile).getFullName().toLowerCase().contains(searchText.toLowerCase())) ||
                            (profile instanceof User && ((User) profile).getUsername().toLowerCase().contains(searchText.toLowerCase()))
            );

            // Add to filtered list only if it matches both the role and the name
            if (matchesRole && matchesName) {
                filteredProfiles.add(profile);
            }
        }

        nutriAccounts.clear();
        nutriAccounts.addAll(filteredProfiles);
        nutriProfileAdapter.notifyDataSetChanged(); // Refresh the adapter

        Log.d("SearchDebug", "Filtered profiles count: " + filteredProfiles.size());
    }


    private String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();  // Get the logged-in user's unique ID
        }
        return null;  // If no user is logged in
    }

    private void filterConsultations() {
        ArrayList<Consultation> filteredConsultations = new ArrayList<>();

        for (Consultation consultation : originalConsultations) {
            // Filter consultations based on search text (matching only nutritionist names)
            boolean matchesSearch = searchText.isEmpty() ||
                    consultation.getNutritionistName().toLowerCase().contains(searchText.toLowerCase());

            if (matchesSearch) {
                filteredConsultations.add(consultation);
            }
        }

        consultationList.clear();
        consultationList.addAll(filteredConsultations);
        consultationAdapter.notifyDataSetChanged(); // Refresh the adapter
    }

    public void fetchConsultationsFromFirestore() {
        // Clear the existing list to avoid duplication
        consultationList.clear();

        // Get the current user's information
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Retrieve the current user's username
            db.collection("Users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String currentUsername = documentSnapshot.getString("username");

                            // Fetch consultations from Firestore
                            db.collection("Consultation_slots")
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            // Loop through each document
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                // Retrieve consultation details from Firestore
                                                String id = document.getId();
                                                String nutritionistName = document.getString("nutritionistName");
                                                String clientName = document.getString("clientName");
                                                String date = document.getString("date");
                                                String time = document.getString("time");
                                                String status = document.getString("status");

                                                // Only add consultations where clientName matches the logged-in user's username
                                                if (clientName != null && clientName.equals(currentUsername)) {
                                                    Consultation consultation = new Consultation(id, nutritionistName, clientName, date, time, status, 150);
                                                    consultationList.add(consultation);

                                                    if (isOneDayAway(date) && !isReminderSent(id)) {
                                                        sendReminderNotification(userId, "Your consultation is scheduled for tomorrow at " + time);
                                                        markReminderAsSent(id);
                                                    }
                                                }
                                            }

                                            // Update ListView based on matching consultations
                                            if (consultationList.isEmpty()) {
                                                Toast.makeText(getContext(), "No consultations found for your account.", Toast.LENGTH_LONG).show();
                                                consultationListView.setVisibility(View.GONE); // Hide ListView if no consultations
                                            } else {
                                                consultationListView.setVisibility(View.VISIBLE); // Show ListView if consultations exist
                                                consultationAdapter.notifyDataSetChanged(); // Refresh the adapter
                                            }
                                        } else {
                                            Toast.makeText(getContext(), "Failed to fetch consultations: " + task.getException(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(getContext(), "User data not found.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error retrieving user information.", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private boolean isOneDayAway(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()); // Assuming date format is yyyy-MM-dd
            Date consultationDate = dateFormat.parse(dateString);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Date tomorrow = calendar.getTime();

            // Compare only the date part
            return dateFormat.format(tomorrow).equals(dateFormat.format(consultationDate));
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isReminderSent(String consultationId) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("SentReminders", Context.MODE_PRIVATE);
        return sharedPreferences.contains(consultationId);
    }

    private void markReminderAsSent(String consultationId) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("SentReminders", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(consultationId, true);
        editor.apply();
    }


    // Method to send notification
    private void sendReminderNotification(String userId, String message) {
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("userId", userId);
        notificationData.put("message", message);
        notificationData.put("type", "Consultation Reminder");
        notificationData.put("isRead", false);
        notificationData.put("timestamp", new Timestamp(System.currentTimeMillis()));

        db.collection("Notifications")
                .add(notificationData) // Use add() to create a new document
                .addOnSuccessListener(documentReference -> {
                    String notificationId = documentReference.getId(); // Get the document ID

                    // Now update the document to include the ID as a field
                    db.collection("Notifications").document(notificationId)
                            .update("notificationId", notificationId) // Store the document ID
                            .addOnSuccessListener(aVoid -> {
                                Log.d("Notification", "Notification added with ID: " + notificationId);
                            })
                            .addOnFailureListener(e -> {
                                Log.w("Notification", "Error updating notification ID", e);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("Notification", "Failed to send notification: " + e.getMessage());
                });
    }


}
