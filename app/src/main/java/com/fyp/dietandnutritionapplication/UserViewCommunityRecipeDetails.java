package com.fyp.dietandnutritionapplication;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserViewCommunityRecipeDetails extends Fragment {

    private FirebaseFirestore db;
    private TextView labelTextView, caloriesTextView, cuisineTypeTextView, dishTypeTextView,
            mealTypeTextView, recipeIdTextView, statusTextView, totalWeightTextView, totalTimeTextView, userIdTextView, ingredientListTextView, instructionsTextView;

    private Button backButton, approveButton, rejectButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private TextView notificationBadgeTextView;
    private NotificationUController notificationUController;
    private Recipe recipe;

    private ImageButton saveButton, removeButton;
    private  Button addToFolderButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_community_recipe_details, container, false);

        if (getArguments() != null) {
            recipe = getArguments().getParcelable("selected_recipe");
        }

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize TextViews
        labelTextView = view.findViewById(R.id.detail_recipe_title);
        caloriesTextView = view.findViewById(R.id.detail_calories);
        dishTypeTextView = view.findViewById(R.id.detail_dish_type);
        mealTypeTextView = view.findViewById(R.id.detail_meal_type);
        totalWeightTextView = view.findViewById(R.id.detail_total_weight);
        totalTimeTextView = view.findViewById(R.id.detail_total_time);
        ingredientListTextView = view.findViewById(R.id.detail_ingredients);
        instructionsTextView = view.findViewById(R.id.detail_instructions);
        backButton = view.findViewById(R.id.back_button);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        saveButton = view.findViewById(R.id.addFavouriteButton);
        removeButton = view.findViewById(R.id.removeFavouriteButton);
        addToFolderButton = view.findViewById(R.id.addToFolder);

        if (currentUser != null) {
            String userId = currentUser.getUid();

            notificationBadgeTextView = view.findViewById(R.id.notificationBadgeTextView);

            notificationUController = new NotificationUController();
            notificationUController.fetchNotifications(userId, new Notification.OnNotificationsFetchedListener() {
                @Override
                public void onNotificationsFetched(List<Notification> notifications) {
                    // Notifications can be processed if needed

                    // After fetching notifications, count them
                    notificationUController.countNotifications(userId, new Notification.OnNotificationCountFetchedListener() {
                        @Override
                        public void onCountFetched(int count) {
                            if (count > 0) {
                                notificationBadgeTextView.setText(String.valueOf(count));
                                notificationBadgeTextView.setVisibility(View.VISIBLE);
                            } else {
                                notificationBadgeTextView.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            });

        }

        ImageView notiImage = view.findViewById(R.id.noti_icon);
        notiImage.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NotificationUFragment())
                    .addToBackStack(null)
                    .commit();

        });

        if (getArguments() != null) {
            String recipeId = getArguments().getString("recipeId");

            // Fetch the recipe details based on the recipe ID
            fetchRecipeDetails(recipeId);
        }

        backButton.setOnClickListener(v -> {
            String source = getArguments().getString("source", "all");  // Default to "all" if no source is passed
            String searchQuery = getArguments().getString("search_query", "");
            int spinner1Value = getArguments().getInt("spinner1_value", 0);
            int spinner2Value = getArguments().getInt("spinner2_value", 0);

            Fragment fragment;
            if ("all".equals(source)) {
                fragment = new NavAllRecipesFragment();
            } else if ("vegetarian".equals(source)) {
                fragment = new NavVegetarianRecipesFragment();
            } else if ("fav".equals(source)) {
                fragment = new ViewFavouriteRecipesFragment();

            } else if ("recommended".equals(source)){ // "recommended"
                fragment = new NavRecommendedRecipesFragment();
            }
            else{
                fragment = new userHomePageFragment();
            }

            Bundle args = new Bundle();
            args.putString("search_query", searchQuery);
            args.putInt("spinner1_value", spinner1Value);
            args.putInt("spinner2_value", spinner2Value);
            fragment.setArguments(args);

            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // Add to folder functionality
        addToFolderButton.setOnClickListener(v -> {
            // Create an instance of AddToFolderFragment
            AddToFolderFragment addToFolderFragment = new AddToFolderFragment(recipe);

            // Call the method to show the dialog
            addToFolderFragment.showAddToFolderDialog(getActivity());
        });

        AddFavouriteRecipeController addFavouriteRecipeController = new AddFavouriteRecipeController();

        // Call the method to check if the recipe is a favorite
        addFavouriteRecipeController.checkRecipeFavouriteStatus(recipe, getContext(), new FavouriteRecipesEntity.OnRecipeCheckListener() {
            @Override
            public void onRecipeChecked(boolean isFavorite) {
                if (isFavorite) {
                    // Recipe is a favorite
                    saveButton.setVisibility(View.GONE);
                    removeButton.setVisibility(View.VISIBLE);
                } else {
                    // Recipe is not a favorite
                    saveButton.setVisibility(View.VISIBLE);
                    removeButton.setVisibility(View.GONE);
                }
            }
        });

        saveButton.setOnClickListener(v -> {
            addRecipeToFavorites(getContext());
            saveButton.setVisibility(View.GONE);
            removeButton.setVisibility(View.VISIBLE);
        });

        removeButton.setOnClickListener(v -> {
            removeRecipeFromFavorites(getContext());
            removeButton.setVisibility(View.GONE);
            saveButton.setVisibility(View.VISIBLE);
        });

        return view;
    }

    private void addRecipeToFavorites(Context context) {
        AddFavouriteRecipeController addFavouriteRecipeController = new AddFavouriteRecipeController();
        addFavouriteRecipeController.checkAddFavouriteRecipe(recipe, context);
    }

    private void removeRecipeFromFavorites(Context context) {
        AddFavouriteRecipeController addFavouriteRecipeController = new AddFavouriteRecipeController();
        addFavouriteRecipeController.removeFavouriteRecipe(recipe, context);
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
                            labelTextView.setText(recipe.getLabel());
                            caloriesTextView.setText(String.valueOf(recipe.getCalories()));
                            dishTypeTextView.setText(dishTypeStr);
                            mealTypeTextView.setText(mealTypeStr);
                            totalWeightTextView.setText(String.valueOf(recipe.getTotalWeight()));
                            totalTimeTextView.setText(String.valueOf(totalTimeValue));
                            ingredientListTextView.setText(ingredientStr);
                            instructionsTextView.setText(stepsStr);


                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }


}
