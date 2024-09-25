package com.example.dietandnutritionapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class NavPersonaliseRecipesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.nav_personalise_recipes, container, false);

        // Initialize buttons using view.findViewById
        Button button_all_recipes = view.findViewById(R.id.button_all_recipes);
        Button button_vegetarian = view.findViewById(R.id.button_vegetarian);
        Button button_favourite = view.findViewById(R.id.button_favourite);
        Button button_personalise_recipes = view.findViewById(R.id.button_personalise);
        Button button_recipes_status = view.findViewById(R.id.button_recipes_status);
        Button button_recommendedRecipes = view.findViewById(R.id.button_recommendRecipes);

        // Set up button click listeners to navigate between fragments
        button_all_recipes.setOnClickListener(v -> {
            // Replace current fragment with NavAllRecipesFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NavAllRecipesFragment())
                    .addToBackStack(null)  // Add to back stack to enable back navigation
                    .commit();
        });

        button_vegetarian.setOnClickListener(v -> {
            // Replace current fragment with NavVegetarianRecipesFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NavVegetarianRecipesFragment())
                    .addToBackStack(null)
                    .commit();
        });

        button_favourite.setOnClickListener(v -> {
            // Replace current fragment with NavFavouriteRecipesFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NavFavouriteRecipesFragment())
                    .addToBackStack(null)
                    .commit();
        });

        button_personalise_recipes.setOnClickListener(v -> {
            // Replace current fragment with NavFavouriteRecipesFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NavPersonaliseRecipesFragment())
                    .addToBackStack(null)
                    .commit();
        });

        button_recipes_status.setOnClickListener(v -> {
            // Replace current fragment with NavRecipesStatusFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NavRecipesStatusFragment())
                    .addToBackStack(null)
                    .commit();
        });


        button_recommendedRecipes.setOnClickListener(v -> {
            // Replace current fragment with NavRecipesStatusFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NavRecommendedRecipesFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}
