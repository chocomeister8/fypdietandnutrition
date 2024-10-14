package com.fyp.dietandnutritionapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class NavUserFolderFragment extends Fragment {

    // For User Folder Functionality
    private String folderName;

    // For All Recipes Functionality
    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipeList;
    private List<Recipe> originalRecipeList; // Keep a copy of the original recipe list
    private EditText searchEditText;
    private Spinner mealTypeSpinner;
    private Spinner dishTypeSpinner;

    // Define your meal types and dish types
    private final String[] mealTypes = {"--Select Meal Type--", "Breakfast", "Lunch", "Dinner", "Snack", "Teatime"};
    private final String[] dishTypes = {"--Select Dish Type--", "Starter", "Main course", "Side dish", "Soup", "Condiments and sauces", "Desserts", "Drinks", "Salad"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_folder, container, false);

        // Retrieve folder name from arguments if provided
        if (getArguments() != null) {
            folderName = getArguments().getString("folder_name");
            // Display the folder name in TextView (if needed)
            TextView folderTitle = view.findViewById(R.id.folder_title);
            folderTitle.setText(folderName);
        }

        // Initialize views for All Recipes functionality
        initializeViews(view);
        loadRecipes(); // Load recipes based on the folder
        restorePreviousState();

        // Set up button click listeners
        setupButtonListeners(view);

        return view;
    }

    private void initializeViews(View view) {
        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recipe_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the recipe list and adapter
        recipeList = new ArrayList<>();
        originalRecipeList = new ArrayList<>(); // Initialize the original list
        recipeAdapter = new RecipeAdapter(recipeList, this::openRecipeDetailFragment, false);
        recyclerView.setAdapter(recipeAdapter);

        // Initialize spinners
        mealTypeSpinner = view.findViewById(R.id.spinner_meal_type);
        dishTypeSpinner = view.findViewById(R.id.spinner_dish_type);
        setupSpinners(); // Call to setup spinners

        // Set up search bar
        searchEditText = view.findViewById(R.id.search_recipe);
        setupSearchBar();
    }

    private void setupSpinners() {
        // Set up meal type spinner
        ArrayAdapter<String> mealTypeAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, mealTypes);
        mealTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealTypeSpinner.setAdapter(mealTypeAdapter);

        // Set up dish type spinner
        ArrayAdapter<String> dishTypeAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, dishTypes);
        dishTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dishTypeSpinner.setAdapter(dishTypeAdapter);

        setupSpinnerListeners(); // Call to setup spinner listeners
    }

    private void setupSpinnerListeners() {
        mealTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterRecipes();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        dishTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterRecipes();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupSearchBar() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterRecipes();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void loadRecipes() {
        // Fetch the recipes from Firestore based on the folder name
        fetchRecipesFromDataSource(folderName);
    }

    private void fetchRecipesFromDataSource(String folder) {
        // Reference to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Fetch recipes from Firestore based on the folder name
        db.collection("recipes") // Replace with your actual collection name
                .whereEqualTo("folder", folder) // Filter by folder name
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        recipeList.clear(); // Clear the existing list
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Recipe recipe = document.toObject(Recipe.class); // Assuming Recipe class is set up for Firestore
                            recipeList.add(recipe); // Add recipe to the list
                        }
                        originalRecipeList = new ArrayList<>(recipeList); // Keep a copy of the original list
                        recipeAdapter.updateRecipeList(recipeList); // Update the adapter
                    } else {
                        Log.d("NavUserFolderFragment", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void restorePreviousState() {
        if (getArguments() != null) {
            String savedSearchQuery = getArguments().getString("search_query", "");
            int savedMealTypePos = getArguments().getInt("spinner1_value", 0);
            int savedDishTypePos = getArguments().getInt("spinner2_value", 0);

            // Restore the saved search query and spinner selections
            searchEditText.setText(savedSearchQuery);
            mealTypeSpinner.setSelection(savedMealTypePos);
            dishTypeSpinner.setSelection(savedDishTypePos);

            // Apply the filters with the restored values
            filterRecipes();
        }
    }

    private void filterRecipes() {
        String searchQuery = searchEditText.getText().toString().trim().toLowerCase();
        String selectedMealType = mealTypeSpinner.getSelectedItem().toString();
        String selectedDishType = dishTypeSpinner.getSelectedItem().toString();

        List<Recipe> filteredList = new ArrayList<>();

        for (Recipe recipe : originalRecipeList) {
            boolean matchesSearchQuery = recipe.getName().toLowerCase().contains(searchQuery);
            boolean matchesMealType = selectedMealType.equals("--Select Meal Type--") || recipe.getMealType().equals(selectedMealType);
            boolean matchesDishType = selectedDishType.equals("--Select Dish Type--") || recipe.getDishType().equals(selectedDishType);

            if (matchesSearchQuery && matchesMealType && matchesDishType) {
                filteredList.add(recipe);
            }
        }

        // Update the adapter with the filtered list
        recipeAdapter.updateRecipeList(filteredList);
    }

    private void setupButtonListeners(View view) {
        Button button_add_recipe = view.findViewById(R.id.add_recipe_button);
        Button clearFiltersButton = view.findViewById(R.id.clear_filters_button);

        button_add_recipe.setOnClickListener(v -> navigateToFragment(new AddRecipeFragment()));
        clearFiltersButton.setOnClickListener(v -> clearFilters());
    }

    private void clearFilters() {
        mealTypeSpinner.setSelection(0); // Reset meal type spinner
        dishTypeSpinner.setSelection(0); // Reset dish type spinner
        searchEditText.setText(""); // Clear search bar

        // Reset the recipe list to original and notify adapter
        recipeAdapter.updateRecipeList(originalRecipeList);
    }

    private void navigateToFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void openRecipeDetailFragment(Recipe recipe) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("selected_recipe", recipe);
        bundle.putString("source", "all");
        bundle.putString("folder_name", folderName);
        Fragment recipeDetailFragment = new RecipeDetailFragment();
        recipeDetailFragment.setArguments(bundle);
        navigateToFragment(recipeDetailFragment);
    }
}
