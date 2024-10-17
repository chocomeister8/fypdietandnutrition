package com.fyp.dietandnutritionapplication;

import java.util.ArrayList;

public class NutriApprovedRecipesController {

    private ArrayList<Recipe> recipesFetched = new ArrayList<>();

    public NutriApprovedRecipesController() {
    }

    public void fetchApprovedRecipes(final RecipesFetchedCallback callback) {
        RecipesEntity recipesEntity = new RecipesEntity();
        recipesEntity.fetchApprovedRecipes(new RecipesEntity.OnRecipesFetchedListener() {
            @Override
            public void onRecipesFetched(ArrayList<Recipe> fetchedRecipes) {
                recipesFetched.clear(); // Clear the existing list
                recipesFetched.addAll(fetchedRecipes); // Add fetched recipes to the list

                // Trigger callback with the fetched recipes
                callback.onRecipesFetched(recipesFetched);
            }
        });
    }

    // Callback interface to pass recipes back to fragment
    public interface RecipesFetchedCallback {
        void onRecipesFetched(ArrayList<Recipe> recipes);
    }
}
