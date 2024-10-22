package com.fyp.dietandnutritionapplication;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ViewConsultationDetailsFragment extends Fragment {
    private FirebaseFirestore db;
    private ArrayList<Consultation> consultationList2 = new ArrayList<>();
    private ConsultationAdapter consultationAdapter;
    private ListView consultationListView;
    private String usernameNutri;

    public static ViewConsultationDetailsFragment newInstance() {
        return new ViewConsultationDetailsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_consultation_details, container, false);

        // Initialize the ListView for consultations
        consultationListView = view.findViewById(R.id.consultationListView);

        // Set up the ConsultationAdapter
        consultationAdapter = new ConsultationAdapter(requireContext(), consultationList2);
        consultationListView.setAdapter(consultationAdapter);

        // Retrieve consultations
        retrieveConsultations();

        return view;
    }

    // Method to retrieve consultations from Firestore
    private void retrieveConsultations() {
        ConsultationEntity consultationEntity = new ConsultationEntity();
        consultationEntity.retrieveCons(new ConsultationEntity.DataCallback() {
            @Override
            public void onSuccess(ArrayList<Consultation> consultationList) {
                // Clear the current list
                consultationList2.clear();

                // Add consultations to the list
                consultationList2.addAll(consultationList);

                // Notify the adapter that the data has changed
                consultationAdapter.notifyDataSetChanged();

                Toast.makeText(getContext(), "Consultations loaded successfully.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed to load consultations.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
