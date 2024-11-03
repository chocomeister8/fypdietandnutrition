package com.fyp.dietandnutritionapplication;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AddFavouriteRecipeController {
    private FavouriteRecipesEntity favouriteRecipesEntity;

    public AddFavouriteRecipeController(){
        this.favouriteRecipesEntity = new FavouriteRecipesEntity();
    }
    public void checkAddFavouriteRecipe(Recipe recipe, Context context){
        FavouriteRecipesEntity favouriteRecipesEntity = new FavouriteRecipesEntity();
        favouriteRecipesEntity.saveRecipeToFirestore(recipe,context);
    }

    public void checkRecipeFavouriteStatus(Recipe recipe, Context context, FavouriteRecipesEntity.OnRecipeCheckListener listener) {
        // Call the method from FavouriteRecipesEntity and pass the listener
        favouriteRecipesEntity.checkRecipeFavouriteStatus(recipe, context, new FavouriteRecipesEntity.OnRecipeCheckListener() {
            @Override
            public void onRecipeChecked(boolean isFavorite) {
                // Forward the result to the original listener
                listener.onRecipeChecked(isFavorite);
            }
        });
    }

    public void removeFavouriteRecipe(Recipe recipe, Context context){
        FavouriteRecipesEntity favouriteRecipesEntity = new FavouriteRecipesEntity();
        favouriteRecipesEntity.removeRecipeFromFirestore(recipe,context);
    }
}
