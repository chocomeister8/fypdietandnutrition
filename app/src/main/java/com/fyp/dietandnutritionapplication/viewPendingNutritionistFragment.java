package com.fyp.dietandnutritionapplication;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class viewPendingNutritionistFragment extends Fragment {
    ListView listView;
    ArrayList<Profile> profiles = new ArrayList<>();
    ArrayList<Profile> originalProfiles = new ArrayList<>(); // Keep the unfiltered original list
    private Spinner roleSpinner;
    private ProfileAdapter adapter; // Ensure you have a ProfileAdapter to handle Profile objects
    private EditText searchAdminEditText;
    private TextView noPendingNutritionistText;
    private String selectedRole = "All Users"; // To keep track of the selected role
    private String searchText = ""; // To keep track of the search text

    public viewPendingNutritionistFragment() {
        // Required empty public constructor
    }

    public static viewPendingNutritionistFragment newInstance(String param1, String param2) {
        viewPendingNutritionistFragment fragment = new viewPendingNutritionistFragment();
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
        View view = inflater.inflate(R.layout.fragment_view_pending_nutritionist, container, false);

        listView = view.findViewById(R.id.listView);
        searchAdminEditText = view.findViewById(R.id.searchNutriEditText);
        noPendingNutritionistText = view.findViewById(R.id.no_pending_nutritionist_text); // Add the TextView reference

        // Initialize and set up the ProfileAdapter
        adapter = new ProfileAdapter(getContext(), profiles);
        listView.setAdapter(adapter);

        ViewAccountsController viewAccountsController = new ViewAccountsController();
        viewAccountsController.retrievePendingNutritionist(new UserAccountEntity.DataCallback() {
            @Override
            public void onSuccess(ArrayList<Profile> accounts) {
                profiles.clear();
                profiles.addAll(accounts);
                originalProfiles.clear();
                originalProfiles.addAll(accounts);
                adapter.notifyDataSetChanged();

                if (profiles.isEmpty()) {
                    noPendingNutritionistText.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                } else {
                    noPendingNutritionistText.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed to load accounts.", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up Spinner for role filtering
        List<String> sortRole = new ArrayList<>();
        sortRole.add("All Users");
        sortRole.add("Admin");
        sortRole.add("Nutritionist");
        sortRole.add("User");

        // Update name filter when text changes in EditText
        searchAdminEditText.addTextChangedListener(new TextWatcher() {
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

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Profile selectedProfile = profiles.get(position); // Get the selected profile

            // Create a new instance of AccountFragment and pass the selected profile
            AccountDetailsFragment accountFragment = new AccountDetailsFragment();

            // Create a bundle to pass data
            Bundle bundle = new Bundle();
            bundle.putSerializable("selectedProfile", selectedProfile); // Pass the profile object (make sure Profile implements Serializable)
            accountFragment.setArguments(bundle);

            // Replace the current fragment with AccountFragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, accountFragment) // Make sure to replace with the correct container ID
                    .addToBackStack(null) // Add to back stack so you can navigate back
                    .commit();
        });

        return view;
    }

    // Combined method to filter by both role and name
    private void filterProfiles() {
        ArrayList<Profile> filteredProfiles = new ArrayList<>();

        for (Profile profile : originalProfiles) {

            boolean matchesName = searchText.isEmpty();

            // Add to filtered list only if it matches both the role and the name
            if (matchesName) {
                filteredProfiles.add(profile);
            }
        }

        profiles.clear();
        profiles.addAll(filteredProfiles);
        adapter.notifyDataSetChanged(); // Refresh the adapter
    }
}
