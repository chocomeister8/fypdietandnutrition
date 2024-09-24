package com.example.dietandnutritionapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class navCreateFolderFragment extends Fragment {

    private FirebaseFirestore db;
    private LinearLayout folderContainer;  // Container to hold folder views

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.nav_create_recipes_folder, container, false);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Check if the user is logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(requireContext(), "User not logged in. Cannot create folders.", Toast.LENGTH_SHORT).show();
            return view; // Exit if the user is not logged in
        }

        // Initialize buttons and container using view.findViewById
        Button button_all_recipes = view.findViewById(R.id.button_all_recipes);
        Button button_vegetarian = view.findViewById(R.id.button_vegetarian);
        Button button_favourite = view.findViewById(R.id.button_favourite);
        Button button_create_recipes = view.findViewById(R.id.button_create_recipes);
        //folderContainer = view.findViewById(R.id.folder_container); // Assume this is a LinearLayout in your layout file

        // Set up button click listeners to navigate between fragments
        button_all_recipes.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NavAllRecipesFragment())
                    .addToBackStack(null)
                    .commit();
        });

        button_vegetarian.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NavVegetarianRecipesFragment())
                    .addToBackStack(null)
                    .commit();
        });

        button_favourite.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NavFavouriteRecipesFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Add long click listeners for "delete folder" confirmation on long press for each button (except 'Create Recipes')
        addLongClickListener(button_all_recipes, "All Recipes");
        addLongClickListener(button_vegetarian, "Vegetarian");
        addLongClickListener(button_favourite, "Favourite");

        // Set up the button to create a folder by popping up a dialog to enter the folder name
        button_create_recipes.setOnClickListener(v -> {
            final EditText folderNameInput = new EditText(requireContext());
            folderNameInput.setHint("Enter folder name");

            // Build the AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Please enter folder name");
            builder.setView(folderNameInput);

            builder.setPositiveButton("OK", (dialog, which) -> {
                String recipesFolder = folderNameInput.getText().toString().trim();
                if (!recipesFolder.isEmpty()) {
                    addFolderToFirebase(recipesFolder);
                } else {
                    Toast.makeText(requireContext(), "Folder name cannot be empty!", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder.show();
        });

//        // Initially fetch folders from Firebase
//        fetchFoldersFromFirebase();

        return view;
    }

    // Method to handle long-click and show a delete confirmation dialog
    private void addLongClickListener(Button button, String folderName) {
        button.setOnLongClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Confirm Delete")
                    .setMessage("Are you sure you want to delete the '" + folderName + "' folder?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteFolderFromFirebase(folderName))
                    .setNegativeButton("No", null)
                    .show();
            return true;
        });
    }

    // Method to add the created folder to Firebase Firestore
    private void addFolderToFirebase(String RecipesFolder) {
        Map<String, Object> recipesFolder = new HashMap<>();
        recipesFolder.put("folderName", RecipesFolder);  // Using the specified field name
        recipesFolder.put("created_at", System.currentTimeMillis());
        recipesFolder.put("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid());

        db.collection("RecipesFolders")  // Updated to match the new collection name
                .add(recipesFolder)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(requireContext(), "Folder '" + RecipesFolder + "' added to Firebase.", Toast.LENGTH_SHORT).show();
                    // Update UI with the new folder
                    addFolderToUI(RecipesFolder);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error adding folder: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Method to delete a folder from Firebase Firestore
    private void deleteFolderFromFirebase(String RecipesFolder) {
        db.collection("RecipesFolders")  // Updated to match the new collection name
                .whereEqualTo("folderName", RecipesFolder)  // Using the specified field name
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("RecipesFolders").document(document.getId())  // Updated to match the new collection name
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(requireContext(), "Folder '" + RecipesFolder + "' deleted from Firebase.", Toast.LENGTH_SHORT).show();
                                        // Remove folder from UI
                                        removeFolderFromUI(RecipesFolder);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(requireContext(), "Error deleting folder: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(requireContext(), "Folder '" + RecipesFolder + "' not found in Firebase.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

//    // Method to fetch and display folders from Firebase Firestore
//    private void fetchFoldersFromFirebase() {
//        db.collection("RecipesFolders")  // Updated to match the new collection name
//                .whereEqualTo("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid())
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        List<String> folderNames = new ArrayList<>();
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            String folderName = document.getString("folderName");  // Using the specified field name
//                            if (folderName != null) {
//                                folderNames.add(folderName);
//                            }
//                        }
//                        // Populate the UI with the fetched folder names
//                        for (String name : folderNames) {
//                            addFolderToUI(name);
//                        }
//                    } else {
//                        Toast.makeText(requireContext(), "Error retrieving folders: " + task.getException(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

    // Method to add a folder view to the UI
    private void addFolderToUI(String RecipesFolder) {
        Button folderButton = new Button(requireContext());
        folderButton.setText(RecipesFolder);
        folderButton.setOnLongClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Confirm Delete")
                    .setMessage("Are you sure you want to delete the '" + RecipesFolder + "' folder?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteFolderFromFirebase(RecipesFolder))
                    .setNegativeButton("No", null)
                    .show();
            return true;
        });
        folderContainer.addView(folderButton);
    }

    // Method to remove a folder view from the UI
    private void removeFolderFromUI(String RecipesFolder) {
        for (int i = 0; i < folderContainer.getChildCount(); i++) {
            View child = folderContainer.getChildAt(i);
            if (child instanceof Button && ((Button) child).getText().equals(RecipesFolder)) {
                folderContainer.removeViewAt(i);
                break;
            }
        }
    }
}
