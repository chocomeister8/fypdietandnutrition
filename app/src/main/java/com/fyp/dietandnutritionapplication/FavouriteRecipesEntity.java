package com.fyp.dietandnutritionapplication;

import android.util.Log;
import android.widget.Toast;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavouriteRecipesEntity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private ArrayList<Recipe> favoriteRecipes;
    ArrayList<Recipe> recipes = new ArrayList<>();
    public FavouriteRecipesEntity(){

    }

    public void saveRecipeToFirestore(Recipe recipe, Context context) {
        FirebaseUser currentUser = auth.getCurrentUser();

        // Ensure user is logged in before saving the recipe
        if (currentUser != null) {
            String userId = currentUser.getUid(); // Get the logged-in user's ID
            String recipeLabel = recipe.getLabel(); // Get the label of the recipe

            // Check for existing recipe with the same label
            db.collection("FavouriteRecipes")
                    .whereEqualTo("userId", userId) // Match the user's saved recipes
                    .whereEqualTo("title", recipeLabel) // Match the title (label) of the recipe
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Recipe with the same label already exists
                            Toast.makeText(context, "This recipe is already in your favorites", Toast.LENGTH_SHORT).show();
                        } else {
                            // Recipe doesn't exist, proceed with saving it
                            Map<String, Object> recipeData = new HashMap<>();
                            recipeData.put("userId", userId); // Add the userId to the recipe data
                            recipeData.put("title", recipe.getLabel());
                            recipeData.put("calories", String.format("%.1f kcal", recipe.getCalories()));
                            recipeData.put("total_weight", String.format("%.1f g", recipe.getTotalWeight()));
                            recipeData.put("total_time", String.format("%d mins", recipe.getTotal_Time()));
                            recipeData.put("calories_per_100g", String.format("%.2f", recipe.getCaloriesPer100g()));
                            recipeData.put("meal_type", String.join(", ", recipe.getMealType()));
                            recipeData.put("cuisine_type", String.join(", ", recipe.getCuisineType()));
                            recipeData.put("dish_type", String.join(", ", recipe.getDishType()));
                            recipeData.put("diet_labels", String.join(", ", recipe.getDietLabels()));
                            recipeData.put("health_labels", String.join(", ", recipe.getHealthLabels()));
                            recipeData.put("image_url", recipe.getImage());
                            recipeData.put("instructions", recipe.getUrl());

                            // Format ingredients as a string
                            StringBuilder ingredientsBuilder = new StringBuilder();
                            for (String ingredient : recipe.getIngredientLines()) {
                                ingredientsBuilder.append("• ").append(ingredient).append("\n");
                            }
                            recipeData.put("ingredients", ingredientsBuilder.toString());

                            // Save the recipe data to Firestore under the "FavouriteRecipes" collection
                            db.collection("FavouriteRecipes")
                                    .add(recipeData) // This will generate a unique document ID automatically
                                    .addOnSuccessListener(documentReference -> {
                                        documentReference.update("timestamp", FieldValue.serverTimestamp());
                                        // Successfully added, and documentReference contains the generated ID
                                        String documentId = documentReference.getId();
                                        Toast.makeText(context, "Recipe saved to favorites", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Error occurred
                                        Toast.makeText(context, "Failed to save recipe", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Error occurred while checking for duplicate recipes
                        Toast.makeText(context, "Failed to check for duplicate recipe", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Handle the case where the user is not logged in
            Toast.makeText(context, "You need to log in to save recipes", Toast.LENGTH_SHORT).show();
        }
    }

    public void checkRecipeFavouriteStatus(Recipe recipe, Context context, OnRecipeCheckListener listener) {
        if (recipe == null || context == null) {
            Log.e("CheckFavStatus", "Recipe or context is null");
            return; // Handle null parameters appropriately
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get the current user's ID
        String recipeTitle = recipe.getLabel(); // Assuming your Recipe class has a method to get the ID

        Log.d("CheckFavStatus", "Checking favorite status for userId: " + userId + ", recipeId: " + recipeTitle);


        db.collection("FavouriteRecipes")
                .whereEqualTo("userId", userId) // Filter by user ID
                .whereEqualTo("title", recipeTitle) // Filter by recipe ID
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Recipe exists in favorites
                            Log.d("CheckFavStatus", "Recipe is a favorite.");
                            listener.onRecipeChecked(true); // Notify the listener that the recipe is a favorite
                        } else {
                            // Recipe does not exist in favorites
                            Log.d("CheckFavStatus", "Recipe is not a favorite.");
                            listener.onRecipeChecked(false); // Notify the listener that the recipe is not a favorite
                        }
                    } else {
                        // Handle any errors
                        Log.w("AddFavouriteRecipeController", "Error checking favorite status", task.getException());
                    }
                });
    }

    public interface OnRecipeCheckListener {
        void onRecipeChecked(boolean isFavorite);
    }

    public void removeRecipeFromFirestore(Recipe recipe, Context context) {
        FirebaseUser currentUser = auth.getCurrentUser();

        // Ensure user is logged in before removing the recipe
        if (currentUser != null) {
            String userId = currentUser.getUid(); // Get the logged-in user's ID
            String recipeLabel = recipe.getLabel(); // Get the label of the recipe

            // Query for the recipe in the "FavouriteRecipes" collection by userId and title
            db.collection("FavouriteRecipes")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("title", recipeLabel)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // If the recipe exists, delete it
                            queryDocumentSnapshots.getDocuments().forEach(documentSnapshot -> {
                                db.collection("FavouriteRecipes")
                                        .document(documentSnapshot.getId())
                                        .delete()
                                        .addOnSuccessListener(aVoid ->
                                                Toast.makeText(context, "Recipe removed from favorites", Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(e ->
                                                Toast.makeText(context, "Failed to remove recipe", Toast.LENGTH_SHORT).show());
                            });
                        } else {
                            // Recipe not found in favorites
                            Toast.makeText(context, "This recipe is not in your favorites", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "Failed to find recipe to remove", Toast.LENGTH_SHORT).show());
        } else {
            // Handle the case where the user is not logged in
            Toast.makeText(context, "You need to log in to remove recipes", Toast.LENGTH_SHORT).show();
        }
    }

    public void getRecipesFromFirestore(String userId, Context context, OnRecipesRetrievedListener listener) {
        // Check if userId is not null or empty
        if (userId != null && !userId.isEmpty()) {
            // Query Firestore to get recipes for the provided userId
            db.collection("FavouriteRecipes")
                    .whereEqualTo("userId", userId)  // Filter by input userId
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        recipes.clear();

                        // Loop through each document and convert it to a Recipe object
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            Recipe recipe = new Recipe();
                            recipe.setLabel(document.getString("title"));
                            recipe.setCalories(Double.parseDouble(document.getString("calories").replace(" kcal", "")));
                            recipe.setTotalWeight(Double.parseDouble(document.getString("total_weight").replace(" g", "")));
                            recipe.setTotal_Time(Integer.parseInt(document.getString("total_time").replace(" mins", "")));
                            recipe.setCaloriesPer100g(Double.parseDouble(document.getString("calories_per_100g")));
                            recipe.setMealType(Arrays.asList(document.getString("meal_type").split(", ")));
                            recipe.setCuisineType(Arrays.asList(document.getString("cuisine_type").split(", ")));
                            recipe.setDishType(Arrays.asList(document.getString("dish_type").split(", ")));
                            recipe.setDietLabels(Arrays.asList(document.getString("diet_labels").split(", ")));
                            recipe.setHealthLabels(Arrays.asList(document.getString("health_labels").split(", ")));
                            recipe.setImage(document.getString("image_url"));

                            String instructionsUrl = document.getString("instructions");
                            Log.d("FirestoreDebug", "Instructions URL: " + instructionsUrl); // Log the URL
                            recipe.setInstructions(instructionsUrl);

                            // Correct ingredients parsing
                            List<String> ingredients = Arrays.asList(document.getString("ingredients").split("\n"));
                            recipe.setIngredientLines(ingredients);

                            // Add the recipe to the list
                            recipes.add(recipe);
                        }

                        // Notify listener with the retrieved recipes
                        listener.onRecipesRetrieved(recipes);

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Failed to retrieve recipes", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(context, "Invalid user ID", Toast.LENGTH_SHORT).show();
        }
    }

    // Listener Interface to pass the results back
    public interface OnRecipesRetrievedListener {
        void onRecipesRetrieved(ArrayList<Recipe> recipes);
        void onError(Exception e);
    }



    public void getFavoriteRecipes(String userId, OnFavoriteRecipesRetrievedListener listener) {
        // Clear previous data
        favoriteRecipes.clear();

        // Create a query to get favorite recipes for the specified user ID
        Query query = db.collection("FavouriteRecipes")
                .whereEqualTo("userid", userId);

        // Listen for query results
        ListenerRegistration registration = (ListenerRegistration) query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                // Get the Recipe object from document data
                                Recipe recipe = document.toObject(Recipe.class);
                                favoriteRecipes.add(recipe);
                            }
                        }
                        // Notify the listener
                        listener.onFavoriteRecipesRetrieved(favoriteRecipes);
                    } else {
                        // Handle error
                        listener.onError(task.getException());
                    }
                });
    }

    public interface OnFavoriteRecipesRetrievedListener {
        void onFavoriteRecipesRetrieved(ArrayList<Recipe> recipes);
        void onError(Exception e);
    }
}
