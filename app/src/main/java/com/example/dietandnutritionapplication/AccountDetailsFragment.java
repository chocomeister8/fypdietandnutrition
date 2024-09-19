package com.example.dietandnutritionapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AccountDetailsFragment extends Fragment {

    private Profile selectedProfile; // Declare selectedProfile at class level

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.view_account_details, container, false);

        // Retrieve the profile from the arguments
        if (getArguments() != null) {
            selectedProfile = (Profile) getArguments().getSerializable("selectedProfile");
        }

        // Find your TextView or other UI elements to display profile details
        TextView firstnameTextView = view.findViewById(R.id.firstName);
        TextView lastnameTextView = view.findViewById(R.id.lastName);
        TextView usernameTextView = view.findViewById(R.id.username);
        TextView phonenumberTextView = view.findViewById(R.id.phone);
        TextView genderTextView = view.findViewById(R.id.gender);
        TextView emailTextView = view.findViewById(R.id.email);
        TextView roleTextView = view.findViewById(R.id.role);
        TextView datejoinedTextView = view.findViewById(R.id.accountActiveSince);


        // Set the details in the UI
        if (selectedProfile != null) {
            firstnameTextView.setText(selectedProfile.getFirstName());
            lastnameTextView.setText(selectedProfile.getLastName());
            usernameTextView.setText(selectedProfile.getUsername());
            phonenumberTextView.setText(selectedProfile.getPhoneNumber());
            genderTextView.setText(selectedProfile.getGender());
            emailTextView.setText(selectedProfile.getEmail());
            roleTextView.setText(selectedProfile.getRole());
            datejoinedTextView.setText(selectedProfile.getRole());
        }

        return view;
    }


}