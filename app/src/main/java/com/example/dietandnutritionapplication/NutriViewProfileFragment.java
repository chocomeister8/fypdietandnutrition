package com.example.dietandnutritionapplication;

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

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class NutriViewProfileFragment extends Fragment {
    private TextView nameTextView, bioTextView;
    private FirebaseFirestore db; // Firebase Firestore instance
    private String nutritionistEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nprofile, container, false);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize the TextViews and Button
        nameTextView = view.findViewById(R.id.textView);
        bioTextView = view.findViewById(R.id.textView6);
        Button button_update = view.findViewById(R.id.button);

        // Load the profile data
        loadProfile();

        // Set OnClickListener for the update button
        button_update.setOnClickListener(v -> {
            // Navigate to the update profile fragment
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
            nutritionistEmail = args.getString("email");  // The nutritionist's email
            if (nutritionistEmail != null) {
                // Fetch the nutritionist's data from Firebase Firestore using the email
                db.collection("nutritionists")
                        .whereEqualTo("email", nutritionistEmail)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                // Get the first document
                                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                                Nutritionist nutritionist = documentSnapshot.toObject(Nutritionist.class);
                                if (nutritionist != null) {
                                    // Populate the TextViews with the fetched data
                                    nameTextView.setText(nutritionist.getFirstName());
                                    bioTextView.setText(nutritionist.getBio());
                                }
                            } else {
                                Toast.makeText(getContext(), "Profile not found", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(getContext(), "No email provided", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "No arguments provided", Toast.LENGTH_SHORT).show();
        }
    }
}
