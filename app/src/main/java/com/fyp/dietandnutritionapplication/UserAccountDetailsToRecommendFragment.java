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

public class UserAccountDetailsToRecommendFragment extends Fragment {

    private Profile selectedProfile; // Declare selectedProfile at class level



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_account_details_to_recommend, container, false);

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
        TextView dieteryPreference = view.findViewById(R.id.DietaryPreferenceTextView);
        TextView allergy = view.findViewById(R.id.AllergyTextView);
        Button recommendButton = view.findViewById(R.id.recommendRecipeButton);
        Button backButton = view.findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // Set the details in the UI
        if (selectedProfile != null) {
            firstnameTextView.setText(selectedProfile.getFirstName());
            lastnameTextView.setText(selectedProfile.getLastName());
            usernameTextView.setText(selectedProfile.getUsername());
            phonenumberTextView.setText(selectedProfile.getPhoneNumber());
            genderTextView.setText(selectedProfile.getGender());
            emailTextView.setText(selectedProfile.getEmail());
//            roleTextView.setText(selectedProfile.getRole());
            if (selectedProfile instanceof User) {
                User user = (User) selectedProfile;
                String dietaryPreferenceString = user.getDietaryPreference();
                dieteryPreference.setText(dietaryPreferenceString); // Display in TextView
            }
            if (selectedProfile instanceof User) {
                User user = (User) selectedProfile;
                String allergyString = user.getFoodAllergies();
                allergy.setText(allergyString); // Display in TextView
            }

            String userStatus = selectedProfile.getStatus();
            Log.d("AccountDetailsFragment", "User status: " + userStatus); // Debug log

            Log.d("FirestoreData", "Fetched status: " + selectedProfile.getStatus());

        }

        recommendButton.setOnClickListener(v -> navigateToFragment(new BrowsetoRecommendRecipesFragment()));


        return view;
    }

    private void navigateToFragment(Fragment fragment) {
        UsernamePass.userName = selectedProfile.getUsername();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit();
    }
}