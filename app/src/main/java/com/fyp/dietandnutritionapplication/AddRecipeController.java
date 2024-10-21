package com.fyp.dietandnutritionapplication;

import java.util.List;
import java.util.Map;

public class AddRecipeController {

    private RecipesEntity recipesEntity;

    public AddRecipeController() {
        // Initialize RecipesEntity
        recipesEntity = new RecipesEntity();
    }

    // Method to add a recipe to Firestore via RecipesEntity
    public void addRecipe(String recipeTitle, double calories, double weight, double totalTime,
                          List<String> mealTypes, List<String> cuisineTypes, List<String> dishTypes,
                          List<String> ingredientsList, List<String> recipeStepsList, String userId, String status) {
        // Create the Recipe data structure
        Recipe recipe = new Recipe();
        recipe.setLabel(recipeTitle);
        recipe.setCalories(calories);
        recipe.setTotalWeight(weight);
        recipe.setTotal_Time((int) totalTime);  // Assuming total time is an integer value
        recipe.setMealType(mealTypes);
        recipe.setCuisineType(cuisineTypes);
        recipe.setDishType(dishTypes);
        recipe.setIngredientLines(ingredientsList);
        recipe.setRecipeStepsLines(recipeStepsList);
        recipe.setUserId(userId);
        recipe.setStatus(status);

        // Delegate the operation to RecipesEntity
        recipesEntity.AddRecipe(recipe);
    }
}
