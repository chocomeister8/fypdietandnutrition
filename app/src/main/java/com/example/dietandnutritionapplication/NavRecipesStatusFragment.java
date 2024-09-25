package com.example.dietandnutritionapplication;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NavRecipesStatusFragment extends Fragment implements RecipeAdapter.OnRecipeClickListener {

    private FirebaseFirestore db;
    private RecyclerView recipesRecyclerView;
    private RecipeAdapter recipesAdapter;
    private List<Recipe> recipeList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nav_recipes_status, container, false);

        // Initialize Firestore and RecyclerView
        db = FirebaseFirestore.getInstance();
        recipesRecyclerView = view.findViewById(R.id.recipe_status_recycle_view);
        recipesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize adapter with an empty list and set it to the RecyclerView
        recipesAdapter = new RecipeAdapter(recipeList, this, true);
        recipesRecyclerView.setAdapter(recipesAdapter);


        // Fetch recipes from Firestore
        fetchUserRecipes(); // Call method to fetch all recipes
        Log.d(TAG, "Recipe list size: " + recipeList.size());

        // Setup buttons
        setupNavigationButtons(view); // Move button setup to a separate method for cleanliness

        return view;
    }

    private void setupNavigationButtons(View view) {
        Button button_all_recipes = view.findViewById(R.id.button_all_recipes);
        Button button_vegetarian = view.findViewById(R.id.button_vegetarian);
        Button button_favourite = view.findViewById(R.id.button_favourite);
        Button button_personalise_recipes = view.findViewById(R.id.button_personalise);
        Button button_recipes_status = view.findViewById(R.id.button_recipes_status);
        Button button_calorie_goal = view.findViewById(R.id.button_calorie_goal);

        // Set up button click listeners to navigate between fragments
        button_all_recipes.setOnClickListener(v -> navigateToFragment(new NavAllRecipesFragment()));
        button_vegetarian.setOnClickListener(v -> navigateToFragment(new NavVegetarianRecipesFragment()));
        button_favourite.setOnClickListener(v -> navigateToFragment(new NavFavouriteRecipesFragment()));
        button_personalise_recipes.setOnClickListener(v -> navigateToFragment(new NavPersonaliseRecipesFragment()));
        button_recipes_status.setOnClickListener(v -> navigateToFragment(new NavRecipesStatusFragment()));
        button_calorie_goal.setOnClickListener(v -> navigateToFragment(new NavCalorieGoalFragment()));
    }

    private void navigateToFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)  // Add to back stack to enable back navigation
                .commit();
    }

    private void fetchUserRecipes() {
        String currentUserId = getCurrentUserId(); // Method to retrieve current user ID

        db.collection("Recipes")
                .whereEqualTo("userId", currentUserId) // Filter to get only the recipes by the current user
                .whereEqualTo("status", "Pending") // Filter to get only recipes with status "Pending"
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            recipeList.clear(); // Clear the list before adding new data
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Recipe recipe = document.toObject(Recipe.class);
                                recipe.setRecipe_id(document.getId());

                                // Calculate calories per 100g if total weight is available
                                double caloriesPer100g = recipe.getCaloriesPer100g();
                                if (recipe.getTotalWeight() > 0) {
                                    caloriesPer100g = (recipe.getCalories() / recipe.getTotalWeight()) * 100;
                                }
                                recipe.setCaloriesPer100g(caloriesPer100g); // Update recipe object

                                recipeList.add(recipe); // Add the recipe to the list
                            }
                            // Notify the adapter of data changes
                            recipesAdapter.notifyDataSetChanged();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        String recipeId = recipe.getRecipe_id(); // Assuming you have a method to get ID
        fetchUserRecipes(); // Call this method with the recipe ID
    }

    private String getCurrentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
