package com.example.dietandnutritionapplication;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface EdamamApi {
    @GET("https://api.edamam.com/api/recipes/v2")
    Call<RecipeResponse> searchRecipes(
            @Query("q") String query,
            @Query("app_id") String app_id,
            @Query("app_key") String app_key,
            @Query("type") String type,
            @Query("health") String health,
            @Query("mealType") String mealType,
            @Query("dishType") String dishType
    );
}