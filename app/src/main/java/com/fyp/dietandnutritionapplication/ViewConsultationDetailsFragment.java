package com.fyp.dietandnutritionapplication;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ViewConsultationDetailsFragment extends Fragment {
    private FirebaseFirestore db;
    private ArrayList<Consultation> consultationList2 = new ArrayList<>();
    private ConsultationAdapter consultationAdapter;
    private Profile selectedProfile;
    private Nutritionist selectedNutri;
    private ListView consultationListView;
    private ArrayList<Consultation> consultations;
    private String usernameNutri;
    private Consultation selectedConsultation;
    private Button bookConsultationButton;
    private TextView consultationIdTextView, nutritionistNameTextView, clientNameTextView, detailsTextView, dateTextView, timeTextView, statusTextView;

    public static ViewConsultationDetailsFragment newInstance(Profile profile) {
        ViewConsultationDetailsFragment fragment = new ViewConsultationDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("selectedProfile", profile); // Pass the Profile object
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedProfile = (Profile) getArguments().getSerializable("selectedProfile");
            if (selectedProfile instanceof Nutritionist) {
                selectedNutri = (Nutritionist) selectedProfile; // Cast to Nutritionist if applicable
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_consultation_details, container, false);

        if (getArguments() != null) {
            selectedProfile = (Profile) getArguments().getSerializable("selectedConsultation");
            if (selectedProfile instanceof Nutritionist) {
                selectedNutri= ((Nutritionist) selectedProfile);
            }
        }
        TextView fullNameTextView = view.findViewById(R.id.fullName);
        TextView emailTextView = view.findViewById(R.id.email);
        TextView educationTextView = view.findViewById(R.id.education);
        TextView bioTextView = view.findViewById(R.id.bio);
        TextView phonenumberTextView = view.findViewById(R.id.phoneNumber);
        TextView expertiseTextView = view.findViewById(R.id.expertise);

        if (selectedNutri != null) {
            fullNameTextView.setText("Full Name - " + selectedNutri.getFullName());
            emailTextView.setText("Email - " + selectedNutri.getEmail());
            educationTextView.setText("Education - " + selectedNutri.getEducation());
            bioTextView.setText("Bio - "+ selectedNutri.getBio());
            phonenumberTextView.setText("Phone - "+ selectedNutri.getPhoneNumber());
            expertiseTextView.setText("Expertise - " + selectedNutri.getExpertise());
        }
        else {
            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
        }

        consultationListView = view.findViewById(R.id.consultationListView);
        db = FirebaseFirestore.getInstance();

        consultationAdapter = new ConsultationAdapter(requireContext(), consultationList2);
        consultationListView.setAdapter(consultationAdapter);
        ConsultationEntity consultationEntity = new ConsultationEntity();
        consultationEntity.retrieveCons(new ConsultationEntity.DataCallback() {
            @Override
            public void onSuccess(ArrayList<Consultation> consultationList) {
                // Clear the current list
                consultationList2.clear();

                // Filter consultations based on the selected nutritionist's username
                for (Consultation consultation : consultationList) {
                    if (selectedNutri != null &&
                            consultation.getNutritionistName().equals(selectedNutri.getUsername()) &&
                            (consultation.getClientName() == null || consultation.getClientName().isEmpty())) {

                        consultationList2.add(consultation); // Add only consultations with null or empty clientName
                    }
                }

                // Notify the adapter that the data has changed
                consultationAdapter.notifyDataSetChanged();
                if (!consultationList2.isEmpty()) {
                    Toast.makeText(getContext(), "Consultations loaded successfully.", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getContext(), "No slots available yet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed to load consultations.", Toast.LENGTH_SHORT).show();
            }
        });



//        usernameNutri = selectedNutri.getUsername();

//        consultationListView = view.findViewById(R.id.consultationListView);
//        ConsultationAdapter adapter = new ConsultationAdapter(getContext(), consultations);
//        consultationListView.setAdapter(adapter);

        //this to consultationAdapter(consultation_item layout)
        // Bind the dynamic TextViews from the XML layout
//        consultationIdTextView = view.findViewById(R.id.consultation_id);
//        nutritionistNameTextView = view.findViewById(R.id.nutritionist_name);
//        clientNameTextView = view.findViewById(R.id.client_name);
//        detailsTextView = view.findViewById(R.id.details);
//        dateTextView = view.findViewById(R.id.date);
//        timeTextView = view.findViewById(R.id.time);
//        statusTextView = view.findViewById(R.id.status);

//        // Populate TextViews with the selected consultation details
//        if (selectedConsultation != null) {
//            consultationIdTextView.setText(String.valueOf(selectedConsultation.getId()));
//            nutritionistNameTextView.setText(selectedConsultation.getNutritionistName());
//            clientNameTextView.setText(selectedConsultation.getClientName());
//            detailsTextView.setText(selectedConsultation.getDetails());
//            dateTextView.setText(selectedConsultation.getDate());
//            timeTextView.setText(selectedConsultation.getTime());
//            statusTextView.setText(selectedConsultation.getStatus());
//        }

//        ConsultationController consultationController = new ConsultationController();
//        consultationController.retrieveConsultations(new ConsultationEntity.DataCallback() { // Use ConsultationController.DataCallback here
//
//            @Override
//            public void onSuccess(ArrayList<Consultation> consultationList) {
//                if (consultationList != null && !consultationList.isEmpty()) {
//                    // Fetch the first consultation from the list (you can change the index if needed)
//                    Consultation consultation = consultationList.get(0);
//
//                    // Populate TextViews with the consultation details
//                    consultationIdTextView.setText(String.valueOf(consultation.getId()));
//                    nutritionistNameTextView.setText(consultation.getNutritionistName());
//                    clientNameTextView.setText(consultation.getClientName());
//                    dateTextView.setText(consultation.getDate());
//                    timeTextView.setText(consultation.getTime());
//                    statusTextView.setText(consultation.getStatus());
//                } else {
//                    // Handle the case where the list is empty or null
//                    Log.e("Consultation", "No consultations found.");
//                }
//                consultations.clear();
//                consultations.addAll(consultationList);
////                adapter.notifyDataSetChanged(); // Refresh UI
//
//            }
//
//            @Override
//            public void onFailure(Exception e) {
//                Log.e("Firestore", "Failed to retrieve consultation details", e);
//            }
//        });



//        // Handle book consultation button click
//        bookConsultationButton = view.findViewById(R.id.book_button);
//        bookConsultationButton.setOnClickListener(v -> {
//            // Create an instance of the BookConsultationFragment
//            BookConsultationFragment bookConsultationFragment = new BookConsultationFragment();
//
//            // Create a bundle to pass data if needed
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("selectedConsultation", selectedConsultation);
//
//            // Set the arguments for the new fragment
//            bookConsultationFragment.setArguments(bundle);
//
//            // Replace the current fragment with BookConsultationFragment
//            getActivity().getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.frame_layout, bookConsultationFragment) // Use the correct container ID
//                    .addToBackStack(null) // Allow back navigation
//                    .commit();
//        });

        return view;
    }
}