package com.example.dietandnutritionapplication;

import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Button; // Import Button
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private RecyclerView folderRecyclerView; // RecyclerView for displaying folders
    private FolderAdapter folderAdapter; // Adapter for the RecyclerView
    private List<String> folderList; // List to hold folder names

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.nav_create_recipes_folder, container, false);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        folderRecyclerView = view.findViewById(R.id.folder_recycler_view);
        folderRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize folder list and adapter
        folderList = new ArrayList<>();
        folderAdapter = new FolderAdapter(folderList, this::confirmAndDeleteFolder);
        folderRecyclerView.setAdapter(folderAdapter);

        // Check if the user is logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(requireContext(), "User not logged in. Cannot create folders.", Toast.LENGTH_SHORT).show();
            return view; // Exit if the user is not logged in
        }

        // Initialize buttons
        ImageButton button_create_recipes = view.findViewById(R.id.button_folder);

        // Set up folder creation functionality
        button_create_recipes.setOnClickListener(v -> showCreateFolderDialog());

        // Fetch existing folders from Firebase on initial load
        fetchFoldersFromFirebase();

        return view;
    }

    private void showCreateFolderDialog() {
        final EditText folderNameInput = new EditText(requireContext());
        folderNameInput.setHint("Enter folder name");

        // Build the AlertDialog to enter the folder name
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
    }

    private void fetchFoldersFromFirebase() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("RecipesFolders")
                .whereEqualTo("user_id", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String folderName = document.getString("folderName");
                            folderList.add(folderName); // Add folder to list
                        }
                        folderAdapter.notifyDataSetChanged(); // Notify adapter of data changes
                    } else {
                        Toast.makeText(requireContext(), "Error fetching folders: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addFolderToFirebase(String recipesFolder) {
        Map<String, Object> folderData = new HashMap<>();
        folderData.put("folderName", recipesFolder);
        folderData.put("created_at", System.currentTimeMillis());
        folderData.put("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid());

        db.collection("RecipesFolders")
                .add(folderData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(requireContext(), "Folder '" + recipesFolder + "' added to Firebase.", Toast.LENGTH_SHORT).show();
                    // Update UI with the new folder
                    folderList.add(recipesFolder); // Add folder to list
                    folderAdapter.notifyItemInserted(folderList.size() - 1); // Notify adapter
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error adding folder: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void confirmAndDeleteFolder(String recipesFolder) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Folder")
                .setMessage("Are you sure you want to delete the folder '" + recipesFolder + "'?")
                .setPositiveButton("Yes", (dialog, which) -> deleteFolderFromFirebase(recipesFolder))
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteFolderFromFirebase(String recipesFolder) {
        db.collection("RecipesFolders")
                .whereEqualTo("folderName", recipesFolder)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("RecipesFolders").document(document.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(requireContext(), "Folder '" + recipesFolder + "' deleted.", Toast.LENGTH_SHORT).show();
                                        // Remove folder from UI
                                        folderList.remove(recipesFolder); // Remove from list
                                        folderAdapter.notifyDataSetChanged(); // Notify adapter
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(requireContext(), "Error deleting folder: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(requireContext(), "Folder not found.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Inner class for FolderAdapter
    private class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {
        private List<String> folderList;
        private final OnFolderDeleteListener deleteListener;

        public FolderAdapter(List<String> folderList, OnFolderDeleteListener deleteListener) {
            this.folderList = folderList;
            this.deleteListener = deleteListener;
        }

        @NonNull
        @Override
        public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_folder, parent, false);
            return new FolderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
            String folderName = folderList.get(position);
            holder.folderName.setText(folderName);

            // Set click listener for the delete button
            holder.deleteButton.setOnClickListener(v -> {
                deleteListener.onFolderDelete(folderName); // Prompt for deletion confirmation
            });
        }

        @Override
        public int getItemCount() {
            return folderList.size();
        }

        public class FolderViewHolder extends RecyclerView.ViewHolder {
            TextView folderName;
            Button deleteButton; // Add the delete button

            public FolderViewHolder(View itemView) {
                super(itemView);
                folderName = itemView.findViewById(R.id.folder_name);
                deleteButton = itemView.findViewById(R.id.delete_button); // Initialize delete button
            }
        }
    }

    // Interface for delete listener
    public interface OnFolderDeleteListener {
        void onFolderDelete(String folderName);
    }
}
