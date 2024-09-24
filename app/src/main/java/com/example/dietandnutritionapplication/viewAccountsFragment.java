package com.example.dietandnutritionapplication;

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
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class viewAccountsFragment extends Fragment {
    ListView listView;
    ArrayList<Profile> profiles = new ArrayList<>();
    ArrayList<Profile> originalProfiles = new ArrayList<>(); // Keep the unfiltered original list
    private Spinner roleSpinner;
    private ProfileAdapter adapter; // Ensure you have a ProfileAdapter to handle Profile objects
    private EditText searchAdminEditText;
    private String selectedRole = "All Users"; // To keep track of the selected role
    private String searchText = ""; // To keep track of the search text

    public viewAccountsFragment() {
        // Required empty public constructor
    }

    public static viewAccountsFragment newInstance(String param1, String param2) {
        viewAccountsFragment fragment = new viewAccountsFragment();
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
        View view = inflater.inflate(R.layout.fragment_view_accounts, container, false);

        listView = view.findViewById(R.id.listView);
        roleSpinner = view.findViewById(R.id.filterRoleSpinner);
        searchAdminEditText = view.findViewById(R.id.searchAdminEditText);

        // Initialize and set up the ProfileAdapter
        adapter = new ProfileAdapter(getContext(), profiles);
        listView.setAdapter(adapter);

        // Fetch accounts from UserManager
//        UserAccountEntity userAccountEntity = new UserAccountEntity();
//        userAccountEntity.fetchAccounts(new UserAccountEntity.DataCallback() {
//            @Override
//            public void onSuccess(ArrayList<Profile> accounts) {
//                originalProfiles.clear(); // Clear the original profiles list
//                originalProfiles.addAll(accounts); // Store fetched profiles in the original list
//                profiles.clear(); // Clear the filtering list
//                profiles.addAll(accounts); // Store profiles for filtering
//                adapter.notifyDataSetChanged(); // Notify adapter of data changes
//            }
//
//            @Override
//            public void onFailure(Exception e) {
//                e.printStackTrace();
//                Toast.makeText(getContext(), "Failed to load accounts.", Toast.LENGTH_SHORT).show();
//            }
//        });
        ViewAccountsController viewAccountsController = new ViewAccountsController();
        viewAccountsController.retrieveAccounts(new UserAccountEntity.DataCallback() {
            @Override
            public void onSuccess(ArrayList<Profile> accounts) {
                profiles.clear(); // Clear the current list
                profiles.addAll(accounts); // Add the fetched accounts
                adapter.notifyDataSetChanged(); // Notify the adapter about the new data
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

        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, sortRole);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(sortAdapter);

        // Update role filter when spinner selection changes
        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRole = sortRole.get(position); // Update the selected role
                filterProfiles(); // Apply combined filter
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle no selection
            }
        });

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
            boolean matchesRole = selectedRole.equals("All Users") ||
                    (profile instanceof Admin && selectedRole.equals("Admin")) ||
                    (profile instanceof Nutritionist && selectedRole.equals("Nutritionist")) ||
                    (profile instanceof User && selectedRole.equals("User"));

            boolean matchesName = searchText.isEmpty() || (
                    (profile instanceof Admin && ((Admin) profile).getUsername().toLowerCase().contains(searchText)) ||
                            (profile instanceof Nutritionist && ((Nutritionist) profile).getUsername().toLowerCase().contains(searchText)) ||
                            (profile instanceof User && ((User) profile).getUsername().toLowerCase().contains(searchText))
            );

            // Add to filtered list only if it matches both the role and the name
            if (matchesRole && matchesName) {
                filteredProfiles.add(profile);
            }
        }

        profiles.clear();
        profiles.addAll(filteredProfiles);
        adapter.notifyDataSetChanged(); // Refresh the adapter
    }
}
