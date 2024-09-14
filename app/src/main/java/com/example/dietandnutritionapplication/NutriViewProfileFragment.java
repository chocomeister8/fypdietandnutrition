package com.example.dietandnutritionapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class NutriViewProfileFragment extends Fragment {
    private TextView nameTextView, bioTextView;
    private ViewNutriProfileController viewController;
    private UpdateNutriProfileController updateController;
    private NutriAccount nutriAccount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nprofile, container, false);

        // Initialize the TextViews and Button
        nameTextView = view.findViewById(R.id.textView);
        bioTextView = view.findViewById(R.id.textView6);
        Button button_update = view.findViewById(R.id.button);

        // Initialize account and controllers
        nutriAccount = new NutriAccount();
        viewController = new ViewNutriProfileController(nutriAccount);
        updateController = new UpdateNutriProfileController(nutriAccount);

        // Load the profile data
        loadProfile();

        // Set OnClickListener for the update button
        // Inside NutriViewProfileFragment
        button_update.setOnClickListener(v -> {
            // Replace current fragment with NavProfileFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NutriUpdateProfilePage())
                    .addToBackStack(null)
                    .commit();
        });



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
