package com.example.dietandnutritionapplication;

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

        // Set onClickListeners
        saveButton.setOnClickListener(v -> saveProfile());
        discardButton.setOnClickListener(v -> showDiscardConfirmationDialog());
    }

    private void saveProfile() {
        String firstName = nameEditText.getText().toString();
        String education = educationEditText.getText().toString();
        String contactInfo = contactInfoEditText.getText().toString();
        String expertise = expertiseEditText.getText().toString();
        String bio = bioEditText.getText().toString();

        // Convert profile image view to drawable (placeholder)
        BitmapDrawable drawable = (BitmapDrawable) profileImageView.getDrawable();
        Bitmap profilePicture = drawable.getBitmap();

        boolean success = updateProfileController.updateProfile(firstName, education, contactInfo, expertise, bio, profilePicture);

        if (success) {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            // Optionally navigate to another activity or clear fields
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
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
}
