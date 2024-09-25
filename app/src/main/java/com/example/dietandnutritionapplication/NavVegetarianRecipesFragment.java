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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

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


        // Initialize buttons using view.findViewById
        Button button_all_recipes = view.findViewById(R.id.button_all_recipes);
        Button button_vegetarian = view.findViewById(R.id.button_vegetarian);
        Button button_favourite = view.findViewById(R.id.button_favourite);
        Button button_personalise_recipes = view.findViewById(R.id.button_personalise);
        Button button_recipes_status = view.findViewById(R.id.button_recipes_status);
        Button button_recommendedRecipes = view.findViewById(R.id.button_recommendRecipes);

        // Set up button click listeners to navigate between fragments
        button_all_recipes.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NavAllRecipesFragment())
                    .addToBackStack(null)  // Add to back stack to enable back navigation
                    .commit();
        });

        button_vegetarian.setOnClickListener(v -> {
            String query = "vegetarian";
            // Fetch vegetarian/vegan recipes when the button is clicked
            fetchRecipes(query);
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
    private void fetchRecipes(String query) {
        String app_id = "2c7710ea"; // Your Edamam API app ID
        String app_key = "97f5e9187c865600f74e2baa358a9efb"; // Your Edamam API app key
        String type = "public";

        EdamamApi api = ApiClient.getRetrofitInstance().create(EdamamApi.class);
        Call<RecipeResponse> call = api.searchRecipes(query, app_id, app_key, type, "vegetarian");

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
}
