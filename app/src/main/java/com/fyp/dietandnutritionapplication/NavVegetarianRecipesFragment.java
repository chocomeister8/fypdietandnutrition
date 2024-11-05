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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NavVegetarianRecipesFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipeList = new ArrayList<>();
    private EditText searchEditText;
    private Spinner mealTypeSpinner;
    private Spinner dishTypeSpinner;

    private final String[] recipeQueries = {"Vegetable Stir-Fry", "Vegetable Curry", "Lentil Soup", "Vegetarian Pizza", "Vegetable Fried Rice", "Veggie Lasagna"};
    private final Random random = new Random();
    private final String[] mealTypes = {"--Select Meal Type--", "Breakfast", "Lunch", "Dinner", "Snack", "Teatime"};
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
        View view = inflater.inflate(R.layout.nav_vegetarian_recipes, container, false);

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

        // Initialize RecyclerView and adapter
        recyclerView = view.findViewById(R.id.recipe_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recipeAdapter = new RecipeAdapter(recipeList, this::openRecipeDetailFragment, false);
        recyclerView.setAdapter(recipeAdapter);
        searchEditText = view.findViewById(R.id.search_recipe);

        fetchRecipes(getRandomQuery(), null,null);

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

        } else if (getArguments() != null) {
            // Restore state from arguments (when returning from RecipeDetailFragment)
            String savedSearchQuery = getArguments().getString("search_query", "");
            int savedMealTypePos = getArguments().getInt("spinner1_value", 0);
            int savedDishTypePos = getArguments().getInt("spinner2_value", 0);

            searchEditText.setText(savedSearchQuery);
            mealTypeSpinner.setSelection(savedMealTypePos);
            dishTypeSpinner.setSelection(savedDishTypePos);

        } else if (!initialLoadDone) {
            // Fetch recipes with a default random query only if initial load is not done
            fetchRecipes(getRandomQuery(), null, null);
        }

        // Initialize buttons using view.findViewById
        Button button_all_recipes = view.findViewById(R.id.button_all_recipes);
        Button button_vegetarian = view.findViewById(R.id.button_vegetarian);
        Button button_favourite = view.findViewById(R.id.button_favourite);
        Button button_personalise_recipes = view.findViewById(R.id.button_personalise);
        Button button_recipes_status = view.findViewById(R.id.button_recipes_status);
        Button button_recommendedRecipes = view.findViewById(R.id.button_recommendRecipes);
        Button button_add_recipe = view.findViewById(R.id.add_recipe_button);

        Button clearFiltersButton = view.findViewById(R.id.clear_filters_button);
        clearFiltersButton.setOnClickListener(v -> clearFiltersAndFetchRandomRecipes());

        // Set up button click listeners to navigate between fragments
        button_all_recipes.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NavAllRecipesFragment())
                    .addToBackStack(null)  // Add to back stack to enable back navigation
                    .commit();
        });


        button_vegetarian.setOnClickListener(v -> {
            // Fetch vegetarian/vegan recipes when the button is clicked
            fetchRecipes(getRandomQuery(), null, null);
        });

        button_favourite.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new ViewFavouriteRecipesFragment())
                    .addToBackStack(null)
                    .commit();
        });

        button_personalise_recipes.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NavCommunityRecipesFragment())
                    .addToBackStack(null)
                    .commit();
        });

        button_recipes_status.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NavPendingRecipesFragment())
                    .addToBackStack(null)
                    .commit();
        });

        button_recommendedRecipes.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NavRecommendedRecipesFragment())
                    .addToBackStack(null)
                    .commit();
        });
        button_add_recipe.setOnClickListener(v -> {
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout, new AddRecipeFragment())
                            .addToBackStack(null)
                            .commit();
        });

        setupSpinnerListeners();
        setupSearchBar();

        isViewInitialized = true;

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

    private void filterRecipes() {
        String searchQuery = searchEditText.getText().toString().trim();
        String selectedMealType = mealTypeSpinner.getSelectedItem() != null ? mealTypeSpinner.getSelectedItem().toString() : "--Select Meal Type--";
        String selectedDishType = dishTypeSpinner.getSelectedItem() != null ? dishTypeSpinner.getSelectedItem().toString() : "--Select Dish Type--";

        // Log the current selections
        Log.d("FilterRecipes", "Search Query: " + searchQuery + ", Meal Type: " + selectedMealType + ", Dish Type: " + selectedDishType);

        // Call fetchRecipes with all current parameters
        fetchRecipes(searchQuery, selectedMealType.equals("--Select Meal Type--") ? null : selectedMealType,
                selectedDishType.equals("--Select Dish Type--") ? null : selectedDishType);
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
        fetchRecipes(getRandomQuery(), null, null);
    }

    private void openRecipeDetailFragment(Recipe recipe) {
        // Create a new fragment instance and pass the recipe data
        Bundle bundle = new Bundle();
        bundle.putParcelable("selected_recipe", recipe);  // Assuming selectedRecipe is the clicked recipe object
        bundle.putString("source", "vegetarian");  // Pass "vegetarian" as the source
        bundle.putString("search_query", searchEditText.getText().toString());  // Pass the search query
        bundle.putInt("spinner1_value", mealTypeSpinner.getSelectedItemPosition());
        bundle.putInt("spinner2_value", dishTypeSpinner.getSelectedItemPosition());

        RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
        recipeDetailFragment.setArguments(bundle);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, recipeDetailFragment)
                .addToBackStack(null)
                .commit();
    }

    // Method to fetch vegetarian and vegan recipes
    private void fetchRecipes(String query, String mealType, String dishType) {
        String app_id = "2c7710ea"; // Your Edamam API app ID
        String app_key = "97f5e9187c865600f74e2baa358a9efb";
        String type = "public";

        EdamamApi api = ApiClient.getRetrofitInstance().create(EdamamApi.class);

        // Assuming the API requires meal type and dish type as separate parameters
        Call<RecipeResponse> call = api.searchRecipes(query, app_id, app_key, type,"vegetarian" ,mealType, dishType, null);

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


    private String getRandomQuery() {
        int index = random.nextInt(recipeQueries.length); // Get a random index
        return recipeQueries[index]; // Return the random query
    }
}
