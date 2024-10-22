package com.fyp.dietandnutritionapplication;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecommendedRecipesEntity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private ArrayList<Recipe> favoriteRecipes;
    ArrayList<Recipe> recipes = new ArrayList<>();

    public RecommendedRecipesEntity(){

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
