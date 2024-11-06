package com.fyp.dietandnutritionapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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

interface OnFolderDeleteListener {
    void onFolderDelete(navCreateFolderFragment.Folder folder);
}

public class navCreateFolderFragment extends Fragment {

    private FirebaseFirestore db;
    private RecyclerView folderRecyclerView;
    private FolderAdapter folderAdapter;
    private List<Folder> folderList;
    private List<Folder> originalFolderList; // For search functionality
    private EditText searchBar; // Search bar

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private TextView notificationBadgeTextView;
    private NotificationUController notificationUController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nav_create_recipes_folder, container, false);

        // Initialize Firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        folderRecyclerView = view.findViewById(R.id.folder_recycler_view);
        folderRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize lists and adapter
        folderList = new ArrayList<>();
        originalFolderList = new ArrayList<>();
        folderAdapter = new FolderAdapter(folderList, this::confirmAndDeleteFolder);
        folderRecyclerView.setAdapter(folderAdapter);

        // Initialize and setup search bar
        searchBar = view.findViewById(R.id.search_bar);
        setupSearchBar();

        // Check user login status
        if (currentUser == null) {
            Toast.makeText(requireContext(), "User not logged in. Cannot create folders.", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Setup notification handling
        setupNotifications(view);

        // Setup notification icon click
        ImageView notiImage = view.findViewById(R.id.noti_icon);
        notiImage.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NotificationUFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Initialize folder creation button
        ImageButton buttonCreateRecipes = view.findViewById(R.id.button_folder);
        buttonCreateRecipes.setOnClickListener(v -> showCreateFolderDialog());

        // Fetch folders
        fetchFoldersFromFirebase();

        return view;
    }

    private void setupSearchBar() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterFolders(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupNotifications(View view) {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            notificationBadgeTextView = view.findViewById(R.id.notificationBadgeTextView);
            notificationUController = new NotificationUController();

            notificationUController.fetchNotifications(userId, notifications -> {
                notificationUController.countNotifications(userId, count -> {
                    if (count > 0) {
                        notificationBadgeTextView.setText(String.valueOf(count));
                        notificationBadgeTextView.setVisibility(View.VISIBLE);
                    } else {
                        notificationBadgeTextView.setVisibility(View.GONE);
                    }
                });
            });
        }
    }

    private void filterFolders(String searchText) {
        folderList.clear();

        if (searchText.isEmpty()) {
            folderList.addAll(originalFolderList);
        } else {
            String searchLower = searchText.toLowerCase();
            for (Folder folder : originalFolderList) {
                if (folder.getFolderName().toLowerCase().contains(searchLower)) {
                    folderList.add(folder);
                }
            }
        }

        folderAdapter.notifyDataSetChanged();
    }

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

    private void fetchFoldersFromFirebase() {
        String userId = currentUser.getUid();

        // Clear both lists before fetching
        folderList.clear();
        originalFolderList.clear();

        // Add default folders
        List<Folder> defaultFolders = getDefaultFolders();
        folderList.addAll(defaultFolders);
        originalFolderList.addAll(defaultFolders);

        // Fetch user-created folders
        db.collection("RecipesFolders")
                .whereEqualTo("user_id", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String folderName = document.getString("folderName");
                            Folder folder = new Folder(folderName, true);
                            folderList.add(folder);
                            originalFolderList.add(folder);
                        }
                        folderAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(requireContext(), "Error fetching folders: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

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

    private void addFolderToFirebase(String recipesFolder) {
        Map<String, Object> folderData = new HashMap<>();
        folderData.put("folderName", recipesFolder);
        folderData.put("created_at", System.currentTimeMillis());
        folderData.put("user_id", currentUser.getUid());

        db.collection("RecipesFolders")
                .add(folderData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(requireContext(), "Folder '" + recipesFolder + "' added.", Toast.LENGTH_SHORT).show();
                    Folder newFolder = new Folder(recipesFolder, true);
                    folderList.add(newFolder);
                    originalFolderList.add(newFolder);
                    folderAdapter.notifyItemInserted(folderList.size() - 1);

                    // Clear search bar after adding new folder
                    searchBar.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error adding folder: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

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
                                        originalFolderList.removeIf(folder -> folder.getFolderName().equals(folderName));
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

    // Adapter class
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

            holder.folderName.setOnClickListener(v -> {
                if (folder.getTargetFragment() != null) {
                    Fragment fragment = null;
                    try {
                        fragment = (Fragment) folder.getTargetFragment().newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (fragment != null && getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).replaceFragment(fragment);
                    }
                } else {
                    Bundle args = new Bundle();
                    args.putString("folder_name", folder.getFolderName());
                    NavUserFolderFragment userFolderFragment = new NavUserFolderFragment();
                    userFolderFragment.setArguments(args);
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).replaceFragment(userFolderFragment);
                    }
                }
            });

            holder.deleteButton.setVisibility(folder.isDeletable() ? View.VISIBLE : View.GONE);
            if (folder.isDeletable()) {
                holder.deleteButton.setOnClickListener(v -> deleteListener.onFolderDelete(folder));
            }
        }

        @Override
        public int getItemCount() {
            return folderList.size();
        }

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

    // Folder class
    public static class Folder {
        private final String folderName;
        private final boolean deletable;
        private Class<?> targetFragment;

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