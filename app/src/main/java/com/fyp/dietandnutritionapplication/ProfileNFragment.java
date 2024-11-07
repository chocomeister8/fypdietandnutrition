package com.fyp.dietandnutritionapplication;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileNFragment extends Fragment {
    Nutritionist nutritionistProfile = new Nutritionist();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    String userId = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nprofile, container, false);
        Button button_update = view.findViewById(R.id.button);

        if (user != null) {
            userId = user.getUid();
            Log.d("Auth", "User ID: " + userId);
        } else {
            Log.d("Auth", "User is not signed in.");
        }

        // Declare all TextViews and EditTexts
        TextView profileNameTextView = view.findViewById(R.id.textView);
        TextView emailTextView = view.findViewById(R.id.textView3);
        TextView bioTextView = view.findViewById(R.id.nutriBio);
        TextView educationTextView = view.findViewById(R.id.textView2);
        TextView specificationTextView = view.findViewById(R.id.textView4);
        EditText usernameEditText = view.findViewById(R.id.username_data);
        EditText firstNameEditText = view.findViewById(R.id.first_name_data);
        EditText lastNameEditText = view.findViewById(R.id.last_name_data);
        EditText emailEditText = view.findViewById(R.id.email_data);
        EditText contactInfoEditText = view.findViewById(R.id.contact_data);
        EditText educationEditText = view.findViewById(R.id.education_data);
        EditText specificationEditText = view.findViewById(R.id.specification_data);
        EditText bioEditText = view.findViewById(R.id.bio_data);

        button_update.setOnClickListener(v -> {
            String updatedUsername = usernameEditText.getText().toString();
            String updatedFirstName = firstNameEditText.getText().toString();
            String updatedLastName = lastNameEditText.getText().toString();
            String updatedEmail = emailEditText.getText().toString();
            String updatedContactInfo = contactInfoEditText.getText().toString();
            String updatedEducation = educationEditText.getText().toString();
            String updatedSpecification = specificationEditText.getText().toString();
            String updatedBio = bioEditText.getText().toString();

            NutriProfileController nutriProfileController = new NutriProfileController();
            nutriProfileController.updateProfile(userId,updatedUsername,updatedFirstName,updatedLastName,updatedEmail,updatedContactInfo,updatedEducation,updatedSpecification,updatedBio,getContext());
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NutriHomeFragment()) // Replace with your home page fragment
                    .addToBackStack(null)  // Optional: add to back stack if you want to allow the user to go back
                    .commit();
        });



//        UserAccountEntity userAccountEntity = new UserAccountEntity();
//        userAccountEntity.retrieveNutritionistByUid(userId, new UserAccountEntity.NutritionistCallback() {
//            @Override
//            public void onSuccess(Nutritionist nutritionist) {
//                nutritionistProfile = nutritionist;
//                profileNameTextView.setText(nutritionistProfile.getFullName());
//                emailTextView.setText(nutritionistProfile.getEmail());
//                bioTextView.setText("Bio - " + nutritionistProfile.getBio());
//                specificationTextView.setText("Specification - "+ nutritionistProfile.getExpertise());
//                educationTextView.setText("Education - "+nutritionistProfile.getEducation());
//
//                usernameEditText.setText(nutritionistProfile.getUsername());
//                firstNameEditText.setText(nutritionistProfile.getFirstName());
//                lastNameEditText.setText(nutritionistProfile.getLastName());
//                emailEditText.setText(nutritionistProfile.getEmail());
//                contactInfoEditText.setText(nutritionistProfile.getContactInfo());
//                educationEditText.setText(nutritionistProfile.getEducation());
//                specificationEditText.setText(nutritionistProfile.getExpertise());
//                bioEditText.setText(nutritionistProfile.getBio());
//
//                Log.d("Firestore", "Nutritionist data retrieved: " + nutritionist.toString());
//            }
//
//            @Override
//            public void onFailure(Exception e) {
//                Log.e("Firestore", "Failed to load profile data", e);
//                Toast.makeText(getContext(), "Failed to load profile data", Toast.LENGTH_SHORT).show();
//            }
//        });
        NutriProfileController nutriProfileController = new NutriProfileController();
        nutriProfileController.getNutriProfile(userId, new UserAccountEntity.NutritionistCallback() {
            @Override
            public void onSuccess(Nutritionist nutritionist) {
                nutritionistProfile = nutritionist;
                profileNameTextView.setText(nutritionistProfile.getFullName());
                emailTextView.setText(nutritionistProfile.getEmail());
                bioTextView.setText("Bio - " + nutritionistProfile.getBio());
                specificationTextView.setText("Specification - "+ nutritionistProfile.getExpertise());
                educationTextView.setText("Education - "+nutritionistProfile.getEducation());

                usernameEditText.setText(nutritionistProfile.getUsername());
                firstNameEditText.setText(nutritionistProfile.getFirstName());
                lastNameEditText.setText(nutritionistProfile.getLastName());
                emailEditText.setText(nutritionistProfile.getEmail());
                contactInfoEditText.setText(nutritionistProfile.getContactInfo());
                educationEditText.setText(nutritionistProfile.getEducation());
                specificationEditText.setText(nutritionistProfile.getExpertise());
                bioEditText.setText(nutritionistProfile.getBio());
                Log.d("Firestore", "Nutritionist data retrieved: " + nutritionist.toString());
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Firestore", "Failed to load profile data", e);
                Toast.makeText(getContext(), "Failed to load profile data", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}