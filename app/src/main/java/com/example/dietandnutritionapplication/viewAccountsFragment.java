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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link viewAccountsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class viewAccountsFragment extends Fragment {
    ListView listView;
    ArrayList<String> items;
    ArrayAdapter<String> adapter;
    ArrayList<Profile> profiles = new ArrayList<>();
    private Spinner roleSpinner;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public viewAccountsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment viewAccountsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static viewAccountsFragment newInstance(String param1, String param2) {
        viewAccountsFragment fragment = new viewAccountsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the items list
        items = new ArrayList<>();

        // Get the activity context and retrieve the list of profiles from MainActivity
        MainActivity mainActivity = (MainActivity) getActivity();
        profiles = mainActivity.getAccountArray();

        // Loop through profiles and convert them to string representations (if needed)
        for (Profile profile : profiles) {
            items.add(profile.getUsername() + " - " + profile.getRole());  // Or however you want to display profile data as a string
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_accounts, container, false);
        listView = view.findViewById(R.id.listView);

        // Get the profiles from MainActivity
        MainActivity mainActivity = (MainActivity) getActivity();
        profiles = mainActivity.getAccountArray();

        // Create and set the ProfileAdapter for the ListView
        ProfileAdapter adapter = new ProfileAdapter(getContext(), profiles);
        listView.setAdapter(adapter);

        roleSpinner = view.findViewById(R.id.filterRoleSpinner);

        List<String> sortRole = new ArrayList<>();
        sortRole.add("All Users");
        sortRole.add("Admin");
        sortRole.add("Nutritionist");
        sortRole.add("User");

        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, sortRole);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(sortAdapter);


        // Set the adapter to the ListView
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "Item clicked: " + parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}