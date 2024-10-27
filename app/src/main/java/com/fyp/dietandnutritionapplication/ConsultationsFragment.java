package com.fyp.dietandnutritionapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ConsultationsFragment extends Fragment {

    private ListView consultationListView;
    private ArrayList<Consultation> consultationList = new ArrayList<>();
    private ArrayList<Consultation> consultationList2 = new ArrayList<>();
    private FirebaseFirestore db;
    private ConsultationsFragmentAdapter_u_consult consultationAdapter;
    private ArrayList<Consultation> originalConsultations = new ArrayList<>(); // unfilter list
    private Button viewNutriButton, viewConsultationButton;
    private EditText searchBar;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        consultationAdapter = new ConsultationsFragmentAdapter_u_consult(requireContext(), consultationList2);
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
                    consultationList2.clear();
                    consultationList2.addAll(consultationList);
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

        // Search functionality
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchText = s.toString();
                filterConsultations(); // Filter consultations based on the search text
            }

            @Override
            public void afterTextChanged(Editable s) {}
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
    // Method to add hardcoded consultations for testing
    private void fetchConsultationsFromFirestore() {
        // Clear the existing list to avoid duplication
        consultationList2.clear();

        db.collection("Consultation_slots")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Get the document ID and other fields
                            String id = document.getId(); // Get the document ID
                            String nutritionistName = document.getString("nutritionistName");
                            String clientName = document.getString("clientName");
                            String date = document.getString("date");
                            String time = document.getString("time");
                            String status = document.getString("status"); // Ensure this matches your Firestore structure

                            // Create a new Consultation object and add it to the list
                            Consultation consultation = new Consultation(id, nutritionistName, clientName, date, time, status, 150);
                            consultationList2.add(consultation);
                        }
                        // Notify the adapter that the data has changed
                        consultationAdapter.notifyDataSetChanged();
                    } else {
                        // Handle the error
                        Toast.makeText(getContext(), "Failed to fetch consultations: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
