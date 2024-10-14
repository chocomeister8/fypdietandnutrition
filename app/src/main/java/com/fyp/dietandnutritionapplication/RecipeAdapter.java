package com.fyp.dietandnutritionapplication;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private List<Recipe> recipeList;
    private OnRecipeClickListener onRecipeClickListener;
    private boolean showStatus; // New parameter to control visibility of status

    // Interface for handling click events
    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    // Constructor to accept both recipe list, listener, and the showStatus flag
    public RecipeAdapter(List<Recipe> recipeList, OnRecipeClickListener listener, boolean showStatus) {
        this.recipeList = recipeList;
        this.onRecipeClickListener = listener;
        this.showStatus = showStatus; // Initialize the showStatus flag
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the recipe_item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        // Get the recipe at the current position
        Recipe recipe = recipeList.get(position);

        // Set the recipe title
        holder.titleTextView.setText(recipe.getLabel());

        String mealTypes = String.join(", ", recipe.getMealType());
        holder.mealTypeTextView.setText("Type: " + mealTypes);

        String cuisineTypes = String.join(", ", recipe.getCuisineType());
        holder.cuisineTypeTextView.setText("Cuisine: " + cuisineTypes);

        holder.caloriesper100gTextView.setText(String.format("Calories per 100g: %.1f", recipe.getCaloriesPer100g()));

        // Check if user_ID is available
        if (recipe.getuserId() != null && !recipe.getuserId().isEmpty()) {
            // Fetch and display the username dynamically based on user_ID
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Users").document(recipe.getuserId()).get()
                    .addOnSuccessListener(userDoc -> {
                        if (userDoc.exists()) {
                            String username = userDoc.getString("username");  // Retrieve the username
                            if (username != null && !username.isEmpty()) {
                                holder.usernameViewText.setText(String.format("Recipe by: %s", username));  // Set the username in the TextView
                            } else {
                                holder.usernameViewText.setText("Recipe by: Unknown user");  // Set default if username is empty
                            }
                        } else {
                            Log.d("RecipeAdapter", "No such user document for user ID: " + recipe.getuserId());
                            holder.usernameViewText.setText("Recipe by: Unknown user");  // Set a default value if the user is not found
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("RecipeAdapter", "Error fetching user", e);  // Log the error if there's an issue
                        holder.usernameViewText.setText("Error fetching user");  // Set an error message
                    });
        } else {
            Log.d("RecipeAdapter", "No user_ID for recipe: " + recipe.getLabel());
            holder.usernameViewText.setText("Recipe by: Online Sources");  // Handle case where user_ID is null or empty
        }

        // Conditionally show or hide the status
        if (showStatus) {
            holder.statusViewText.setText("Status: " + recipe.getStatus());
            holder.statusViewText.setVisibility(View.VISIBLE); // Show status
        } else {
            holder.statusViewText.setVisibility(View.GONE); // Hide status
        }

        // Load image with Picasso
        if (recipe.getImage() != null && !recipe.getImage().isEmpty()) {
            Picasso.get().load(recipe.getImage()).into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.recipe_image); // Use a placeholder image if no URL is available
        }

        // Set the click listener for the recipe item
        holder.itemView.setOnClickListener(v -> {
            if (onRecipeClickListener != null) {
                onRecipeClickListener.onRecipeClick(recipe); // Trigger the click callback with the selected recipe
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    // Method to update the recipe list and notify the adapter
    public void updateRecipeList(List<Recipe> newRecipeList) {
        this.recipeList = new ArrayList<>(newRecipeList); // Create a new list to avoid modifying the original list
        notifyDataSetChanged(); // Notify the adapter to refresh the views
    }


    // ViewHolder class to represent each recipe item view
    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, mealTypeTextView, cuisineTypeTextView, caloriesper100gTextView, weightTextView, statusViewText, usernameViewText;
        ImageView imageView;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            // Initialize the views
            titleTextView = itemView.findViewById(R.id.recipe_title);
            mealTypeTextView = itemView.findViewById(R.id.meal_type);
            cuisineTypeTextView = itemView.findViewById(R.id.cuisine_type);
            caloriesper100gTextView = itemView.findViewById(R.id.calories_per_100g);
            weightTextView = itemView.findViewById(R.id.recipe_weight); // Initialize the weight TextView
            imageView = itemView.findViewById(R.id.recipe_image);
            statusViewText = itemView.findViewById(R.id.status); // Initialize the status TextView
            usernameViewText = itemView.findViewById(R.id.userName);
        }
    }
}
