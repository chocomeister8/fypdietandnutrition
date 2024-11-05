package com.fyp.dietandnutritionapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebViewFragment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecipeDetailFragment extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private Recipe recipe;
    private FirebaseUser currentUser;
    private Button saveButton;
    private Button removeButton;
    private String favoriteRecipeId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_details, container, false);

        // Get the recipe from the bundle
        if (getArguments() != null) {
            recipe = getArguments().getParcelable("selected_recipe");
        }

        currentUser = auth.getCurrentUser();



        // Initialize views
        TextView titleTextView = view.findViewById(R.id.detail_recipe_title);
        TextView caloriesTextView = view.findViewById(R.id.detail_calories);
        TextView weightTextView = view.findViewById(R.id.detail_total_weight);
        TextView caloriesp100gTextView = view.findViewById(R.id.detail_calories_p100g);
        TextView mealTypeTextView = view.findViewById(R.id.detail_meal_type);
        TextView cuisineTypeTextView = view.findViewById(R.id.detail_cuisine_type);
        TextView dishTypeTextView = view.findViewById(R.id.detail_dish_type);
        TextView dietLabelsTextView = view.findViewById(R.id.detail_diet_labels);
        TextView ingredientsTextView = view.findViewById(R.id.detail_ingredients);
        ImageView imageView = view.findViewById(R.id.detail_image);
        Button backButton = view.findViewById(R.id.back_button);
        ImageButton saveButton = view.findViewById(R.id.addFavouriteButton);
        ImageButton removeButton = view.findViewById(R.id.removeFavouriteButton);
        Button addToFolderButton = view.findViewById(R.id.addToFolder);
        TextView healthLabelsTextView = view.findViewById(R.id.detail_health_labels);
        TextView viewMoreHealthLabels = view.findViewById(R.id.view_more_health_labels);
        TextView viewLessHealthLabels = view.findViewById(R.id.view_less_health_labels);
        TextView viewMoreIngredients = view.findViewById(R.id.view_more_ingredients);
        TextView viewLessIngredients = view.findViewById(R.id.view_less_ingredients);
        TextView instructionsLink = view.findViewById(R.id.detail_instructions_link);

        // Set recipe details
        if (recipe != null) {
            titleTextView.setText(recipe.getLabel());
            caloriesTextView.setText(String.format("%.1f kcal", recipe.getCalories()));
            weightTextView.setText(String.format("%.1f g", recipe.getTotalWeight()));
            caloriesp100gTextView.setText(String.format("%.2f", recipe.getCaloriesPer100g()));
            mealTypeTextView.setText(String.join(", ", recipe.getMealType()));
            cuisineTypeTextView.setText(String.join(", ", recipe.getCuisineType()));
            dishTypeTextView.setText(String.join(", ", recipe.getDishType()));
            dietLabelsTextView.setText(String.join(", ", recipe.getDietLabels()));
            healthLabelsTextView.setText(String.join(", ", recipe.getHealthLabels()));
            instructionsLink.setText(recipe.getUrl());


            Picasso.get().load(recipe.getImage()).into(imageView);

            // Format ingredients
            StringBuilder ingredientsBuilder = new StringBuilder();
            for (String ingredient : recipe.getIngredientLines()) {
                ingredientsBuilder.append("• ").append(ingredient).append("\n");
            }
            ingredientsTextView.setText(ingredientsBuilder.toString());

            List<String> healthLabels = recipe.getHealthLabels();
            if (healthLabels.size() > 5) {
                healthLabelsTextView.setText(formatAsBulletList(healthLabels.subList(0, 3)));
                viewMoreHealthLabels.setVisibility(View.VISIBLE); // Show "View More"
            } else {
                healthLabelsTextView.setText(formatAsBulletList(healthLabels));
                viewMoreHealthLabels.setVisibility(View.GONE); // Hide "View More"
            }

            // View More and View Less for health labels
            viewMoreHealthLabels.setOnClickListener(v -> {
                healthLabelsTextView.setText(formatAsBulletList(healthLabels));
                viewMoreHealthLabels.setVisibility(View.GONE);
                viewLessHealthLabels.setVisibility(View.VISIBLE);
            });

            viewLessHealthLabels.setOnClickListener(v -> {
                healthLabelsTextView.setText(formatAsBulletList(healthLabels.subList(0, 3)));
                viewLessHealthLabels.setVisibility(View.GONE);
                viewMoreHealthLabels.setVisibility(View.VISIBLE);
            });

            // Handle ingredients View More/View Less functionality
            List<String> ingredients = recipe.getIngredientLines();
            if (ingredients.size() > 3) {
                ingredientsTextView.setText(formatAsBulletList(ingredients.subList(0, 3)));
                viewMoreIngredients.setVisibility(View.VISIBLE); // Show "View More"
            } else {
                ingredientsTextView.setText(formatAsBulletList(ingredients));
                viewMoreIngredients.setVisibility(View.GONE); // Hide "View More"
            }

            viewMoreIngredients.setOnClickListener(v -> {
                ingredientsTextView.setText(formatAsBulletList(ingredients));
                viewMoreIngredients.setVisibility(View.GONE);
                viewLessIngredients.setVisibility(View.VISIBLE);
            });

            viewLessIngredients.setOnClickListener(v -> {
                ingredientsTextView.setText(formatAsBulletList(ingredients.subList(0, 3)));
                viewLessIngredients.setVisibility(View.GONE);
                viewMoreIngredients.setVisibility(View.VISIBLE);
            });

            instructionsLink.setText("View Instructions"); // Set fixed text for the link
            if (recipe != null) {
                Log.d("RecipeDetailsFragment", "Recipe found: " + recipe.getLabel());
                Log.d("RecipeDetailsFragment", "Instructions: " + (recipe.getUrl() != null ? recipe.getUrl() : "Instructions are null"));
                Log.d("RecipeDetailsFragment", "Instructions: " + (recipe.getInstructions() != null ? recipe.getInstructions() : "Instructions are null"));

            } else {
                Log.d("RecipeDetailsFragment", "No recipe found in bundle.");
            }
            instructionsLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("RecipeDetailsFragment", "Instructions link clicked"); // Logging click event

                    // Assuming recipe.getUrl() returns the instructions URL
                    String instructionsUrl = recipe.getUrl();
                    String favRecipeInstructions = recipe.getInstructions();

                    if (instructionsUrl != null && !instructionsUrl.isEmpty()) {
                        // Create an instance of the WebViewFragment
                        RecipeInstructionsFragment recipeInstructionsFragment = RecipeInstructionsFragment.newInstance(instructionsUrl);

                        // Replace the current fragment with the WebViewFragment
                        requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frame_layout, recipeInstructionsFragment) // Make sure 'frame_layout' is your container ID
                                .addToBackStack(null) // Allows back navigation
                                .commit();
                    }
                    else if (favRecipeInstructions != null && !favRecipeInstructions.isEmpty()) {
                        // Create an instance of the WebViewFragment
                        RecipeInstructionsFragment recipeInstructionsFragment = RecipeInstructionsFragment.newInstance(favRecipeInstructions);

                        // Replace the current fragment with the WebViewFragment
                        requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frame_layout, recipeInstructionsFragment) // Make sure 'frame_layout' is your container ID
                                .addToBackStack(null) // Allows back navigation
                                .commit();
                    }
                    else {
                        Toast.makeText(getActivity(), "No instructions available.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

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


    private String formatAsBulletList(List<String> items) {
        StringBuilder bulletList = new StringBuilder();
        for (String item : items) {
            bulletList.append("• ").append(item).append("\n");
        }
        return bulletList.toString();
    }
}
