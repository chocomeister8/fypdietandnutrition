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

    private String folderName;
    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipeList = new ArrayList<>();
    private List<Recipe> originalRecipeList = new ArrayList<>();
    private EditText searchEditText;
    private Spinner mealTypeSpinner;
    private Spinner dishTypeSpinner;

    private final String[] mealTypes = {"--Select Meal Type--", "Breakfast", "Lunch", "Dinner", "Snack", "Teatime"};
    private final String[] dishTypes = {"--Select Dish Type--", "Starter", "Main course", "Side dish", "Soup", "Condiments and sauces", "Desserts", "Drinks", "Salad"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_folder, container, false);

        // Retrieve folder name from arguments if provided
        if (getArguments() != null) {
            folderName = getArguments().getString("folder_name");
            TextView folderTitle = view.findViewById(R.id.folder_title);
            folderTitle.setText(folderName);
        }

        initializeViews(view);
        loadRecipes(); // Load recipes based on the folder
        restorePreviousState();
        setupButtonListeners(view);

        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recipe_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recipeAdapter = new RecipeAdapter(recipeList, this::openRecipeDetailFragment, false);
        recyclerView.setAdapter(recipeAdapter);

        mealTypeSpinner = view.findViewById(R.id.spinner_meal_type);
        dishTypeSpinner = view.findViewById(R.id.spinner_dish_type);
        setupSpinners();

        searchEditText = view.findViewById(R.id.search_recipe);
        setupSearchBar();
    }

//    private void loadRecipes() {
//        // Fetch the recipes from Firestore based on the folder name
//        RecipesEntity recipesEntity = new RecipesEntity();
//        recipesEntity.fetchRecipesFromFolder(folderName, new RecipesEntity.OnRecipesFetchedListener() {
//            @Override
//            public void onRecipesFetched(ArrayList<Recipe> fetchedRecipes) {
//                recipeList.clear(); // Clear the current recipe list
//                recipeList.addAll(fetchedRecipes); // Add fetched recipes
//                originalRecipeList = new ArrayList<>(recipeList); // Keep a copy of the original list
//                recipeAdapter.updateRecipeList(recipeList); // Notify the adapter to update
//            }
//        });
//    }

    private void loadRecipes() {
        NavUserFolderController controller = new NavUserFolderController(folderName);
        controller.checkFetchRecipesFolder(new NavUserFolderController.OnRecipesFetchedListener() {
            @Override
            public void onRecipesFetched(ArrayList<Recipe> fetchedRecipes) {
                recipeList.clear(); // Clear the current recipe list
                recipeList.addAll(fetchedRecipes); // Add fetched recipes
                originalRecipeList = new ArrayList<>(recipeList); // Keep a copy of the original list
                recipeAdapter.updateRecipeList(recipeList); // Notify the adapter to update
            }
        });
    }

    private void setupSpinners() {
        ArrayAdapter<String> mealTypeAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, mealTypes);
        mealTypeSpinner.setAdapter(mealTypeAdapter);

        ArrayAdapter<String> dishTypeAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, dishTypes);
        dishTypeSpinner.setAdapter(dishTypeAdapter);

        setupSpinnerListeners();
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

    private void filterRecipes() {
        String searchQuery = searchEditText.getText().toString().trim().toLowerCase();
        String selectedMealType = mealTypeSpinner.getSelectedItem().toString();
        String selectedDishType = dishTypeSpinner.getSelectedItem().toString();

        List<Recipe> filteredList = new ArrayList<>();

        for (Recipe recipe : originalRecipeList) {
            boolean matchesSearchQuery = recipe.getLabel().toLowerCase().contains(searchQuery);
            boolean matchesMealType = selectedMealType.equals("--Select Meal Type--") || recipe.getMealType().contains(selectedMealType);
            boolean matchesDishType = selectedDishType.equals("--Select Dish Type--") || recipe.getDishType().contains(selectedDishType);

            if (matchesSearchQuery && matchesMealType && matchesDishType) {
                filteredList.add(recipe);
            }
        }

        recipeAdapter.updateRecipeList(filteredList); // Update the adapter with the filtered list
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

        recipeAdapter.updateRecipeList(originalRecipeList); // Reset to original recipe list
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
        Fragment recipeDetailFragment = new RecipeDetailFragment();
        recipeDetailFragment.setArguments(bundle);
        navigateToFragment(recipeDetailFragment);
    }

    private void restorePreviousState() {
        // Add code to restore previous state if necessary
    }
}
