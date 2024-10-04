package com.example.dietandnutritionapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dietandnutritionapplication.R;
import com.example.dietandnutritionapplication.Recipe;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class NutriRecipeDetailsFragment extends Fragment {

    private FirebaseFirestore db;
    private TextView labelTextView, caloriesTextView, cuisineTypeTextView, dishTypeTextView,
            mealTypeTextView, recipeIdTextView, statusTextView, totalWeightTextView, totalTimeTextView, userIdTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nutri_recipe_details, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize TextViews
        labelTextView = view.findViewById(R.id.labelTextView);
        caloriesTextView = view.findViewById(R.id.caloriesTextView);
        cuisineTypeTextView = view.findViewById(R.id.cuisineTypeTextView);
        dishTypeTextView = view.findViewById(R.id.dishTypeTextView);
        mealTypeTextView = view.findViewById(R.id.mealTypeTextView);
        recipeIdTextView = view.findViewById(R.id.recipeIdTextView);
        statusTextView = view.findViewById(R.id.statusTextView);
        totalWeightTextView = view.findViewById(R.id.totalWeightTextView);
        totalTimeTextView = view.findViewById(R.id.totalTimeTextView);
        userIdTextView = view.findViewById(R.id.userIdTextView);

        // Retrieve the recipe ID from the arguments
        if (getArguments() != null) {
            String recipeId = getArguments().getString("recipeId");

            // Fetch the recipe details based on the recipe ID
            fetchRecipeDetails(recipeId);
        }

        return view;
    }

    private void fetchRecipeDetails(String recipeId) {
        db.collection("Recipes").document(recipeId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Recipe recipe = documentSnapshot.toObject(Recipe.class);

                        if (recipe != null) {
                            // Convert lists to comma-separated strings
                            String cuisineTypeStr = String.join(", ", recipe.getCuisineType());
                            String dishTypeStr = String.join(", ", recipe.getDishType());
                            String mealTypeStr = String.join(", ", recipe.getMealType());
                            Integer totalTimeValue = recipe.getTotal_Time();


                            // Set data to views
                            labelTextView.setText("Label: " + recipe.getLabel());
                            caloriesTextView.setText("Calories: " + recipe.getCalories());
                            cuisineTypeTextView.setText("Cuisine Type: " + cuisineTypeStr);
                            dishTypeTextView.setText("Dish Type: " + dishTypeStr);
                            mealTypeTextView.setText("Meal Type: " + mealTypeStr);
                            recipeIdTextView.setText("Recipe ID: " + recipe.getRecipe_id());
                            statusTextView.setText("Status: " + recipe.getStatus());
                            totalWeightTextView.setText("Total Weight: " + recipe.getTotalWeight() + "g");
                            totalTimeTextView.setText("Total Time: " + totalTimeValue + " minutes");
                            userIdTextView.setText("User ID: " + recipe.getuserId());
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }
}
