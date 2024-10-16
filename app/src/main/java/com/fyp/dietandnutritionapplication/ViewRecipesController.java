package com.fyp.dietandnutritionapplication;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewRecipesController {

    private FirebaseFirestore db;
    private RecipesEntity recipesEntity;

    public ViewRecipesController() {
        recipesEntity = new RecipesEntity(); // Initialize RecipesEntity
    }

    public void fetchPendingRecipesForUser(String currentUserId, OnRecipesFetchedListener listener) {
        // Pass currentUserId and handle the listener
        recipesEntity.fetchUserPendingRecipes(currentUserId, new RecipesEntity.OnRecipesFetchedListener() {
            @Override
            public void onRecipesFetched(ArrayList<Recipe> recipeList) {
                // Handle the list of fetched recipes here
                // You can notify the adapter or update the UI based on the fetched recipes
                System.out.println("Fetched " + recipeList.size() + " pending recipes for user: " + currentUserId);
                listener.onRecipesFetched(recipeList);
            }
        });
    }

    public void fetchAllApprovedRecipes(OnRecipesFetchedListener listener) {
        recipesEntity.fetchAllApprovedRecipes(new RecipesEntity.OnRecipesFetchedListener() {
            @Override
            public void onRecipesFetched(ArrayList<Recipe> recipeList) {
                Log.d(TAG, "Fetched " + recipeList.size() + " approved recipes.");
                listener.onRecipesFetched(recipeList); // Pass the fetched list to the listener
            }
        });
    }

    // Callback interface to return the recipes to the fragment
    public interface OnRecipesFetchedListener {
        void onRecipesFetched(List<Recipe> recipeList);
    }
}
