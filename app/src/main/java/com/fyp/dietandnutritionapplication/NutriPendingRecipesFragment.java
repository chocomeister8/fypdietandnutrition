package com.fyp.dietandnutritionapplication;

import static androidx.fragment.app.FragmentManager.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NutriPendingRecipesFragment extends Fragment implements RecipeAdapter.OnRecipeClickListener {
    private FirebaseFirestore db;
    private RecyclerView recipesRecyclerView;
    private RecipeAdapter recipesAdapter;
    private List<Recipe> recipeList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nutri_pending_recipes_status, container, false);

        // Initialize Firestore and RecyclerView
        db = FirebaseFirestore.getInstance();
        recipesRecyclerView = view.findViewById(R.id.recipe_recycler_view);
        recipesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize adapter with an empty list and set it to the RecyclerView
        recipesAdapter = new RecipeAdapter(recipeList, this, true);
        recipesRecyclerView.setAdapter(recipesAdapter);


        // Fetch recipes from Firestore
        fetchPendingRecipes(); // Call method to fetch all recipes
        Log.d(TAG, "Recipe list size: " + recipeList.size());

        // Setup buttons
        setupNavigationButtons(view); // Move button setup to a separate method for cleanliness

        return view;
    }

    private void setupNavigationButtons(View view) {
        Button button_all_recipes = view.findViewById(R.id.button_all_recipes);
        Button button_approved_recipes = view.findViewById(R.id.button_approved);
        Button button_recipes_status = view.findViewById(R.id.button_recipes_status);
        Button button_rejected_recipes = view.findViewById(R.id.button_rejected);
        Button button_recommend_recipes = view.findViewById(R.id.recommendRecipes);

        // Set up button click listeners to navigate between fragments
        button_all_recipes.setOnClickListener(v -> navigateToFragment(new NutriAllRecipesFragment()));
        button_approved_recipes.setOnClickListener(v -> navigateToFragment(new NutriApprovedRecipesFragment()));
        button_recipes_status.setOnClickListener(v -> navigateToFragment(new NutriPendingRecipesFragment()));
        button_rejected_recipes.setOnClickListener(v -> navigateToFragment(new NutriRejectedRecipesFragment()));
        button_recommend_recipes.setOnClickListener(v -> navigateToFragment(new ViewAllUserToRecommendFragment()));
    }

    private void navigateToFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)  // Add to back stack to enable back navigation
                .commit();
    }
    private void fetchPendingRecipes() {
        NutriPendingRecipesController pendingRecipesController = new NutriPendingRecipesController();
        pendingRecipesController.fetchPendingRecipes(new NutriPendingRecipesController.RecipesFetchedCallback() {
            @Override
            public void onRecipesFetched(ArrayList<Recipe> fetchedRecipes) {
                recipeList.clear(); // Clear the existing list
                recipeList.addAll(fetchedRecipes); // Add fetched recipes to the list
                recipesAdapter.notifyDataSetChanged(); // Notify adapter of changes
            }
        });
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        String recipeId = recipe.getRecipe_id(); // Get the recipe ID

        // Create a new instance of NutriRecipeDetailsFragment
        NutriRecipeDetailsFragment recipeDetailsFragment = new NutriRecipeDetailsFragment();

        // Create a bundle to pass the recipe ID
        Bundle args = new Bundle();
        args.putString("recipeId", recipeId);

        // Set the arguments for NutriRecipeDetailsFragment
        recipeDetailsFragment.setArguments(args);

        // Navigate to NutriRecipeDetailsFragment
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, recipeDetailsFragment)
                .addToBackStack(null)  // Add to back stack to enable back navigation
                .commit();
    }

}
