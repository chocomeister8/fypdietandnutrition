package com.example.dietandnutritionapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NavAllRecipesFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipeList;
    private EditText searchEditText;

    private final String[] recipeQueries = {"balanced", "high-fiber", "high-protein", "low-carb", "low-fat", "low-sodium"};
    private final Random random = new Random();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nav_all_recipes, container, false);

        // Initialize buttons
        Button button_all_recipes = view.findViewById(R.id.button_all_recipes);
        Button button_vegetarian = view.findViewById(R.id.button_vegetarian);
        Button button_favourite = view.findViewById(R.id.button_favourite);
        Button button_personalise_recipes = view.findViewById(R.id.button_personalise);
        Button button_recipes_status = view.findViewById(R.id.button_recipes_status);
        Button button_recommendedRecipes = view.findViewById(R.id.button_recommendRecipes);

        Button button_add_recipe = view.findViewById(R.id.add_recipe_button);


        searchEditText = view.findViewById(R.id.search_recipe);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recipe_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the recipe list and adapter
        recipeList = new ArrayList<>();
        recipeAdapter = new RecipeAdapter(recipeList, this::openRecipeDetailFragment, false);
        recyclerView.setAdapter(recipeAdapter);

        // Fetch recipes
        fetchRecipes(getRandomQuery());

        // Set up button click listeners
        button_all_recipes.setOnClickListener(v -> navigateToFragment(new NavAllRecipesFragment()));
        button_vegetarian.setOnClickListener(v -> navigateToFragment(new NavVegetarianRecipesFragment()));
        button_favourite.setOnClickListener(v -> navigateToFragment(new NavFavouriteRecipesFragment()));
        button_personalise_recipes.setOnClickListener(v -> navigateToFragment(new NavPersonaliseRecipesFragment()));
        button_recipes_status.setOnClickListener(v -> navigateToFragment(new NavRecipesStatusFragment()));
        button_recommendedRecipes.setOnClickListener(v -> navigateToFragment(new NavRecommendedRecipesFragment()));

        button_add_recipe.setOnClickListener(v -> navigateToFragment(new AddRecipeFragment()));


        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing before text is changed
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Perform search when text changes
                String query = charSequence.toString();
                if (!query.isEmpty()) {
                    fetchRecipes(query); // Fetch recipes based on the query
                } else {
                    // Optionally, fetch default recipes or clear the list when search bar is empty
                    fetchRecipes("rice"); // or recipeList.clear();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Do nothing after text is changed
            }
        });
        return view;
    }

    private void navigateToFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit();
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

    private void fetchRecipes(String query) {
        String app_id = "2c7710ea"; // Your Edamam API app ID
        String app_key = "97f5e9187c865600f74e2baa358a9efb"; // Your Edamam API app key
        String type = "public";
        String health = "DASH";

        EdamamApi api = ApiClient.getRetrofitInstance().create(EdamamApi.class);
        Call<RecipeResponse> call = api.searchRecipes(query, app_id, app_key, type, health);

        call.enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recipeList.clear();
                    for (RecipeResponse.Hit hit : response.body().getHits()) {
                        Recipe recipe = hit.getRecipe(); // Directly get the Recipe object from Hit

                        double caloriesPer100g = recipe.getCaloriesPer100g();
                        if (recipe.getTotalWeight() > 0) {
                            caloriesPer100g = (recipe.getCalories() / recipe.getTotalWeight()) * 100;
                        }

                        // Optionally, you can set this value back into the recipe object or create a new object to store it
                        // For example:
                        recipe.setCaloriesPer100g(caloriesPer100g); // Make sure to add this method in Recipe class

                        recipeList.add(recipe); // Add the recipe to the list
                    }
                    recipeAdapter.notifyDataSetChanged(); // Notify adapter about data change
                } else {
                    Log.d("API Response", "Response not successful: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<RecipeResponse> call, Throwable t) {
                Log.e("API Error", "Failed to fetch recipes: " + t.getMessage());
            }
        });
    }
    private String getRandomQuery() {
        int index = random.nextInt(recipeQueries.length); // Get a random index
        return recipeQueries[index]; // Return the random query
    }
}
