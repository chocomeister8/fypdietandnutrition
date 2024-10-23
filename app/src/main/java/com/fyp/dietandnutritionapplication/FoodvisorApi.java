package com.fyp.dietandnutritionapplication;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface FoodvisorApi {

    @Multipart
    @POST("analysis/")
    Call<ResponseBody> recognizeFood(
            @Header("Authorization") String apiKey,
            @Part MultipartBody.Part image
    );

    @GET("food/list/")
    Call<List<RecognizedIngredient>> getIngredientInfo(
            @Query("name") String ingredientName,
            @Header("Authorization") String apiKey
    );
}
