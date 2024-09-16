package com.example.dietandnutritionapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CreateNProfileActivity extends AppCompatActivity {
    private EditText nameEditText, educationEditText, contactInfoEditText, expertiseEditText, bioEditText;
    private ImageView profileImageView;
    private Button saveButton;
    private CreateNProfileController createProfileController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nutri_create_profile);

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText);
        educationEditText = findViewById(R.id.educationEditText);
        contactInfoEditText = findViewById(R.id.contactInfoEditText);
        expertiseEditText = findViewById(R.id.expertiseEditText);
        bioEditText = findViewById(R.id.bioEditText);
        profileImageView = findViewById(R.id.profileImageView);
        saveButton = findViewById(R.id.saveButton);

        // Initialize controller
        NutriAccount nutriAccount = new NutriAccount();
        createProfileController = new CreateNProfileController(nutriAccount);

        saveButton.setOnClickListener(v -> saveProfile());
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
        //Nutritionist nutritionist = new Nutritionist(firstName,username,name,phoneNumber,password,email,gender,role,dateJoined,education,contactInfo,expertise,bio,profilePicture)

        boolean success = createProfileController.createProfile(firstName, "username","last name","phone","password","email","gender","role","date",education,contactInfo,expertise,bio,"img");

        if (success) {
            Toast.makeText(this, "Profile created successfully", Toast.LENGTH_SHORT).show();

            // Navigate to the profile view activity
            Intent intent = new Intent(CreateNProfileActivity.this, NutriViewProfileFragment.class);
            // Optionally pass the created profile data
            intent.putExtra("profile_name", firstName);
            intent.putExtra("education", education);
            intent.putExtra("contact_info", contactInfo);
            intent.putExtra("expertise", expertise);
            intent.putExtra("bio", bio);
            // Start the profile activity
            startActivity(intent);

        } else {
            Toast.makeText(this, "Failed to create profile", Toast.LENGTH_SHORT).show();
        }
    }

}

