package com.fyp.dietandnutritionapplication;

import android.content.Context;

import java.util.ArrayList;

public class NavRecommendedRecipesController {

    private RecommendedRecipesEntity recommendedRecipesEntity;
    private ArrayList<Recipe> retrievedRecipes = new ArrayList<>();

    // Constructor
    public NavRecommendedRecipesController() {
        recommendedRecipesEntity = new RecommendedRecipesEntity();  // Initialize the entity
    }

    // Method to retrieve recommended recipes using getRecommendedRecipesByUserId
    public void retrieveRecommendedRecipes(String userId, Context context, final OnRecommendedRecipesRetrievedListener listener) {
        // Call getRecommendedRecipesByUserId from RecommendedRecipesEntity
        recommendedRecipesEntity.getRecommendedRecipesByUserId(userId, context,new RecommendedRecipesEntity.OnRecommendedRecipesRetrievedListener() {
            @Override
            public void onRecipesRetrieved(ArrayList<Recipe> recipes) {
                // Save the retrieved recipes locally
                retrievedRecipes.clear();  // Clear previously stored recipes
                retrievedRecipes.addAll(recipes);  // Add the newly retrieved recipes

                // Notify the listener (e.g., UI) that the recipes are available
                listener.onRecipesRetrieved(recipes);
            }

            @Override
            public void onError(Exception e) {
                // If there's an error, pass it to the listener
                listener.onError(e);
            }
        });
    }


    // Method to save a recipe to Firestore
    public void saveRecipe(Recipe recipe, String username, Context context) {
        recommendedRecipesEntity.saveRecipeToFirestore(recipe, username, context);
    }

    // Getter method for retrieving the saved recipes list
    public ArrayList<Recipe> getRetrievedRecipes() {
        return retrievedRecipes;
    }

    // Interface for passing the retrieved recipes or error back to the caller (e.g., a fragment)
    public interface OnRecommendedRecipesRetrievedListener {
        void onRecipesRetrieved(ArrayList<Recipe> recipes);
        void onError(Exception e);
    }
}