package com.fyp.dietandnutritionapplication;

import static java.lang.Double.parseDouble;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendedRecipesEntity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private ArrayList<Recipe> favoriteRecipes;
    ArrayList<Recipe> recipes = new ArrayList<>();
    private static final String INGREDIENT_DELIMITER = "• ";

    public RecommendedRecipesEntity(){

    }

    public void getRecommendedRecipesByUserId(String userId, Context context, OnRecommendedRecipesRetrievedListener listener) {
        // Step 1: Query the Users collection to get the username based on userId
        db.collection("Users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Retrieve the username from the Users collection
                        String username = documentSnapshot.getString("username");

                        // Step 2: Now query the RecommendedRecipes collection using the username
                        db.collection("RecommendedRecipes")
                                .whereEqualTo("username", username)  // Filter by the retrieved username
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    recipes.clear();
                                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                                        try {
                                            Recipe recipe = createRecipeFromDocument(document);
                                            recipes.add(recipe);
                                        } catch (Exception e) {
                                            listener.onError(e);  // Notify listener about the error
                                            Toast.makeText(context, "Error retrieving recipes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    listener.onRecipesRetrieved(recipes);
                                })
                                .addOnFailureListener(e -> {
                                    listener.onError(e);
                                    Toast.makeText(context, "Failed to retrieve recommended recipes", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to retrieve user information", Toast.LENGTH_SHORT).show();
                });
    }

    public interface OnRecommendedRecipesRetrievedListener {

        void onRecipesRetrieved(ArrayList<Recipe> recipes);
        void onError(Exception e);
    }
    private double safeParseDouble(String value) {
        try {
            return value != null ? parseDouble(value.replace(" kcal", "").trim()) : 0.0;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0.0; // Default value if parsing fails
        }
    }

    private Recipe createRecipeFromDocument(DocumentSnapshot document) throws Exception {
        Recipe recipe = new Recipe();
        recipe.setLabel(document.getString("title"));

        // Retrieve and safely parse the calorie value
        String caloriesString = document.getString("calories");
        recipe.setCalories(safeParseDouble(caloriesString));

        // Retrieve and safely parse total weight
        String totalWeightString = document.getString("total_weight");
        recipe.setTotalWeight(safeParseDouble(totalWeightString));

        // Retrieve total time and cast to int safely
        String totalTimeString = document.getString("total_time");
        int totalTime = 0;
        if (totalTimeString != null) {
            // Extract numeric part
            totalTimeString = totalTimeString.replaceAll("[^0-9]", ""); // Remove non-numeric characters
            if (!totalTimeString.isEmpty()) {
                totalTime = Integer.parseInt(totalTimeString); // Convert to int
            }
        }
        recipe.setTotal_Time(totalTime); // Default to 0 if null

        // Retrieve and safely parse calories per 100g
        String caloriesPer100gString = document.getString("calories_per_100g");
        recipe.setCaloriesPer100g(safeParseDouble(caloriesPer100gString));

        // Set other fields
        recipe.setMealType(Arrays.asList(document.getString("meal_type").split(", ")));
        recipe.setCuisineType(Arrays.asList(document.getString("cuisine_type").split(", ")));
        recipe.setDishType(Arrays.asList(document.getString("dish_type").split(", ")));
        recipe.setDietLabels(Arrays.asList(document.getString("diet_labels").split(", ")));
        recipe.setHealthLabels(Arrays.asList(document.getString("health_labels").split(", ")));
        recipe.setImage(document.getString("image_url"));

        // Correct ingredients parsing
        List<String> ingredients = Arrays.asList(document.getString("ingredients").split(INGREDIENT_DELIMITER));
        recipe.setIngredientLines(ingredients);

        String instructionsUrl = document.getString("instructions");
        Log.d("FirestoreDebug", "Instructions URL: " + instructionsUrl); // Log the URL
        recipe.setInstructions(instructionsUrl);

        return recipe;
    }

    public void saveRecipeToFirestore(Recipe recipe,String username, Context context) {
        FirebaseUser currentUser = auth.getCurrentUser();


        // Ensure user is logged in before saving the recipe
        if (currentUser != null) {
            String userId = currentUser.getUid();  // Get the logged-in user's ID

            // Create a map to hold the recipe data
            Map<String, Object> recipeData = new HashMap<>();
            recipeData.put("username", username);
            recipeData.put("recommendedBy", userId);  // Add the userId to the recipe data
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
            db.collection("RecommendedRecipes")
                    .add(recipeData) // This will generate a unique document ID automatically
                    .addOnSuccessListener(documentReference -> {
                        // Successfully added, and documentReference contains the generated ID
                        String documentId = documentReference.getId();
                        Toast.makeText(context, "Recipe Recommended", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Error occurred
                        Toast.makeText(context, "Failed to recommand", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Handle the case where the user is not logged in
            Toast.makeText(context, "You need to log in to recommand", Toast.LENGTH_SHORT).show();
        }
    }
}
