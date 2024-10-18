package com.fyp.dietandnutritionapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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

// Listener interface for folder deletion
interface OnFolderDeleteListener {
    void onFolderDelete(navCreateFolderFragment.Folder folder);
}

public class navCreateFolderFragment extends Fragment {

    private FirebaseFirestore db;
    private RecyclerView folderRecyclerView;
    private FolderAdapter folderAdapter;
    private List<Folder> folderList; // List of Folder objects

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
            return view;
        }

        // Initialize folder creation button
        ImageButton buttonCreateRecipes = view.findViewById(R.id.button_folder);
        buttonCreateRecipes.setOnClickListener(v -> showCreateFolderDialog());

        // Fetch both default and user folders
        fetchFoldersFromFirebase();

        return view;
    }

    // Show dialog to create a new folder
    private void showCreateFolderDialog() {
        final EditText folderNameInput = new EditText(requireContext());
        folderNameInput.setHint("Enter folder name");

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Please enter folder name")
                .setView(folderNameInput)
                .setPositiveButton("OK", (dialog, which) -> {
                    String recipesFolder = folderNameInput.getText().toString().trim();
                    if (!recipesFolder.isEmpty()) {
                        addFolderToFirebase(recipesFolder);
                    } else {
                        Toast.makeText(requireContext(), "Folder name cannot be empty!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .show();
    }

    // Fetch folders from Firestore
    private void fetchFoldersFromFirebase() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Fetch default folders (not deletable)
        List<Folder> defaultFolders = getDefaultFolders();
        folderList.addAll(defaultFolders);

        // Fetch user-created folders from Firebase
        db.collection("RecipesFolders")
                .whereEqualTo("user_id", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String folderName = document.getString("folderName");
                            folderList.add(new Folder(folderName, true)); // User folders are deletable
                        }
                        folderAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(requireContext(), "Error fetching folders: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Create a list of default folders
    private List<Folder> getDefaultFolders() {
        List<Folder> defaultFolders = new ArrayList<>();
        defaultFolders.add(new Folder("All Recipes", false, NavAllRecipesFragment.class));
        defaultFolders.add(new Folder("For You", false, NavRecommendedRecipesFragment.class));
        defaultFolders.add(new Folder("Vegetarian", false, NavVegetarianRecipesFragment.class));
        defaultFolders.add(new Folder("Favourite Recipes", false, ViewFavouriteRecipesFragment.class));
        defaultFolders.add(new Folder("Community Recipes", false, NavCommunityRecipesFragment.class));
        defaultFolders.add(new Folder("Pending Recipes", false, NavPendingRecipesFragment.class));

        return defaultFolders;
    }

    // Add a new folder to Firestore
    private void addFolderToFirebase(String recipesFolder) {
        Map<String, Object> folderData = new HashMap<>();
        folderData.put("folderName", recipesFolder);
        folderData.put("created_at", System.currentTimeMillis());
        folderData.put("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid());

        db.collection("RecipesFolders")
                .add(folderData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(requireContext(), "Folder '" + recipesFolder + "' added.", Toast.LENGTH_SHORT).show();
                    folderList.add(new Folder(recipesFolder, true)); // New folder is deletable
                    folderAdapter.notifyItemInserted(folderList.size() - 1);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error adding folder: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Confirm and delete a folder
    private void confirmAndDeleteFolder(Folder folder) {
        if (!folder.isDeletable()) {
            Toast.makeText(requireContext(), "This folder cannot be deleted.", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Folder")
                .setMessage("Are you sure you want to delete the folder '" + folder.getFolderName() + "'?")
                .setPositiveButton("Yes", (dialog, which) -> deleteFolderFromFirebase(folder.getFolderName()))
                .setNegativeButton("No", null)
                .show();
    }

    // Delete a folder from Firestore
    private void deleteFolderFromFirebase(String folderName) {
        db.collection("RecipesFolders")
                .whereEqualTo("folderName", folderName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("RecipesFolders").document(document.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(requireContext(), "Folder '" + folderName + "' deleted.", Toast.LENGTH_SHORT).show();
                                        folderList.removeIf(folder -> folder.getFolderName().equals(folderName));
                                        folderAdapter.notifyDataSetChanged();
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

    // Adapter class to handle folder display and deletion
    private class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {
        private final List<Folder> folderList;
        private final OnFolderDeleteListener deleteListener;

        public FolderAdapter(List<Folder> folderList, OnFolderDeleteListener deleteListener) {
            this.folderList = folderList;
            this.deleteListener = deleteListener;
        }

        @NonNull
        @Override
        public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder, parent, false);
            return new FolderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
            Folder folder = folderList.get(position);
            holder.folderName.setText(folder.getFolderName());

            // Set click listener to the folder name
            holder.folderName.setOnClickListener(v -> {
                // Navigate to predefined fragment for default folders
                if (folder.getTargetFragment() != null) {
                    Fragment fragment = null;
                    try {
                        fragment = (Fragment) folder.getTargetFragment().newInstance(); // Create an instance of the target fragment
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (fragment != null) {
                        // Replace the current fragment with the target fragment
                        ((MainActivity) v.getContext()).replaceFragment(fragment); // Assuming MainActivity has replaceFragment method
                    }
                } else {
                    // Handle user-created folders
                    Bundle args = new Bundle();
                    args.putString("folder_name", folder.getFolderName()); // Pass folder name as argument

                    // Navigate to a generic fragment for user-created folders
                    NavUserFolderFragment userFolderFragment = new NavUserFolderFragment();
                    userFolderFragment.setArguments(args); // Pass the folder name to the fragment

                    ((MainActivity) v.getContext()).replaceFragment(userFolderFragment); // Replace with the generic fragment
                }
            });

            // Handle visibility of delete button based on folder type
            holder.deleteButton.setVisibility(folder.isDeletable() ? View.VISIBLE : View.GONE);
            if (folder.isDeletable()) {
                holder.deleteButton.setOnClickListener(v -> deleteListener.onFolderDelete(folder));
            }
        }



        @Override
        public int getItemCount() {
            return folderList.size();
        }

        // ViewHolder class to hold folder views
        class FolderViewHolder extends RecyclerView.ViewHolder {
            TextView folderName;
            Button deleteButton;

            public FolderViewHolder(@NonNull View itemView) {
                super(itemView);
                folderName = itemView.findViewById(R.id.folder_button);
                deleteButton = itemView.findViewById(R.id.delete_button);
            }
        }
    }

    // Inner class to represent Folder object
    public static class Folder {
        private final String folderName;
        private final boolean deletable; // Indicates if the folder can be deleted
        private Class<?> targetFragment; // Target fragment class for navigation

        public Folder(String folderName, boolean deletable) {
            this.folderName = folderName;
            this.deletable = deletable;
        }

        public Folder(String folderName, boolean deletable, Class<?> targetFragment) {
            this.folderName = folderName;
            this.deletable = deletable;
            this.targetFragment = targetFragment;
        }

        public String getFolderName() {
            return folderName;
        }

        public boolean isDeletable() {
            return deletable;
        }

        public Class<?> getTargetFragment() {
            return targetFragment;
        }
    }
}

