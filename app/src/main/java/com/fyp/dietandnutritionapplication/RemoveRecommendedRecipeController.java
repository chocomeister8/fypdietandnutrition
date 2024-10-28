package com.fyp.dietandnutritionapplication;

public class RemoveRecommendedRecipeController {
    public RemoveRecommendedRecipeController(){

    }
    public void removeRecommendedRecipe(String username,String title){
        RecommendedRecipesEntity recommendedRecipesEntity = new RecommendedRecipesEntity();
        recommendedRecipesEntity.removeRecipeFromFirestore(username,title);
    }
}
