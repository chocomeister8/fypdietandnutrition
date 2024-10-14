package com.fyp.dietandnutritionapplication;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface EdamamApiService {
    @GET("api/food-database/v2/parser")
    Call<FoodResponse> parseFood(
            @Query("app_id") String appId,
            @Query("app_key") String appKey,
            @Query("ingr") String foodLabel
    );
}
