package com.example.dietandnutritionapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;

public class ConsultationsUFragment extends Fragment {

    private ListView nutritionistListView;
    private List<Nutritionist> nutritionistList;
//    private UserConsultationsController adapter;
    private FirebaseFirestore db;
    ArrayList<Profile> nutriAccounts = new ArrayList<>();
    private ProfileAdapter adapter;
    private NutriProfileAdapter nutriProfileAdapter;
    private NutriAdapterTest nutriAdapterTest;
    ArrayList<Profile> originalProfiles = new ArrayList<>();
    private EditText searchBar;
    private String selectedRole = "All Users";
    private String searchText = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_consultations_u, container, false);

        // Initialize the ListView
        nutritionistListView = view.findViewById(R.id.nutritionist_list_view);
        searchBar = view.findViewById(R.id.search_bar);


        // Initialize the list of nutritionists
        nutritionistList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();


        nutriProfileAdapter = new NutriProfileAdapter(getContext(),nutriAccounts);
        nutritionistListView.setAdapter(nutriProfileAdapter);

        ConsultationController consultationController = new ConsultationController();
        consultationController.retrieveNutri(new UserAccountEntity.DataCallback() {
            @Override
            public void onSuccess(ArrayList<Profile> accounts) {
                nutriAccounts.clear(); // Clear the current list
                nutriAccounts.addAll(accounts); // Add the fetched accounts
                originalProfiles.clear();
                originalProfiles.addAll(accounts);
                nutriProfileAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed to load accounts.", Toast.LENGTH_SHORT).show();
            }
        });
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

        return view;
    }
    private void filterProfiles() {
        ArrayList<Profile> filteredProfiles = new ArrayList<>();

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
    }
}
