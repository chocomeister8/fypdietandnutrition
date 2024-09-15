package com.example.dietandnutritionapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class navCreateFolderFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.nav_create_recipes_folder, container, false);

        // Initialize buttons using view.findViewById
        Button button_all_recipes = view.findViewById(R.id.button_all_recipes);
        Button button_vegetarian = view.findViewById(R.id.button_vegetarian);
        Button button_favourite = view.findViewById(R.id.button_favourite);
        Button button_create_recipes = view.findViewById(R.id.button_create_recipes);

        // Set up button click listeners to navigate between fragments
        button_all_recipes.setOnClickListener(v -> {
            // Replace current fragment with NavAllRecipesFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NavAllRecipesFragment())
                    .addToBackStack(null)
                    .commit();
        });

        button_vegetarian.setOnClickListener(v -> {
            // Replace current fragment with NavVegetarianRecipesFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NavVegetarianRecipesFragment())
                    .addToBackStack(null)
                    .commit();
        });

        button_favourite.setOnClickListener(v -> {
            // Replace current fragment with NavFavouriteRecipesFragment
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
            // Create an EditText view for input
            final EditText folderNameInput = new EditText(requireContext());
            folderNameInput.setHint("Enter folder name");

            // Build the AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Please enter folder name");
            builder.setView(folderNameInput);

            // Set up the buttons for the dialog
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String folderName = folderNameInput.getText().toString().trim();
                    if (!folderName.isEmpty()) {
                        // Handle folder creation logic
                        Toast.makeText(requireContext(), "Folder '" + folderName + "' created.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Folder name cannot be empty!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            // Show the dialog
            builder.show();
        });

        return view;
    }

    // Method to handle long-click and show a delete confirmation dialog
    private void addLongClickListener(Button button, String folderName) {
        button.setOnLongClickListener(v -> {
            // Show confirmation dialog for deleting the folder
            new AlertDialog.Builder(requireContext())
                    .setTitle("Confirm Delete")
                    .setMessage("Are you sure you want to delete the '" + folderName + "' folder?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Logic for deleting the folder (not implemented yet)
                            Toast.makeText(requireContext(), "Folder '" + folderName + "' deleted.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        });
    }
}
