package com.fyp.dietandnutritionapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AddToFolderFragment extends Fragment {
    private Recipe recipe; // The recipe to be added
    private FirebaseFirestore db; // Firestore instance
    private static final String TAG = "AddToFolderFragment"; // Tag for logging

    // Constructor to initialize the fragment with a recipe
    public AddToFolderFragment(Recipe recipe) {
        this.recipe = recipe;
        this.db = FirebaseFirestore.getInstance();
    }

    // Method to show a dialog for folder name input
    public void showAddToFolderDialog(Context context) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(context, "Error: User is not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch existing folders for the user
        db.collection("RecipesFolders")
                .whereEqualTo("user_id", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Create a list of folder names
                        List<String> folderNames = new ArrayList<>();
                        for (DocumentSnapshot doc : task.getResult()) {
                            String folderName = doc.getString("folderName");
                            if (folderName != null) {
                                folderNames.add(folderName);
                            }
                        }

                        // Convert folder names list to array
                        String[] folderArray = folderNames.toArray(new String[0]);

                        // Variable to track the selected folder index
                        final int[] selectedIndex = {-1};

                        // Create an AlertDialog to select an existing folder or add a new one
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Select Folder");

                        // Display folder names in a list
                        builder.setSingleChoiceItems(folderArray, -1, (dialog, which) -> {
                            // User selects a folder, update the selectedIndex
                            selectedIndex[0] = which;
                        });

                        // Add a confirmation button to add the recipe to the selected folder
                        builder.setPositiveButton("Add", (dialog, which) -> {

                            if (selectedIndex[0] != -1) {
                                // A folder is selected, proceed with adding the recipe
                                String selectedFolder = folderArray[selectedIndex[0]];
                                addRecipeToFolder(selectedFolder, context);
                            } else {
                                // No folder is selected, show an error message
                                Toast.makeText(context, "Please select a folder.", Toast.LENGTH_SHORT).show();
                            }
                        });

                        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

                        // Create and show the dialog
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        // No folders found or error occurred
                        Toast.makeText(context, "No folders found. Please create one.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void addRecipeToFolder(String folderName, @NonNull Context context) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(context, "Error: User is not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (recipe == null) {
            Toast.makeText(context, "Error: Recipe is null.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Reference to the folder in Firestore
        DocumentReference folderRef = db.collection("RecipesFoldersStoring").document(folderName);

        // Get the existing folder document
        folderRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                RecipeFolder folder;

                if (document.exists()) {
                    // Folder exists, retrieve it
                    folder = document.toObject(RecipeFolder.class);
                    if (folder != null) {
                        folder.addRecipe(recipe); // Add the new recipe
                    }
                } else {
                    // Folder does not exist, create a new one
                    folder = new RecipeFolder(userId, folderName);
                    folder.addRecipe(recipe);
                }

                // Save the updated folder back to Firestore
                folderRef.set(folder)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "Recipe added to folder!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Error adding recipe to folder: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(context, "Error retrieving folder: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
