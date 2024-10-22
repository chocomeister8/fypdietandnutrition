package com.fyp.dietandnutritionapplication;

import android.content.Context;

public class RecommendRecipesController {
    public RecommendRecipesController(){

    }
    public void recommendRecipesToUser(Recipe recipe,String username, Context context){
        RecommendedRecipesEntity recommendedRecipesEntity = new RecommendedRecipesEntity();
        recommendedRecipesEntity.saveRecipeToFirestore(recipe,username,context);



    }
}
