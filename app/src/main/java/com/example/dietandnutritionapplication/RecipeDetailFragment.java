package com.example.dietandnutritionapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RecipeDetailFragment extends Fragment {

    private Recipe recipe;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_details, container, false);

        // Get the recipe from the bundle
        if (getArguments() != null) {
            recipe = getArguments().getParcelable("selected_recipe");
        }

        // Initialize views
        TextView titleTextView = view.findViewById(R.id.detail_recipe_title);
        TextView caloriesTextView = view.findViewById(R.id.detail_calories);
        TextView weightTextView = view.findViewById(R.id.detail_total_weight);
        TextView caloriesp100gTextView = view.findViewById(R.id.detail_calories_p100g);
        TextView totalTimeTextView = view.findViewById(R.id.detail_total_time);
        TextView mealTypeTextView = view.findViewById(R.id.detail_meal_type);
        TextView cuisineTypeTextView = view.findViewById(R.id.detail_cuisine_type);
        TextView dishTypeTextView = view.findViewById(R.id.detail_dish_type);
        TextView dietLabelsTextView = view.findViewById(R.id.detail_diet_labels);
        TextView ingredientsTextView = view.findViewById(R.id.detail_ingredients);
        ImageView imageView = view.findViewById(R.id.detail_image);
        Button backButton = view.findViewById(R.id.back_button);
        TextView healthLabelsTextView = view.findViewById(R.id.detail_health_labels);
        TextView viewMoreHealthLabels = view.findViewById(R.id.view_more_health_labels);
        TextView viewLessHealthLabels = view.findViewById(R.id.view_less_health_labels);
        TextView viewMoreIngredients = view.findViewById(R.id.view_more_ingredients);
        TextView viewLessIngredients = view.findViewById(R.id.view_less_ingredients);


        // Set recipe details
        if (recipe != null) {
            titleTextView.setText(recipe.getLabel());
            caloriesTextView.setText(String.format("%.1f kcal", recipe.getCalories()));
            weightTextView.setText(String.format("%.1f g", recipe.getTotalWeight())); // Set text
            totalTimeTextView.setText(String.format("%d mins", recipe.getTotalTime()));
            caloriesp100gTextView.setText(String.format("%.2f", recipe.getCaloriesPer100g()));
            mealTypeTextView.setText(String.join(", ", recipe.getMealType()));
            cuisineTypeTextView.setText(String.join(", ", recipe.getCuisineType()));
            dishTypeTextView.setText(String.join(", ", recipe.getDishType()));
            dietLabelsTextView.setText(String.join(", ", recipe.getDietLabels()));
            healthLabelsTextView.setText(String.join(", ", recipe.getHealthLabels()));

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
        }

        backButton.setOnClickListener(v -> {
            String source = getArguments().getString("source", "all");  // Default to "all" if no source is passed
            String searchQuery = getArguments().getString("search_query", "");
            int spinner1Value = getArguments().getInt("spinner1_value", 0);
            int spinner2Value = getArguments().getInt("spinner2_value", 0);

            if ("all".equals(source)) {
                // Pass the search and spinner values back to NavAllRecipesFragment
                NavAllRecipesFragment fragment = new NavAllRecipesFragment();
                Bundle args = new Bundle();
                args.putString("search_query", searchQuery);
                args.putInt("spinner1_value", spinner1Value);
                args.putInt("spinner2_value", spinner2Value);
                fragment.setArguments(args);

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, fragment)
                        .addToBackStack(null)
                        .commit();
            } else if ("vegetarian".equals(source)) {
                // Pass the search and spinner values back to NavVegetarianRecipesFragment
                NavVegetarianRecipesFragment fragment = new NavVegetarianRecipesFragment();
                Bundle args = new Bundle();
                args.putString("search_query", searchQuery);
                args.putInt("spinner1_value", spinner1Value);
                args.putInt("spinner2_value", spinner2Value);
                fragment.setArguments(args);

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    private String formatAsBulletList(List<String> items) {
        StringBuilder bulletList = new StringBuilder();
        for (String item : items) {
            bulletList.append("• ").append(item).append("\n");
        }
        return bulletList.toString();
    }

}
