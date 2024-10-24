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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class NavRecommendedRecipesFragment extends Fragment implements NavRecommendedRecipesController.OnRecommendedRecipesRetrievedListener {

    private FirebaseFirestore firestore;
    private double userCalorieGoal;

    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipeList;
    private EditText searchEditText;
    private Spinner mealTypeSpinner;
    private Spinner dishTypeSpinner;
    private List<Recipe> recipeNewList;
    private List<Recipe> APIRecipeList;

    private final Random random = new Random();

    private List<String> simpleFoodSearches = Arrays.asList(
            "chicken", "beef", "steak", "fish","lamb"
    );

    private final String[] mealTypes = {"--Select Meal Type--", "Breakfast", "Lunch", "Dinner", "Snack", "Teatime"};
    private final String[] dishTypes = {"--Select Dish Type--", "Starter", "Main course", "Side dish", "Soup", "Condiments and sauces", "Desserts", "Drinks", "Salad"};

    private boolean initialLoadDone = false;
    private boolean isViewInitialized = false; // New flag to check if view is fully initialized

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.nav_recommended_recipes, container, false);

        firestore = FirebaseFirestore.getInstance();
        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recipe_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recipeList = new ArrayList<>();
        APIRecipeList = new ArrayList<>();
        recipeAdapter = new RecipeAdapter(recipeList, this::openRecipeDetailFragment, false);
        recyclerView.setAdapter(recipeAdapter);

        searchEditText = view.findViewById(R.id.search_recipe);

        // Setup spinners
        mealTypeSpinner = view.findViewById(R.id.spinner_meal_type);
        dishTypeSpinner = view.findViewById(R.id.spinner_dish_type);
        setupSpinners(); // Call to setup spinners

        if (savedInstanceState != null) {
            String savedSearchQuery = savedInstanceState.getString("search_query", "");
            int savedMealTypePos = savedInstanceState.getInt("spinner1_value", 0);
            int savedDishTypePos = savedInstanceState.getInt("spinner2_value", 0);

            searchEditText.setText(savedSearchQuery);
            mealTypeSpinner.setSelection(savedMealTypePos);
            dishTypeSpinner.setSelection(savedDishTypePos);

            // Fetch recipes based on restored state
            filterRecipes();
            fetchRecipesAPI(getRandomSimpleFoodSearch(),null,null);
            displayRecommendedRecipes();

        } else if (getArguments() != null) {
            // Restore state from arguments (when returning from RecipeDetailFragment)
            String savedSearchQuery = getArguments().getString("search_query", "");
            int savedMealTypePos = getArguments().getInt("spinner1_value", 0);
            int savedDishTypePos = getArguments().getInt("spinner2_value", 0);

            searchEditText.setText(savedSearchQuery);
            mealTypeSpinner.setSelection(savedMealTypePos);
            dishTypeSpinner.setSelection(savedDishTypePos);
            filterRecipes();

        } else if (!initialLoadDone) {
            // Fetch recipes with a default random query only if initial load is not done
            fetchRecipes();
        }

        // Initialize buttons using view.findViewById
        Button button_all_recipes = view.findViewById(R.id.button_all_recipes);
        Button button_vegetarian = view.findViewById(R.id.button_vegetarian);
        Button button_favourite = view.findViewById(R.id.button_favourite);
        Button button_personalise_recipes = view.findViewById(R.id.button_personalise);
        Button button_recipes_status = view.findViewById(R.id.button_recipes_status);
        Button button_recommendedRecipes = view.findViewById(R.id.button_recommendRecipes);

        button_all_recipes.setOnClickListener(v -> navigateToFragment(new NavAllRecipesFragment()));
        button_vegetarian.setOnClickListener(v -> navigateToFragment(new NavVegetarianRecipesFragment()));
        button_favourite.setOnClickListener(v -> navigateToFragment(new ViewFavouriteRecipesFragment()));
        button_personalise_recipes.setOnClickListener(v -> navigateToFragment(new NavCommunityRecipesFragment()));
        button_recipes_status.setOnClickListener(v -> navigateToFragment(new NavPendingRecipesFragment()));
        button_recommendedRecipes.setOnClickListener(v -> navigateToFragment(new NavRecommendedRecipesFragment()));

        Button clearFiltersButton = view.findViewById(R.id.clear_filters_button);
        clearFiltersButton.setOnClickListener(v -> clearFiltersAndFetchRandomRecipes());

        setupSpinnerListeners();
        setupSearchBar();
        isViewInitialized = true;

        return view;
    }

    private void filterRecipes() {
        Log.d("FilterRecipes", "Total Recipes Available: " + recipeList.size());

        String searchQuery = searchEditText.getText().toString().trim().toLowerCase();
        String selectedMealType = mealTypeSpinner.getSelectedItem() != null ? mealTypeSpinner.getSelectedItem().toString() : "--Select Meal Type--";
        String selectedDishType = dishTypeSpinner.getSelectedItem() != null ? dishTypeSpinner.getSelectedItem().toString() : "--Select Dish Type--";

        // Log the current selections
        Log.d("FilterRecipes", "Search Query: " + searchQuery + ", Meal Type: " + selectedMealType + ", Dish Type: " + selectedDishType);

        // Create a new list to hold filtered recipes
        List<Recipe> filteredList = new ArrayList<>();

        // Check if search query is empty and both spinners are set to default
        boolean isSearchEmpty = searchQuery.isEmpty();
        boolean isMealTypeDefault = selectedMealType.equals("--Select Meal Type--");
        boolean isDishTypeDefault = selectedDishType.equals("--Select Dish Type--");

        for (Recipe recipe : recipeList) {
            boolean matchesSearchQuery = isSearchEmpty || recipe.getLabel().toLowerCase().contains(searchQuery);
            boolean matchesMealType = isMealTypeDefault || recipe.getMealType().equals(selectedMealType);
            boolean matchesDishType = isDishTypeDefault || recipe.getDishType().equals(selectedDishType);

            // Check if the recipe matches the search query and selected types
            if (matchesSearchQuery && matchesMealType && matchesDishType) {
                filteredList.add(recipe);
            }
        }

        // Update the adapter with the filtered list
        recipeAdapter.updateRecipeList(filteredList);
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
                // Only fetch recipes if the initial load is done and the view is fully initialized
                if (initialLoadDone && isViewInitialized) {
                    filterRecipes();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        dishTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Only fetch recipes if the initial load is done and the view is fully initialized
                if (initialLoadDone && isViewInitialized) {
                    filterRecipes();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
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
                // Only fetch recipes if the initial load is done and the view is fully initialized
                if (initialLoadDone && isViewInitialized) {
                    filterRecipes();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Do nothing after text is changed
            }
        });
    }

    private void clearFiltersAndFetchRandomRecipes() {
        // Reset the spinners to default selections
        mealTypeSpinner.setSelection(0); // Assuming the first position is the default
        dishTypeSpinner.setSelection(0); // Assuming the first position is the default

        // Clear the search bar
        searchEditText.setText(""); // This will clear the search bar

        // Fetch recipes with a random query
        fetchRecipes();
    }

    private void navigateToFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void openRecipeDetailFragment(Recipe recipe) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("selected_recipe", recipe);  // Assuming selectedRecipe is the clicked recipe object
        bundle.putString("source", "recommended");  // Pass "all" as the source
        bundle.putString("search_query", searchEditText.getText().toString());  // Pass the search query
        bundle.putInt("spinner1_value", mealTypeSpinner.getSelectedItemPosition());  // Pass the selected position of spinner1
        bundle.putInt("spinner2_value", dishTypeSpinner.getSelectedItemPosition());  // Pass the selected position of spinner2

        RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
        recipeDetailFragment.setArguments(bundle);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, recipeDetailFragment)
                .addToBackStack(null)
                .commit();

    }

    private void fetchRecipesAPI(String query, String mealType, String dishType) {
        String app_id = "2c7710ea"; // Your Edamam API app ID
        String app_key = "97f5e9187c865600f74e2baa358a9efb";
        String type = "public";

        EdamamApi api = ApiClient.getRetrofitInstance().create(EdamamApi.class);

        Call<RecipeResponse> call = api.searchRecipes(query, app_id, app_key, type, null, mealType, dishType, null);

        call.enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<RecipeResponse.Hit> hits = response.body().getHits();

                    Log.d("Fetched Recipes", "Number of recipes fetched: " + hits.size());

                    // Clear previous recipes
                    APIRecipeList.clear();

                    for (RecipeResponse.Hit hit : hits) {
                        Recipe recipe = hit.getRecipe();
                        double caloriesPer100g = recipe.getCaloriesPer100g();

                        if (recipe.getTotalWeight() > 0) {
                            caloriesPer100g = (recipe.getCalories() / recipe.getTotalWeight()) * 100;
                        }

                        recipe.setCaloriesPer100g(caloriesPer100g);

                        // Filter recipes based on user's calorie goal
                        if (recipe.getCalories() <= userCalorieGoal) {
                            APIRecipeList.add(recipe); // Add only recipes that meet the calorie goal
                        }
                    }
//                    recipeAdapter.notifyDataSetChanged();

                    if (!initialLoadDone) {
                        setupSpinnerListeners();
                        setupSearchBar();
                        initialLoadDone = true; // Set flag to true after first fetch
                    }
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


    private void fetchRecipes() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            NavRecommendedRecipesController navRecommendedRecipesController = new NavRecommendedRecipesController();

            // Call retrieveRecommendedRecipes with userId and an implementation of the listener
            navRecommendedRecipesController.retrieveRecommendedRecipes(userId, getContext(), this);

        } else {
            Toast.makeText(getContext(), "User is not logged in.", Toast.LENGTH_SHORT).show();
        }
    }


    public void displayRecommendedRecipes() {
        // Ensure recipeNewList is initialized
        if (recipeNewList == null) {
            recipeNewList = new ArrayList<>(); // Initialize if null
        } else {
            recipeNewList.clear(); // Clear if already initialized
        }

        // Compare the two recipe lists (recipeList and APIRecipeList)
        for (Recipe recipe1 : recipeList) {
            for (Recipe recipe2 : APIRecipeList) {
                // Compare by both label and calories
                if (recipe1.getLabel().equals(recipe2.getLabel())) {
                    recipeNewList.add(recipe2); // Add matching recipe to the new list
                    break;  // Exit inner loop once a match is found
                }
            }
        }

        // Notify the adapter that the data has changed
        recipeList.clear();
        recipeList.addAll(recipeNewList);
        recipeAdapter.notifyDataSetChanged();
    }


    private String getRandomSimpleFoodSearch() {
        if (!simpleFoodSearches.isEmpty()) {
            return simpleFoodSearches.get(random.nextInt(simpleFoodSearches.size()));
        } else {
            Log.w("Random Search", "Simple food searches list is empty!");
            return ""; // Return an empty string or handle it appropriately
        }
    }

    private String getUserId() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        return (auth.getCurrentUser() != null) ? auth.getCurrentUser().getUid() : null;
    }

    @Override
    public void onRecipesRetrieved(ArrayList<Recipe> recipes) {
        // Update the UI with the retrieved recommended recipes
        recipeList.clear();
        recipeList.addAll(recipes);
        recipeAdapter.notifyDataSetChanged();

        filterRecipes();
    }

    @Override
    public void onError(Exception e) {

    }
}
