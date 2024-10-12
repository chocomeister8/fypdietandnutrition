package com.example.dietandnutritionapplication;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class NavUserFolderFragment extends Fragment {

    // For User Folder Functionality
    private String folderName;

    // For All Recipes Functionality
    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipeList;
    private EditText searchEditText;
    private Spinner mealTypeSpinner;
    private Spinner dishTypeSpinner;

    private final Random random = new Random();

    // Define your meal types and dish types
    private final String[] mealTypes = {"--Select Meal Type--", "Breakfast", "Lunch", "Dinner", "Snack", "Teatime"};
    private final String[] dishTypes = {"--Select Dish Type--", "Starter", "Main course", "Side dish", "Soup", "Condiments and sauces", "Desserts", "Drinks", "Salad"};

    private List<String> simpleFoodSearches = Arrays.asList(
            "chicken", "beef", "steak", "fish", "soup", "lamb", "pasta", "potato", "burger", "curry", "shrimp", "bacon", "fried", "grilled", "smoked", "salmon"
    );

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

        // Fetch recipes
        fetchRecipes(getRandomSimpleFoodSearch(), null, null);

        // Restore previous search and spinner selections if available
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
        String searchQuery = searchEditText.getText().toString().trim();
        String selectedMealType = mealTypeSpinner.getSelectedItem() != null ? mealTypeSpinner.getSelectedItem().toString() : "--Select Meal Type--";
        String selectedDishType = dishTypeSpinner.getSelectedItem() != null ? dishTypeSpinner.getSelectedItem().toString() : "--Select Dish Type--";

        Log.d("FilterRecipes", "Search Query: " + searchQuery + ", Meal Type: " + selectedMealType + ", Dish Type: " + selectedDishType);

        fetchRecipes(searchQuery, selectedMealType.equals("--Select Meal Type--") ? null : selectedMealType,
                selectedDishType.equals("--Select Dish Type--") ? null : selectedDishType);
    }

    private void setupButtonListeners(View view) {

        Button button_add_recipe = view.findViewById(R.id.add_recipe_button);
        Button clearFiltersButton = view.findViewById(R.id.clear_filters_button);


        button_add_recipe.setOnClickListener(v -> navigateToFragment(new AddRecipeFragment()));
        clearFiltersButton.setOnClickListener(v -> clearFiltersAndFetchRandomRecipes());
    }

    private void clearFiltersAndFetchRandomRecipes() {
        mealTypeSpinner.setSelection(0); // Reset meal type spinner
        dishTypeSpinner.setSelection(0); // Reset dish type spinner
        searchEditText.setText(""); // Clear search bar
        fetchRecipes(getRandomSimpleFoodSearch(), null, null); // Fetch random recipes
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
        bundle.putString("search_query", searchEditText.getText().toString());
        bundle.putInt("spinner1_value", mealTypeSpinner.getSelectedItemPosition());
        bundle.putInt("spinner2_value", dishTypeSpinner.getSelectedItemPosition());

        RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
        recipeDetailFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, recipeDetailFragment)
                .addToBackStack(null)
                .commit();
    }

    private void fetchRecipes(String query, String mealType, String dishType) {
        String app_id = "2c7710ea"; // Your Edamam API app ID
        String app_key = "97f5e9187c865600f74e2baa358a9efb";
        String type = "public";

        EdamamApi api = ApiClient.getRetrofitInstance().create(EdamamApi.class);

        // Assuming the API requires meal type and dish type as separate parameters
        Call<RecipeResponse> call = api.searchRecipes(query, app_id, app_key, type,null, mealType, dishType, null);

        call.enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<RecipeResponse.Hit> hits = response.body().getHits(); // Get hits from response

                    // Debugging: Log the number of recipes fetched
                    Log.d("Fetched Recipes", "Number of recipes fetched: " + hits.size());

                    // Clear previous recipes
                    recipeList.clear();

                    for (RecipeResponse.Hit hit : hits) {
                        Recipe recipe = hit.getRecipe(); // Extract the Recipe from Hit

                        double caloriesPer100g = recipe.getCaloriesPer100g();
                        if (recipe.getTotalWeight() > 0) {
                            caloriesPer100g = (recipe.getCalories() / recipe.getTotalWeight()) * 100;
                        }
                        recipe.setCaloriesPer100g(caloriesPer100g); // Update recipe object

                        recipeList.add(recipe);
                    }

                    recipeAdapter.notifyDataSetChanged();
                } else {
                    Log.d("Fetch Recipes", "Response was not successful or body is null. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<RecipeResponse> call, Throwable t) {
                Log.e("Fetch Recipes", "Error: " + t.getMessage());
            }
        });
    }

    private String getRandomSimpleFoodSearch() {
        return simpleFoodSearches.get(random.nextInt(simpleFoodSearches.size()));
    }
}
