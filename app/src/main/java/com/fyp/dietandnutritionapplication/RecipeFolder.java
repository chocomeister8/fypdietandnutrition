package com.fyp.dietandnutritionapplication;

import com.google.firebase.firestore.PropertyName;

import java.util.List;

public class RecipeFolder {
    private String userId;
    private String folderName;
    private List<Recipe> recipes; // Assuming a folder can contain multiple recipes

    // Required empty constructor for Firestore
    public RecipeFolder() {}

    public RecipeFolder(String userId, String folderName, Recipe recipe) {
        this.userId = userId;
        this.folderName = folderName;
        this.recipes = List.of(recipe); // Add the initial recipe to the list
    }

    @PropertyName("userId")
    public String getUserId() {
        return userId;
    }

    @PropertyName("folderName")
    public String getFolderName() {
        return folderName;
    }

    @PropertyName("recipes")
    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    public void addRecipe(Recipe recipe) {
        this.recipes.add(recipe);
    }
}
