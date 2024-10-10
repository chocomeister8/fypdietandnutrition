package com.example.dietandnutritionapplication;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ConsultationsFragment extends Fragment {

    private ListView consultationListView;
    private ArrayList<Consultation> consultationList = new ArrayList<>();
    private FirebaseFirestore db;
    private ConsultationAdapter consultationAdapter;
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

        consultationList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        // Set up the ConsultationAdapter to bind data to the ListView
        consultationAdapter = new ConsultationAdapter(requireContext(), consultationList);
        consultationListView.setAdapter(consultationAdapter);

        // Fetch consultations from Firebase Firestore
        ConsultationController consultationController = new ConsultationController();
        consultationController.retrieveConsultations(new ConsultationEntity.DataCallback() {
            @Override
            public void onSuccess(ArrayList<Consultation> consultations) {
                consultationList.clear(); // Clear the current list
                consultationList.addAll(consultations); // Add the fetched consultations
                originalConsultations.clear();
                originalConsultations.addAll(consultations);
                consultationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed to load consultations.", Toast.LENGTH_SHORT).show();
            }
        });

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
        consultationListView.setOnItemClickListener((parent, view1, position, id) -> {
            Consultation selectedConsultation = consultationList.get(position);

            // Create a new instance of ViewConsultationDetailsFragment and pass the selected consultation
            ViewConsultationDetailsFragment consultationDetailsFragment = new ViewConsultationDetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("selectedConsultation", selectedConsultation);
            consultationDetailsFragment.setArguments(bundle);

            // Replace the current fragment with ViewConsultationDetailsFragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, consultationDetailsFragment)
                    .addToBackStack(null) // Add to back stack so you can navigate back
                    .commit();
        });

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

}
