package com.fyp.dietandnutritionapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AccountDetailsFragment extends Fragment {

    private Profile selectedProfile; // Declare selectedProfile at class level
    private SuspendUserController suspendUserController;
    private ReactivateUserController reactivateUserController; // Declare an instance for ReactivateUserController


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.view_account_details, container, false);
        suspendUserController = new SuspendUserController();
        reactivateUserController = new ReactivateUserController(); // Initialize the controller


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
        Button suspendUserButton = view.findViewById(R.id.suspendUserButton);
        Button reactivateUserButton = view.findViewById(R.id.ReactivateUserButton);

        // Set the details in the UI
        if (selectedProfile != null) {
            firstnameTextView.setText(selectedProfile.getFirstName());
            lastnameTextView.setText(selectedProfile.getLastName());
            usernameTextView.setText(selectedProfile.getUsername());
            phonenumberTextView.setText(selectedProfile.getPhoneNumber());
            genderTextView.setText(selectedProfile.getGender());
            emailTextView.setText(selectedProfile.getEmail());
            roleTextView.setText(selectedProfile.getRole());
            datejoinedTextView.setText(selectedProfile.getDateJoined());

            String userStatus = selectedProfile.getStatus();
            Log.d("AccountDetailsFragment", "User status: " + userStatus); // Debug log

            Log.d("FirestoreData", "Fetched status: " + selectedProfile.getStatus());


            // Determine user status and set button visibility
            if ("active".equalsIgnoreCase(userStatus)) {
                suspendUserButton.setVisibility(View.VISIBLE);  // Show suspend button for active users
                reactivateUserButton.setVisibility(View.GONE);  // Hide reactivate button
            } else if ("deactivated".equalsIgnoreCase(userStatus)) {
                suspendUserButton.setVisibility(View.GONE);  // Hide suspend button for deactivated users
                reactivateUserButton.setVisibility(View.VISIBLE);  // Show reactivate button
            } else {
                // Handle unexpected status
                Log.d("AccountDetailsFragment", "Unexpected status: " + userStatus);
                suspendUserButton.setVisibility(View.GONE);  // Hide both buttons if status is unexpected
                reactivateUserButton.setVisibility(View.GONE);
            }
        }

        suspendUserButton.setOnClickListener(v -> {
            if (selectedProfile != null) {
                String usernameToSuspend = selectedProfile.getUsername();
                suspendUserController.suspendUser(usernameToSuspend); // Suspend user
                selectedProfile.setStatus("deactivated"); // Update the profile status
                Toast.makeText(getActivity(), "Suspended user: " + usernameToSuspend, Toast.LENGTH_SHORT).show();

                // Redirect to ViewAccountsFragment after suspension
                redirectToViewAccountsFragment();
            } else {
                Toast.makeText(getActivity(), "No user selected to suspend.", Toast.LENGTH_SHORT).show();
            }
        });

        reactivateUserButton.setOnClickListener(v -> {
            if (selectedProfile != null) {
                String usernameToReactivate = selectedProfile.getUsername();
                reactivateUserController.ReactivateUser(usernameToReactivate); // Reactivate user
                selectedProfile.setStatus("active"); // Update the profile status
                Toast.makeText(getActivity(), "Reactivated user: " + usernameToReactivate, Toast.LENGTH_SHORT).show();

                // Redirect to ViewAccountsFragment after reactivation
                redirectToViewAccountsFragment();
            } else {
                Toast.makeText(getActivity(), "No user selected to reactivate.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void redirectToViewAccountsFragment() {
        viewAccountsFragment viewAccountsFragment = new viewAccountsFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, viewAccountsFragment)  // Replace with your fragment container ID
                .addToBackStack(null)  // Optionally add to backstack
                .commit();
    }
}