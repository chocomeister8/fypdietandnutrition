package com.fyp.dietandnutritionapplication;

import android.content.Context;

import java.util.ArrayList;

public class NavFavouriteRecipesController {
    private FavouriteRecipesEntity favouriteRecipesEntity;
    private ArrayList<Recipe> retrievedRecipes = new ArrayList<>();

    public NavFavouriteRecipesController() {
        favouriteRecipesEntity = new FavouriteRecipesEntity(); // Initialize entity
    }

    // Method to retrieve favorite recipes using the getRecipesFromFirestore function
    public void retrieveFavoriteRecipes(String userId, Context context, final OnFavoriteRecipesRetrievedListener listener) {
        // Call the getRecipesFromFirestore method from FavouriteRecipesEntity
        favouriteRecipesEntity.getRecipesFromFirestore(userId, context, new FavouriteRecipesEntity.OnRecipesRetrievedListener() {
            @Override
            public void onRecipesRetrieved(ArrayList<Recipe> recipes) {
                // Save the retrieved recipes locally
                retrievedRecipes.clear();  // Clear any previously stored recipes
                retrievedRecipes.addAll(recipes);
                listener.onFavoriteRecipesRetrieved(recipes);


            }

            @Override
            public void onError(Exception e) {
                // If there's an error, pass it to the listener
                listener.onError(e);
            }
        });
    }

    // Getter method for retrieving the saved recipes list
    public ArrayList<Recipe> getRetrievedRecipes() {
        return retrievedRecipes;
    }

    // Interface for passing the retrieved recipes or error back to the caller (e.g., a fragment)
    public interface OnFavoriteRecipesRetrievedListener {
        void onFavoriteRecipesRetrieved(ArrayList<Recipe> recipes);
        void onError(Exception e);
    }
}