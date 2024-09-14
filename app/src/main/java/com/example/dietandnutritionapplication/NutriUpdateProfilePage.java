package com.example.dietandnutritionapplication;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.Nullable;

public class NutriUpdateProfilePage extends Fragment {
    private EditText nameEditText, educationEditText, contactInfoEditText, expertiseEditText, bioEditText;
    private ImageView profileImageView;
    private Button saveButton, discardButton;
    private UpdateNutriProfileController updateProfileController;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.nutri_update_profile, container, false);

        // Initialize views
        nameEditText = view.findViewById(R.id.nameEditText);
        educationEditText = view.findViewById(R.id.educationEditText);
        contactInfoEditText = view.findViewById(R.id.contactInfoEditText);
        expertiseEditText = view.findViewById(R.id.expertiseEditText);
        bioEditText = view.findViewById(R.id.bioEditText);
        profileImageView = view.findViewById(R.id.profileImageView);
        saveButton = view.findViewById(R.id.saveButton);
        discardButton = view.findViewById(R.id.discardButton);

        // Initialize controller
        NutriAccount nutriAccount = new NutriAccount();
        updateProfileController = new UpdateNutriProfileController(nutriAccount);

        // Retrieve and set data if available
        Bundle arguments = getArguments();
        if (arguments != null) {
            String currentName = arguments.getString("currentName");
            String currentBio = arguments.getString("currentBio");
            // Set these to EditText fields
            nameEditText.setText(currentName);
            bioEditText.setText(currentBio);
            // Handle other fields similarly
        }

        // Set onClickListeners
        saveButton.setOnClickListener(v -> saveProfile());
        discardButton.setOnClickListener(v -> showDiscardConfirmationDialog());

        return view;
    }

    private void showDiscardConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Discard Changes")
                .setMessage("Are you sure you want to discard the changes you made?")
                .setPositiveButton("Discard", (dialog, which) -> {
                    // Optionally navigate back or clear fields
                    requireActivity().getSupportFragmentManager().popBackStack(); // Go back to previous fragment
                })
                .setNegativeButton("Cancel", null) // Do nothing
                //.setBackgroundDrawableResource(R.color.transparent_background) // Set background transparency
                .show();
    }

    private void saveProfile() {
        if (!validateInputs()) {
            return;
        }

        // Get the user's email from the arguments
        Bundle arguments = getArguments();
        String email = arguments != null ? arguments.getString("email") : "";

        String firstName = nameEditText.getText().toString();
        String education = educationEditText.getText().toString();
        String contactInfo = contactInfoEditText.getText().toString();
        String expertise = expertiseEditText.getText().toString();
        String bio = bioEditText.getText().toString();

        BitmapDrawable drawable = (BitmapDrawable) profileImageView.getDrawable();
        Bitmap profilePicture = drawable != null ? drawable.getBitmap() : null;

        try {
            boolean success = updateProfileController.updateProfile(email, firstName, education, contactInfo, expertise, profilePicture, bio);
            if (success) {
                Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();

                // Create a result intent to return updated profile data
                Bundle result = new Bundle();
                result.putString("updatedName", firstName);
                result.putString("updatedBio", bio);
                // Add other updated fields as needed
                getParentFragmentManager().setFragmentResult("profileUpdate", result); // Use FragmentResult API
                requireActivity().getSupportFragmentManager().popBackStack(); // Close this fragment
            } else {
                Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInputs() {
        if (nameEditText.getText().toString().trim().isEmpty()) {
            Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        } else if (educationEditText.getText().toString().trim().isEmpty()) {
            Toast.makeText(requireContext(), "Education cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        } else if (expertiseEditText.getText().toString().trim().isEmpty()) {
            Toast.makeText(requireContext(), "Expertise cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
