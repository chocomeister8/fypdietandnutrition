package com.fyp.dietandnutritionapplication;

import static androidx.fragment.app.FragmentManager.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RecipesEntity {

    // Firestore instance
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private RecipeAdapter recipesAdapter;
    private List<Recipe> recipeList = new ArrayList<>();

    public RecipesEntity() {
        // Default constructor
    }

    // Method to fetch recipes from the folder
    public void fetchRecipesFromFolder(String folderNameInput, OnRecipesFetchedListener listener) {
        db.collection("RecipesFoldersStoring")  // Replace with your collection name
                .whereEqualTo("folderName", folderNameInput)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Recipe> recipeList = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Extract the recipes array from the document
                                List<Map<String, Object>> recipes = (List<Map<String, Object>>) document.get("recipes");

                                // Iterate through the array and convert each map into a Recipe object
                                if (recipes != null) {
                                    for (Map<String, Object> recipeMap : recipes) {
                                        Recipe recipe = new Recipe();
                                        recipe.setLabel((String) recipeMap.get("label"));
                                        recipe.setImage((String) recipeMap.get("image"));
                                        recipe.setMealType((List<String>) recipeMap.get("mealType"));
                                        recipe.setCuisineType((List<String>) recipeMap.get("cuisineType"));
                                        recipe.setDishType((List<String>) recipeMap.get("dishType"));
                                        recipe.setDietLabels((List<String>) recipeMap.get("dietLabels"));
                                        recipe.setHealthLabels((List<String>) recipeMap.get("healthLabels"));
                                        recipe.setIngredientLines((List<String>) recipeMap.get("ingredientLines"));
                                        recipe.setCalories(getDoubleValue(recipeMap.get("calories")));
                                        recipe.setTotalWeight(getDoubleValue(recipeMap.get("totalWeight")));
                                        recipe.setTotal_Time(getIntValue(recipeMap.get("total_time")));
                                        recipe.setCaloriesPer100g(getDoubleValue(recipeMap.get("caloriesPer100g")));
                                        recipe.setImage((String) recipeMap.get("url"));
                                        recipe.setUserId((String) recipeMap.get("userId"));

                                        // Add the Recipe object to the list
                                        recipeList.add(recipe);
                                    }
                                }
                            }

                            // Pass the fetched recipes to the listener
                            listener.onRecipesFetched(recipeList);
                        } else {
                            Log.d("Firestore", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    // Helper method to handle potential null values for Double fields
    private double getDoubleValue(Object value) {
        if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof Long) {
            return ((Long) value).doubleValue();
        } else {
            return 0.0;  // Default value
        }
    }

    // Helper method to handle potential null values for Integer fields
    private int getIntValue(Object value) {
        if (value instanceof Long) {
            return ((Long) value).intValue();
        } else if (value instanceof Integer) {
            return (Integer) value;
        } else {
            return 0;  // Default value
        }
    }

    // Callback interface for fetching recipes
    public interface OnRecipesFetchedListener {
        void onRecipesFetched(ArrayList<Recipe> recipeList);
    }

    public void AddRecipe(Recipe recipe) {
        String uniqueRecipeId = UUID.randomUUID().toString(); // Generate unique ID

        Map<String, Object> recipeData = new HashMap<>();

        recipeData.put("recipe_id", uniqueRecipeId);
        recipeData.put("label", recipe.getLabel());
        recipeData.put("calories", recipe.getCalories());
        recipeData.put("totalWeight", recipe.getTotalWeight());
        recipeData.put("total_time", recipe.getTotal_Time());
        recipeData.put("mealType", recipe.getMealType());
        recipeData.put("cuisineType", recipe.getCuisineType());
        recipeData.put("dishType", recipe.getDishType());
        recipeData.put("ingredientsList", recipe.getIngredientLines());
        recipeData.put("userId", recipe.getuserId());
        recipeData.put("status", recipe.getStatus());

        // Store the recipe in Firestore
        db.collection("Recipes").document(uniqueRecipeId).set(recipeData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("RecipesEntity", "Recipe successfully added with ID: " + uniqueRecipeId);
                })
                .addOnFailureListener(e -> {
                    Log.e("RecipesEntity", "Error adding recipe", e);
                });
    }

    public void fetchUserPendingRecipes(String userId, OnRecipesFetchedListener listener) {
        db.collection("Recipes")
                .whereEqualTo("userId", userId) // Filter to get only the recipes by the current user
                .whereEqualTo("status", "Pending") // Filter to get only recipes with status "Pending"
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Recipe> recipeList = new ArrayList<>(); // Temporary list to store recipes

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Recipe recipe = document.toObject(Recipe.class);
                                recipe.setRecipe_id(document.getId());

                                // Calculate calories per 100g if total weight is available
                                double caloriesPer100g = recipe.getCaloriesPer100g();
                                if (recipe.getTotalWeight() > 0) {
                                    caloriesPer100g = (recipe.getCalories() / recipe.getTotalWeight()) * 100;
                                }
                                recipe.setCaloriesPer100g(caloriesPer100g); // Update recipe object

                                recipeList.add(recipe); // Add the recipe to the temporary list
                            }

                            // Pass the fetched recipes to the listener
                            listener.onRecipesFetched(recipeList);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void fetchAllApprovedRecipes(OnRecipesFetchedListener listener) {
        db.collection("Recipes")
                .whereEqualTo("status", "Approved") // Filter to get only recipes with status "Approved"
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Recipe> recipeList = new ArrayList<>(); // Temporary list to store recipes

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Recipe recipe = document.toObject(Recipe.class);
                                recipe.setRecipe_id(document.getId());

                                // Calculate calories per 100g if total weight is available
                                double caloriesPer100g = recipe.getCaloriesPer100g();
                                if (recipe.getTotalWeight() > 0) {
                                    caloriesPer100g = (recipe.getCalories() / recipe.getTotalWeight()) * 100;
                                }
                                recipe.setCaloriesPer100g(caloriesPer100g); // Update recipe object

                                recipeList.add(recipe); // Add the recipe to the temporary list
                            }

                            // Pass the fetched recipes to the listener
                            listener.onRecipesFetched(recipeList);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                            listener.onRecipesFetched(new ArrayList<>()); // Pass an empty list in case of failure
                        }
                    }
                });
    }

    public void fetchRejectedRecipes(OnRecipesFetchedListener listener) {
        db.collection("Recipes")
                .whereEqualTo("status", "Rejected") // Filter to get only rejected recipes
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Recipe> recipeList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Recipe recipe = document.toObject(Recipe.class);
                                recipe.setRecipe_id(document.getId());

                                // Calculate calories per 100g if total weight is available
                                double caloriesPer100g = recipe.getCaloriesPer100g();
                                if (recipe.getTotalWeight() > 0) {
                                    caloriesPer100g = (recipe.getCalories() / recipe.getTotalWeight()) * 100;
                                }
                                recipe.setCaloriesPer100g(caloriesPer100g); // Update recipe object

                                recipeList.add(recipe); // Add the recipe to the list
                            }
                            // Pass the fetched recipes to the listener
                            listener.onRecipesFetched(recipeList);
                        } else {
                            Log.w("Firestore", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}