package com.example.dietandnutritionapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class NutriUpdateProfilePage extends AppCompatActivity {
    private EditText nameEditText, educationEditText, contactInfoEditText, expertiseEditText, bioEditText;
    private ImageView profileImageView;
    private Button saveButton, discardButton;
    private UpdateNutriProfileController updateProfileController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText);
        educationEditText = findViewById(R.id.educationEditText);
        contactInfoEditText = findViewById(R.id.contactInfoEditText);
        expertiseEditText = findViewById(R.id.expertiseEditText);
        bioEditText = findViewById(R.id.bioEditText);
        profileImageView = findViewById(R.id.profileImageView);
        saveButton = findViewById(R.id.saveButton);
        discardButton = findViewById(R.id.discardButton);

        // Initialize controller
        NutriAccount nutriAccount = new NutriAccount();
        updateProfileController = new UpdateNutriProfileController(nutriAccount);

        // Retrieve and set data if available
        Intent intent = getIntent();
        if (intent != null) {
            String currentName = intent.getStringExtra("currentName");
            String currentBio = intent.getStringExtra("currentBio");
            // Set these to EditText fields
            nameEditText.setText(currentName);
            bioEditText.setText(currentBio);
            // Handle other fields similarly
        }

        // Set onClickListeners
        saveButton.setOnClickListener(v -> saveProfile());
        discardButton.setOnClickListener(v -> showDiscardConfirmationDialog());
    }

    private void showDiscardConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Discard Changes")
                .setMessage("Are you sure you want to discard the changes you made?")
                .setPositiveButton("Discard", (dialog, which) -> {
                    // Optionally navigate back or clear fields
                    finish(); // Close the activity
                })
                .setNegativeButton("Cancel", null) // Do nothing
                //.setBackgroundDrawableResource(R.color.transparent_background) // Set background transparency
                .show();
    }


    private void saveProfile() {
        if (!validateInputs()) {
            return;
        }

        // Get the user's email from the intent (or wherever you store it)
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");

        String firstName = nameEditText.getText().toString();
        String education = educationEditText.getText().toString();
        String contactInfo = contactInfoEditText.getText().toString();
        String expertise = expertiseEditText.getText().toString();
        String bio = bioEditText.getText().toString();

        BitmapDrawable drawable = (BitmapDrawable) profileImageView.getDrawable();
        Bitmap profilePicture = drawable.getBitmap();

        try {
            boolean success = updateProfileController.updateProfile(email, firstName, education, contactInfo, expertise, profilePicture, bio);
            if (success) {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

                // Create an intent to return the updated profile data
                Intent resultIntent = new Intent();
                resultIntent.putExtra("updatedName", firstName);
                resultIntent.putExtra("updatedBio", bio);
                // Add other updated fields as needed
                setResult(RESULT_OK, resultIntent); // Set result code and data
                finish(); // Close this activity
            } else {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private boolean validateInputs() {
        if (nameEditText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        } else if (educationEditText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Education cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        } else if (expertiseEditText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Expertise cannot be empty", Toast.LENGTH_SHORT).show();
            return false;

        }


        return true;
    }
}

