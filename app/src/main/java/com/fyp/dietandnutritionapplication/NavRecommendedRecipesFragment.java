package com.fyp.dietandnutritionapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import android.widget.ImageView;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
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
    private List<Recipe> filteredrecipeList;
    private EditText searchEditText;
    private Spinner mealTypeSpinner;
    private Spinner dishTypeSpinner;
    private List<Recipe> recipeNewList;
    private List<Recipe> APIRecipeList;

    private final Random random = new Random();

    private List<String> simpleFoodSearches = Arrays.asList(
            "chicken", "beef", "steak", "fish","lamb"
    );

    private final String[] mealTypes = {"--Select Meal Type--", "Breakfast", "Lunch/Dinner", "Snack", "Teatime"};
    private final String[] dishTypes = {"--Select Dish Type--", "Starter", "Main course", "Side dish", "Soup", "Condiments and sauces", "Desserts", "Drinks", "Salad"};

    private boolean initialLoadDone = false;
    private boolean isViewInitialized = false; // New flag to check if view is fully initialized

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private TextView notificationBadgeTextView;
    private NotificationUController notificationUController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.nav_recommended_recipes, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            notificationBadgeTextView = view.findViewById(R.id.notificationBadgeTextView);

            notificationUController = new NotificationUController();
            notificationUController.fetchNotifications(userId, new Notification.OnNotificationsFetchedListener() {
                @Override
                public void onNotificationsFetched(List<Notification> notifications) {
                    // Notifications can be processed if needed

                    // After fetching notifications, count them
                    notificationUController.countNotifications(userId, new Notification.OnNotificationCountFetchedListener() {
                        @Override
                        public void onCountFetched(int count) {
                            if (count > 0) {
                                notificationBadgeTextView.setText(String.valueOf(count));
                                notificationBadgeTextView.setVisibility(View.VISIBLE);
                            } else {
                                notificationBadgeTextView.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            });

        }

        ImageView notiImage = view.findViewById(R.id.noti_icon);
        notiImage.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NotificationUFragment())
                    .addToBackStack(null)
                    .commit();

        });

        firestore = FirebaseFirestore.getInstance();
        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recipe_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recipeList = new ArrayList<>();
        filteredrecipeList = new ArrayList<>();
        APIRecipeList = new ArrayList<>();
        recipeNewList = new ArrayList<>();
        recipeAdapter = new RecipeAdapter(recipeList, this::openRecipeDetailFragment, false);
        recyclerView.setAdapter(recipeAdapter);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Start fetching recipes after the delay
            fetchRecipesFromRecommended();
        }, 1000);

        fetchRecipes();
        isViewInitialized = true;

        searchEditText = view.findViewById(R.id.search_recipe);
        // Setup spinners
        mealTypeSpinner = view.findViewById(R.id.spinner_meal_type);
        dishTypeSpinner = view.findViewById(R.id.spinner_dish_type);
        setupSpinners();

        if (savedInstanceState != null) {
            String savedSearchQuery = savedInstanceState.getString("search_query", "");
            int savedMealTypePos = savedInstanceState.getInt("spinner1_value", 0);
            int savedDishTypePos = savedInstanceState.getInt("spinner2_value", 0);

            searchEditText.setText(savedSearchQuery);
            mealTypeSpinner.setSelection(savedMealTypePos);
            dishTypeSpinner.setSelection(savedDishTypePos);

            // Fetch recipes based on restored state
//            filterRecipes();

//            displayRecommendedRecipes();

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
            initialLoadDone = true; // Set this to true after the first load
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

        return view;
    }

    private void filterRecipes() {
        Log.d("FilterRecipes", "Total Recipes Available: " + recipeList.size());

        String searchQuery = searchEditText.getText().toString().trim().toLowerCase();
        String selectedMealType = mealTypeSpinner.getSelectedItem() != null ? mealTypeSpinner.getSelectedItem().toString(): "--Select Meal Type--";
        String selectedDishType = dishTypeSpinner.getSelectedItem() != null ? dishTypeSpinner.getSelectedItem().toString(): "--Select Dish Type--";

        // Log the current selections
        Log.d("FilterRecipes", "Search Query: " + searchQuery + ", Meal Type: " + selectedMealType + ", Dish Type: " + selectedDishType);

        // Create a new list to hold filtered recipes
        List<Recipe> filteredList = new ArrayList<>();


        for (Recipe recipe : recipeList) {
            boolean matchesSearchQuery = searchQuery.isEmpty() || recipe.getLabel().toLowerCase().contains(searchQuery);
            boolean matchesMealType = selectedMealType.equals("--Select Meal Type--") || recipe.getMealType().equals(selectedMealType);
            boolean matchesDishType = selectedDishType.equals("--Select Dish Type--") || recipe.getDishType().equals(selectedDishType);

            Log.d("FilterRecipes", "Recipe: " + recipe.getLabel() + ", Matches: " + matchesSearchQuery + ", " + matchesMealType + ", " + matchesDishType);

            // Check if the recipe matches the search query and selected types
            if (matchesSearchQuery && matchesMealType && matchesDishType) {
                filteredList.add(recipe);
            }
        }

        // Update the adapter with the filtered list
        recipeAdapter.updateRecipeList(filteredList);
        recipeAdapter.notifyDataSetChanged();
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
                    Log.d("Spinner", "Meal type selected: " + mealTypes[position]);
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
                    Log.d("Spinner", "Dish type selected: " + dishTypes[position]);
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
                filterRecipes();
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

    private void fetchRecipesFromRecommended() {
        // Clear previous recipe lists
        APIRecipeList.clear();


        String app_id = "2c7710ea"; // Your Edamam API app ID
        String app_key = "97f5e9187c865600f74e2baa358a9efb";
        String type = "public";

        // Fetch favorite recipes
//        fetchFavoriteRecipes(null, null, null);

        // Initialize AtomicInteger to track completed requests
        AtomicInteger completedRequests = new AtomicInteger(0);
        int totalRecipes = recipeList.size(); // Total number of recipes to fetch

        // Log the number of recipes being fetched
        Log.d("Fetch Recipes", "Total recipes to fetch: " + totalRecipes);


        // Iterate through each recipe label
        for (Recipe recipe : recipeList) {
            String labelQuery = recipe.getLabel(); // Use the label from the current recipe
            Log.d("API Call", "Fetching recipes for: " + labelQuery);

            EdamamApi api = ApiClient.getRetrofitInstance().create(EdamamApi.class);

            // Call the API to fetch recipes based on the label
            Call<RecipeResponse> call = api.searchRecipes(labelQuery, app_id, app_key, type, null, null, null, null);

            call.enqueue(new Callback<RecipeResponse>() {
                @Override
                public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<RecipeResponse.Hit> hits = response.body().getHits(); // Get hits from response

                        // Log the number of recipes fetched
                        Log.d("Fetched Recipes", "Number of recipes fetched for " + labelQuery + ": " + hits.size());

                        for (RecipeResponse.Hit hit : hits) {
                            Recipe apiRecipe = hit.getRecipe(); // Extract the Recipe from Hit

                            // Calculate calories per 100g
                            double caloriesPer100g = apiRecipe.getCaloriesPer100g();
                            if (apiRecipe.getTotalWeight() > 0) {
                                caloriesPer100g = (apiRecipe.getCalories() / apiRecipe.getTotalWeight()) * 100;
                            }
                            apiRecipe.setCaloriesPer100g(caloriesPer100g); // Update recipe object

                            APIRecipeList.add(apiRecipe); // Add to the APIRecipeList
                        }

                        // Compare the retrieved recipes with the user's favorite recipes
                        for (Recipe favoriteRecipe : recipeList) {
                            for (Recipe apiRecipe : APIRecipeList) {
                                // Compare by label
                                if (favoriteRecipe.getLabel().equals(apiRecipe.getLabel())) {
                                    Log.d("Matching Recipe:", favoriteRecipe.getLabel());
                                    recipeNewList.add(apiRecipe); // Add matching recipe to the new list
                                    break; // Exit inner loop once a match is found
                                } else {
                                    Log.d("No Match Found:", favoriteRecipe.getLabel() + " vs " + apiRecipe.getLabel());
                                }
                            }
                        }
                    } else {
                        Log.d("Fetch Recipes", "Response was not successful or body is null. Code: " + response.code());
                    }

                    // Increment completed requests
                    if (completedRequests.incrementAndGet() == totalRecipes) {

                        // Update the UI after all requests have completed
                        recipeList.clear();
                        recipeList.addAll(recipeNewList);
                        HashSet<Recipe> recipeSet = new HashSet<>(recipeList);
                        recipeList.clear();
                        recipeList.addAll(recipeSet);
                        Log.d("Recipe List Size", "Size before notify: " + recipeList.size());
                        recipeAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<RecipeResponse> call, Throwable t) {
                    Log.e("Fetch Recipes", "Error: " + t.getMessage());
                    // Increment completed requests even if there was a failure
                    if (completedRequests.incrementAndGet() == totalRecipes) {
                        // Update the UI in case of failure as well
                        recipeList.clear();
                        recipeList.addAll(recipeNewList);
                        Log.d("Recipe List Size", "Size before notify: " + recipeList.size());
                        recipeAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
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

    @Override
    public void onRecipesRetrieved(ArrayList<Recipe> recipes) {
        // Clear previous data and set new data
        recipeList.clear();
        recipeList.addAll(recipes);
        filteredrecipeList.clear(); // Reset filtered recipes
        filteredrecipeList.addAll(recipes); // Initialize filtered with all recipes

        // Update the adapter to refresh the displayed recipes
        recipeAdapter.notifyDataSetChanged();
    }

    @Override
    public void onError(Exception e) {

    }
}
