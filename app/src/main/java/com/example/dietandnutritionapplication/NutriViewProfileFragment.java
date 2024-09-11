package com.example.dietandnutritionapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class NutriViewProfileFragment extends Fragment {
    private TextView nameTextView, bioTextView;
    private ViewNutriProfileController viewController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nutri_view_profile, container, false);

        // Initialize the TextViews
        nameTextView = view.findViewById(R.id.nameTextView);
        bioTextView = view.findViewById(R.id.bioTextView);

        // Initialize controller and account
        NutriAccount nutriAccount = new NutriAccount();
        viewController = new ViewNutriProfileController(nutriAccount);

        // Load the profile data
        loadProfile();

        return view;
    }

    private void loadProfile() {
        // Retrieve email or other necessary data from arguments
        Bundle args = getArguments();
        if (args != null) {
            String email = args.getString("email");
            if (email != null) {
                Nutritionist nutritionist = viewController.viewProfile(email);
                if (nutritionist != null) {
                    nameTextView.setText(nutritionist.getFirstName());
                    bioTextView.setText(nutritionist.getBio());
                } else {
                    Toast.makeText(getContext(), "Profile not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "No email provided", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "No arguments provided", Toast.LENGTH_SHORT).show();
        }
    }
}
