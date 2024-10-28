package com.fyp.dietandnutritionapplication;

import android.util.Log;

import java.util.ArrayList;

public class NavUserFolderController {

    private String folderName; // Store the folder name
    private RecipesEntity recipesEntity; // Instance of RecipesEntity to fetch recipes

    public NavUserFolderController(String folderName) {
        this.folderName = folderName; // Set the folder name through the constructor
        this.recipesEntity = new RecipesEntity(); // Initialize RecipesEntity
    }

    public void checkFetchRecipesFolder(final OnRecipesFetchedListener listener) {
        recipesEntity.fetchRecipesFromFolder(folderName, new RecipesEntity.OnRecipesFetchedListener() {
            @Override
            public void onRecipesFetched(ArrayList<Recipe> fetchedRecipes) {
                // Call the listener to handle the fetched recipes
                if (listener != null) {
                    Log.d("NavUserFolderController", "Fetched recipes: " + fetchedRecipes.size());
                    listener.onRecipesFetched(fetchedRecipes);
                }
            }
        });
    }

    // Interface for handling fetched recipes
    public interface OnRecipesFetchedListener {
        void onRecipesFetched(ArrayList<Recipe> recipes);
    }
}