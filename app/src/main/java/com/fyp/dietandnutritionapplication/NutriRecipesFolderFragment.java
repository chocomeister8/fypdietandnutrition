package com.fyp.dietandnutritionapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class NutriRecipesFolderFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.nutri_all_recipes, container, false);

        // Initialize buttons using view.findViewById
        Button button_all_recipes = view.findViewById(R.id.button_all_recipes);
/*        Button button_pending_recipes = view.findViewById(R.id.button_pending_recipes);
        Button button_approved_recipes = view.findViewById(R.id.button_approved_recipes);*/

        // Set up button click listeners to navigate between fragments
        button_all_recipes.setOnClickListener(v -> {
            // Replace current fragment with NavAllRecipesFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NutriAllRecipesFragment())
                    .addToBackStack(null)
                    .commit();
        });

/*        button_pending_recipes.setOnClickListener(v -> {
            // Replace current fragment with NavVegetarianRecipesFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NutriPendingRecipesFragment())
                    .addToBackStack(null)
                    .commit();
        });

        button_approved_recipes.setOnClickListener(v -> {
            // Replace current fragment with NavFavouriteRecipesFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NutriApprovedRecipesFragment())
                    .addToBackStack(null)
                    .commit();
        });*/


        return view;
    }
}
