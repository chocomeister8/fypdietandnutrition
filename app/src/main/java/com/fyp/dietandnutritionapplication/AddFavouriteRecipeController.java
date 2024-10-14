package com.fyp.dietandnutritionapplication;

import android.content.Context;

public class AddFavouriteRecipeController {
    public AddFavouriteRecipeController(){

    }
    public void checkAddFavouriteRecipe(Recipe recipe, Context context){
        FavouriteRecipesEntity favouriteRecipesEntity = new FavouriteRecipesEntity();
        favouriteRecipesEntity.saveRecipeToFirestore(recipe,context);
    }

}
