package com.example.dietandnutritionapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;


public class AccountsFragment extends Fragment {
    private ListView accountsListView;
    private ArrayList<Profile> accountArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.viewallaccounts, container, false);

        // Retrieve the data from arguments
        if (getArguments() != null) {
            accountArray = (ArrayList<Profile>) getArguments().getSerializable("accountArray");
        } else {
            accountArray = new ArrayList<>(); // Handle the case where no data is passed
        }

        // Initialize ListView
        accountsListView = view.findViewById(R.id.accountsListView);

        // Check if accountArray is not empty
        if (accountArray.isEmpty()) {
            // Optional: Show a message or handle empty state
            // For example, set an empty text view or a placeholder
        }

        // Create an adapter to display the account data
        ArrayAdapter<Profile> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_1,
                accountArray
        );

        accountsListView.setAdapter(adapter);

        return view;
    }
}