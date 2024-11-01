package com.fyp.dietandnutritionapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

public class UserAccountDetailsToRecommendFragment extends Fragment {

    private Profile selectedProfile; // Declare selectedProfile at class level
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_account_details_to_recommend, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

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
        Button healthAdviceButton = view.findViewById(R.id.healthAdviceButton);

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
            if (selectedProfile instanceof User) {
                User user = (User) selectedProfile;
                dieteryPreference.setText(user.getDietaryPreference()); // Display dietary preference
                allergy.setText(user.getFoodAllergies()); // Display allergies
            }

            String userStatus = selectedProfile.getStatus();
            Log.d("AccountDetailsFragment", "User status: " + userStatus); // Debug log
            Log.d("FirestoreData", "Fetched status: " + selectedProfile.getStatus());
        }

        recommendButton.setOnClickListener(v -> navigateToFragment(new BrowsetoRecommendRecipesFragment()));
        healthAdviceButton.setOnClickListener(v -> showHealthAdviceDialog());

        return view;
    }

    private void navigateToFragment(Fragment fragment) {
        UsernamePass.userName = selectedProfile.getUsername();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void showHealthAdviceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Leave a Health Advice");

        // Set up the input field
        final EditText input = new EditText(requireContext());
        input.setHint("Enter your health advice here");
        builder.setView(input);

        // Set up the submit button
        builder.setPositiveButton("Submit", (dialog, which) -> {
            String comment = input.getText().toString().trim();
            if (!comment.isEmpty()) {
                saveCommentToFirestore(comment);
            } else {
                Toast.makeText(getContext(), "Comment cannot be empty!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void saveCommentToFirestore(String comment) {
        // Check if getUserId() is available and returns a non-null ID
        String userId = selectedProfile.getUserId();  // Assuming `getUserId()` returns the user's unique ID

        if (userId == null || userId.isEmpty()) {
            // Log an error if userId is null
            Log.e("UserAccountFragment", "Error: userId is null or empty. Using username as fallback.");
            userId = selectedProfile.getUsername();  // Use username if userId is not available

            if (userId == null || userId.isEmpty()) {
                Toast.makeText(getContext(), "Error: User ID and username are both null or empty.", Toast.LENGTH_SHORT).show();
                return;  // Exit the method if no valid identifier is found
            }
        }

        // Use the userId or fallback username as the document reference
        DocumentReference documentRef = db.collection("healthAdvice").document(userId);

        // Create an object to hold the comment
        HealthAdvice healthAdvice = new HealthAdvice(comment);

        // Set the comment in Firestore
        documentRef.set(healthAdvice)
                .addOnSuccessListener(aVoid -> {
                    Log.d("UserAccountFragment", "Comment successfully saved" );
                    Toast.makeText(getContext(), "Comment saved!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("UserAccountFragment", "Error saving comment", e);
                    Toast.makeText(getContext(), "Error saving comment.", Toast.LENGTH_SHORT).show();
                });
    }

    // Inner class for the HealthAdvice object
    public static class HealthAdvice {
        private String comment;

        public HealthAdvice() {}  // Firestore requires a no-argument constructor

        public HealthAdvice(String comment) {
            this.comment = comment;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }
}
