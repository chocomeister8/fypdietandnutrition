package com.fyp.dietandnutritionapplication;

import static androidx.fragment.app.FragmentManager.TAG;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NutriApprovedRecipesFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecipeAdapter recipesAdapter;
    private List<Recipe> recipeList;
    private EditText searchEditText;
    private Spinner mealTypeSpinner;
    private Spinner dishTypeSpinner;
    private FirebaseFirestore db;

    private final String[] mealTypes = {"--Select Meal Type--", "Breakfast", "Lunch", "Dinner", "Snack", "Teatime"};
    private final String[] dishTypes = {"--Select Dish Type--", "Starter", "Main course", "Side dish", "Soup", "Condiments and sauces", "Desserts", "Drinks", "Salad"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.nutri_approved_recipes_status, container, false);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize buttons using view.findViewById
        Button button_all_recipes = view.findViewById(R.id.button_all_recipes);
        Button button_pending_recipes = view.findViewById(R.id.button_recipes_status);
        Button button_approved_recipes = view.findViewById(R.id.button_approved);
        Button button_rejected_recipes = view.findViewById(R.id.button_rejected);

        searchEditText = view.findViewById(R.id.search_recipe);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recipe_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recipeList = new ArrayList<>();
        recipesAdapter = new RecipeAdapter(recipeList, null, true);
        recyclerView.setAdapter(recipesAdapter);

        mealTypeSpinner = view.findViewById(R.id.spinner_meal_type);
        dishTypeSpinner = view.findViewById(R.id.spinner_dish_type);
        setupSpinners(); // Call to setup spinners

        // Call the setup methods for listeners
        setupSpinnerListeners(); // Call to setup spinner listeners
        setupSearchBar(); // Call to setup search bar listeners

        // Fetch approved recipes from Firestore
        fetchApprovedRecipes();

        // Set up button click listeners to navigate between fragments
        button_all_recipes.setOnClickListener(v -> {
            // Replace current fragment with NutriAllRecipesFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NutriAllRecipesFragment())
                    .addToBackStack(null)
                    .commit();
        });

        button_pending_recipes.setOnClickListener(v -> {
            // Replace current fragment with NutriPendingRecipesFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NutriPendingRecipesFragment())
                    .addToBackStack(null)
                    .commit();
        });

        button_approved_recipes.setOnClickListener(v -> {
            // Refresh the current fragment to fetch approved recipes again
            fetchApprovedRecipes();
//            fetchRejectedRecipes();
        });

        button_rejected_recipes.setOnClickListener(v -> {
            // Replace current fragment with NutriPendingRecipesFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NutriRejectedRecipesFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
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
    }

    private void setupSpinnerListeners() {
        mealTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Call filterRecipes when meal type changes
                filterRecipes();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        dishTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Call filterRecipes when dish type changes
                filterRecipes();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupSearchBar() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing before text is changed
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Call the method to filter recipes based on search input
                filterRecipes();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Do nothing after text is changed
            }
        });
    }

    private void filterRecipes() {
        String searchQuery = searchEditText.getText().toString().trim();
        String selectedMealType = mealTypeSpinner.getSelectedItem() != null ? mealTypeSpinner.getSelectedItem().toString() : "--Select Meal Type--";
        String selectedDishType = dishTypeSpinner.getSelectedItem() != null ? dishTypeSpinner.getSelectedItem().toString() : "--Select Dish Type--";

        // Log the current selections
        Log.d("FilterRecipes", "Search Query: " + searchQuery + ", Meal Type: " + selectedMealType + ", Dish Type: " + selectedDishType);

        // TODO: Implement filtering logic based on search query, meal type, and dish type
    }

//    private void fetchApprovedRecipes() {
//
//        db.collection("Recipes")
//                .whereEqualTo("status", "Approved") // Filter to get only recipes with status "Pending"
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            recipeList.clear(); // Clear the list before adding new data
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Recipe recipe = document.toObject(Recipe.class);
//                                User user = document.toObject(User.class);
//                                recipe.setRecipe_id(document.getId());
//
//                                // Calculate calories per 100g if total weight is available
//                                double caloriesPer100g = recipe.getCaloriesPer100g();
//                                if (recipe.getTotalWeight() > 0) {
//                                    caloriesPer100g = (recipe.getCalories() / recipe.getTotalWeight()) * 100;
//                                }
//                                recipe.setCaloriesPer100g(caloriesPer100g); // Update recipe object
//
//                                recipeList.add(recipe); // Add the recipe to the list
//                            }
//                            // Notify the adapter of data changes
//                            recipesAdapter.notifyDataSetChanged();
//                        } else {
//                            Log.w(TAG, "Error getting documents.", task.getException());
//                        }
//                    }
//                });
//    }

    private void fetchApprovedRecipes() {
        NutriApprovedRecipesController approvedRecipesController = new NutriApprovedRecipesController();
        approvedRecipesController.fetchApprovedRecipes(new NutriApprovedRecipesController.RecipesFetchedCallback() {
            @Override
            public void onRecipesFetched(ArrayList<Recipe> fetchedRecipes) {
                recipeList.clear(); // Clear the existing list
                recipeList.addAll(fetchedRecipes); // Add fetched recipes to the list
                recipesAdapter.notifyDataSetChanged(); // Notify adapter of changes
            }
        });
    }


    private void clearFiltersAndFetchRandomRecipes() {
        // Reset the spinners to default selections
        mealTypeSpinner.setSelection(0); // Assuming the first position is the default
        dishTypeSpinner.setSelection(0); // Assuming the first position is the default

        // Clear the search bar
        searchEditText.setText(""); // This will clear the search bar

        // Optionally: Fetch random recipes or reset the recipe list
    }

}
