package com.example.dietandnutritionapplication;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class viewAccountsFragment extends Fragment {
    ListView listView;
    ArrayList<Profile> profiles = new ArrayList<>();
    private Spinner roleSpinner;
    private ProfileAdapter adapter; // Ensure you have a ProfileAdapter to handle Profile objects

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

        // Initialize and set up the ProfileAdapter
        adapter = new ProfileAdapter(getContext(), profiles);
        listView.setAdapter(adapter);

        // Fetch accounts from UserManager
        UserAccountEntity userAccountEntity = new UserAccountEntity();
        userAccountEntity.fetchAccounts(new UserAccountEntity.DataCallback() {
            @Override
            public void onSuccess(ArrayList<Profile> accounts) {
                profiles.clear(); // Clear existing profiles
                profiles.addAll(accounts); // Add fetched profiles
                adapter.notifyDataSetChanged(); // Notify adapter of data changes
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
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

        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle filter logic here
                String selectedRole = sortRole.get(position);
                filterProfilesByRole(selectedRole);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle no selection
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "Item clicked: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void filterProfilesByRole(String role) {
        if (role.equals("All Users")) {
            adapter.notifyDataSetChanged();
        } else {
            ArrayList<Profile> filteredProfiles = new ArrayList<>();
            for (Profile profile : profiles) {
                if (profile instanceof Admin && role.equals("Admin")) {
                    filteredProfiles.add(profile);
                } else if (profile instanceof Nutritionist && role.equals("Nutritionist")) {
                    filteredProfiles.add(profile);
                } else if (profile instanceof User && role.equals("User")) {
                    filteredProfiles.add(profile);
                }
            }
            adapter.clear();
            adapter.addAll(filteredProfiles);
            adapter.notifyDataSetChanged();
        }
    }
}