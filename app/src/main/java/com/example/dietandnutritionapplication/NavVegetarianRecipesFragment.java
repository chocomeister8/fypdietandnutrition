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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NavVegetarianRecipesFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipeList = new ArrayList<>();
    private EditText searchEditText;
    private Spinner mealTypeSpinner;
    private Spinner dishTypeSpinner;

    private final String[] recipeQueries = {"American", "Asian", "British", "Caribbean", "Central Europe", "Chinese", "Eastern Europe", "French", "Indian", "Italian", "Japanese", "Kosher", "Mediterranean", "Mexican", "Middle Eastern", "Nordic", "South American", "South East Asian"};
    private final Random random = new Random();
    private final String[] mealTypes = {"--Select Meal Type--", "Breakfast", "Lunch", "Dinner", "Snack", "Teatime"};
    private final String[] dishTypes = {"--Select Dish Type--", "Starter", "Main course", "Side dish", "Soup", "Condiments and sauces", "Desserts", "Drinks", "Salad"};


    private static final String BASE_URL = "https://api.edamam.com/";
    private static final String APP_ID = "your_app_id";  // Replace with your actual App ID
    private static final String APP_KEY = "your_app_key";  // Replace with your actual App Key

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.nav_vegetarian_recipes, container, false);

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

        // Call the setup methods for listeners
        setupSpinnerListeners(); // Call to setup spinner listeners
        setupSearchBar(); // Call to setup search bar listeners


        // Initialize buttons using view.findViewById
        Button button_all_recipes = view.findViewById(R.id.button_all_recipes);
        Button button_vegetarian = view.findViewById(R.id.button_vegetarian);
        Button button_favourite = view.findViewById(R.id.button_favourite);
        Button button_personalise_recipes = view.findViewById(R.id.button_personalise);
        Button button_recipes_status = view.findViewById(R.id.button_recipes_status);
        Button button_recommendedRecipes = view.findViewById(R.id.button_recommendRecipes);

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
                    .replace(R.id.frame_layout, new NavFavouriteRecipesFragment())
                    .addToBackStack(null)
                    .commit();
        });

        button_personalise_recipes.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NavPersonaliseRecipesFragment())
                    .addToBackStack(null)
                    .commit();
        });

        button_recipes_status.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NavRecipesStatusFragment())
                    .addToBackStack(null)
                    .commit();
        });

        button_recommendedRecipes.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NavRecommendedRecipesFragment())
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
                filterRecipes(); // This now calls the combined filter method
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
        bundle.putParcelable("selected_recipe", recipe);

        RecipeDetailFragment fragment = new RecipeDetailFragment();
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment)
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
        Call<RecipeResponse> call = api.searchRecipes(query, app_id, app_key, type,"vegetarian" ,mealType, dishType);

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


    private String getRandomQuery() {
        int index = random.nextInt(recipeQueries.length); // Get a random index
        return recipeQueries[index]; // Return the random query
    }
}
