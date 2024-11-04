package com.fyp.dietandnutritionapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NutriRecipeDetailsFragment extends Fragment {

    private FirebaseFirestore db;
    private TextView labelTextView, caloriesTextView, cuisineTypeTextView, dishTypeTextView,
            mealTypeTextView, recipeIdTextView, statusTextView, totalWeightTextView, totalTimeTextView, userIdTextView,  ingredientListTextView, instructionsTextView;

    private Button backButton, approveButton, rejectButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nutri_recipe_details, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize TextViews
        labelTextView = view.findViewById(R.id.labelTextView);
        caloriesTextView = view.findViewById(R.id.caloriesTextView);
        dishTypeTextView = view.findViewById(R.id.dishTypeTextView);
        mealTypeTextView = view.findViewById(R.id.mealTypeTextView);
        recipeIdTextView = view.findViewById(R.id.recipeIdTextView);
        statusTextView = view.findViewById(R.id.statusTextView);
        totalWeightTextView = view.findViewById(R.id.totalWeightTextView);
        totalTimeTextView = view.findViewById(R.id.totalTimeTextView);
        userIdTextView = view.findViewById(R.id.userIdTextView);
        ingredientListTextView = view.findViewById(R.id.detail_ingredients);
        instructionsTextView = view.findViewById(R.id.detail_instructions);
        backButton = view.findViewById(R.id.backButton);
        approveButton = view.findViewById(R.id.approveButton);
        rejectButton = view.findViewById(R.id.rejectButton);

        // Set an OnClickListener for the back button
        backButton.setOnClickListener(v -> {
            // Go back to the previous fragment
            getActivity().onBackPressed();
        });

        if (getArguments() != null) {
            String recipeId = getArguments().getString("recipeId");

            // Fetch the recipe details based on the recipe ID
            fetchRecipeDetails(recipeId);

            // Set Approve Button OnClickListener
            approveButton.setOnClickListener(v -> updateRecipeStatus(recipeId, "Approved"));

            // Set Reject Button OnClickListener
            rejectButton.setOnClickListener(v -> updateRecipeStatus(recipeId, "Rejected"));
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

                            String dishTypeStr = String.join(", ", recipe.getDishType());
                            String mealTypeStr = String.join(", ", recipe.getMealType());
                            Integer totalTimeValue = recipe.getTotal_Time();

                            if (documentSnapshot.contains("ingredientsList")) {
                                List<String> ingredientLines = (List<String>) documentSnapshot.get("ingredientsList");
                                recipe.setIngredientLines(ingredientLines);  // Use setter to set ingredientLines
                            }

                            if (documentSnapshot.contains("recipeStepsList")) {
                                List<String> recipeLines = (List<String>) documentSnapshot.get("recipeStepsList");
                                recipe.setRecipeStepsLines(recipeLines);  // Use setter to set ingredientLines
                            }

                            String ingredientStr = "";
                            if (recipe.getIngredientLines() != null) {
                                List<String> ingredients = recipe.getIngredientLines();
                                StringBuilder formattedIngredients = new StringBuilder();
                                for (int i = 0; i < ingredients.size(); i++) {
                                    formattedIngredients.append(ingredients.get(i)).append(" g");
                                    if (i < ingredients.size() - 1) {
                                        formattedIngredients.append("\n");  // Add a comma and space between ingredients
                                    }
                                }
                                ingredientStr = formattedIngredients.toString();
                            } else {
                                ingredientStr = "-";
                            }


                            String stepsStr = "";
                            if (recipe.getRecipeStepsLines() != null) {
                                List<String> steps = recipe.getRecipeStepsLines();
                                StringBuilder numberedSteps = new StringBuilder();
                                for (int i = 0; i < steps.size(); i++) {
                                    numberedSteps.append(i + 1).append(". ").append(steps.get(i));
                                    if (i < steps.size() - 1) {
                                        numberedSteps.append("\n");  // Add a space between steps
                                    }
                                }
                                stepsStr = numberedSteps.toString();
                            } else {
                                stepsStr = "No steps available";
                            }

                            // Set data to views
                            labelTextView.setText("Label: " + recipe.getLabel());
                            caloriesTextView.setText("Calories: " + recipe.getCalories());
                            dishTypeTextView.setText("Dish Type: " + dishTypeStr);
                            mealTypeTextView.setText("Meal Type: " + mealTypeStr);
                            recipeIdTextView.setText("Recipe ID: " + recipe.getRecipe_id());
                            statusTextView.setText("Status: " + recipe.getStatus());
                            totalWeightTextView.setText("Total Weight: " + recipe.getTotalWeight() + "g");
                            totalTimeTextView.setText("Total Time: " + totalTimeValue + " minutes");
                            ingredientListTextView.setText(ingredientStr);
                            instructionsTextView.setText(stepsStr);
                            userIdTextView.setText("User ID: " + recipe.getuserId());
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }

    private void updateRecipeStatus(String recipeId, String status) {

        db.collection("Recipes").document(recipeId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String userId = documentSnapshot.getString("userId");

                        db.collection("Recipes").document(recipeId)
                                .update("status", status)
                                .addOnSuccessListener(aVoid -> {

                                    // Successfully updated status
                                    Toast.makeText(getActivity(), "Recipe " + status, Toast.LENGTH_SHORT).show();
                                    statusTextView.setText("Status: " + status); // Update the UI
                                    sendNotification(userId, recipeId, status);
                                })
                                .addOnFailureListener(e -> {
                                    // Handle failure
                                    Toast.makeText(getActivity(), "Failed to update recipe status", Toast.LENGTH_SHORT).show();
                                });
                }
    })
            .addOnFailureListener(e -> {
        // Handle failure to fetch recipe
        Toast.makeText(getActivity(), "Failed to fetch recipe", Toast.LENGTH_SHORT).show();
    });
}


    private void sendNotification(String userId, String recipeId, String status) {

        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("userId", userId);

        if (status.equals("Approved")) {
            notificationData.put("message", "Your recipe (ID: " + recipeId + ") has been " + status + " by the nutritionist.");
        } else if (status.equals("Rejected")) {
            notificationData.put("message", "Your recipe (ID: " + recipeId + ") has been " + status + " by the nutritionist.");
        } else {
            notificationData.put("message", "Your recipe (ID: " + recipeId + ") is still pending.");
        }

        notificationData.put("type", "Recipe Status Update");
        notificationData.put("isRead", false);
        // Add timestamp for when the notification is sent
        Timestamp entryDateTime = new Timestamp(System.currentTimeMillis());
        notificationData.put("timestamp", entryDateTime);

        // Add notification data to Firestore
        db.collection("Notifications")
                .add(notificationData) // Use add() to create a new document
                .addOnSuccessListener(documentReference -> {
                    String notificationId = documentReference.getId(); // Get the document ID

                    // Now update the document to include the ID as a field
                    db.collection("Notifications").document(notificationId)
                            .update("notificationId", notificationId) // Store the document ID
                            .addOnSuccessListener(aVoid -> {
                                Log.d("Notification", "Notification added with ID: " + notificationId);
                            })
                            .addOnFailureListener(e -> {
                                Log.w("Notification", "Error updating notification ID", e);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("Notification", "Failed to send notification: " + e.getMessage());
                });
    }

}
