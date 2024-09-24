package com.example.dietandnutritionapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private List<Recipe> recipeList;
    private OnRecipeClickListener onRecipeClickListener;

    // Interface for handling click events
    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    // Constructor to accept both recipe list and the listener
    public RecipeAdapter(List<Recipe> recipeList, OnRecipeClickListener listener) {
        this.recipeList = recipeList;
        this.onRecipeClickListener = listener;
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

        holder.caloriesper100gTextView.setText(String.format("Calories per 100g: %.2f", recipe.getCaloriesPer100g()));



        // Use Picasso to load the image
        Picasso.get().load(recipe.getImage()).into(holder.imageView);

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

    // ViewHolder class to represent each recipe item view
    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, mealTypeTextView, caloriesper100gTextView;
        ImageView imageView;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            // Initialize the views
            titleTextView = itemView.findViewById(R.id.recipe_title);
            mealTypeTextView = itemView.findViewById(R.id.meal_type);
            caloriesper100gTextView = itemView.findViewById(R.id.calories_per_100g);
            imageView = itemView.findViewById(R.id.recipe_image);
        }
    }
}
