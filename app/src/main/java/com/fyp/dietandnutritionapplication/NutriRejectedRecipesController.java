package com.fyp.dietandnutritionapplication;

import java.util.ArrayList;

public class NutriRejectedRecipesController {
    private ArrayList<Recipe> recipesFetched = new ArrayList<>();

    // Interface for callback to notify when data is fetched
    public interface OnRecipesFetchedListener {
        void onRecipesFetched(ArrayList<Recipe> recipes);
    }

    public NutriRejectedRecipesController() {
        // Constructor
    }

    // Method to fetch rejected recipes
    public void fetchRejectedRecipes(OnRecipesFetchedListener listener) {
        RecipesEntity recipesEntity = new RecipesEntity();
        recipesEntity.fetchRejectedRecipes(new RecipesEntity.OnRecipesFetchedListener() {
            @Override
            public void onRecipesFetched(ArrayList<Recipe> fetchedRecipes) {
                recipesFetched.clear(); // Clear the existing list
                recipesFetched.addAll(fetchedRecipes); // Add fetched recipes to the list

                // Notify the listener (fragment) that the data is ready
                listener.onRecipesFetched(recipesFetched);
            }
        });
    }
}
