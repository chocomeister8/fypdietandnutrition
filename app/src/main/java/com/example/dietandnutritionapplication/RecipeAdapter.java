package com.example.dietandnutritionapplication;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dietandnutritionapplication.Recipe;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private List<Recipe> recipeList;

    public RecipeAdapter(List<Recipe> recipeList) {
        this.recipeList = recipeList;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.titleTextView.setText(recipe.getLabel()); // Adjust based on your Recipe class
        String mealTypes = String.join(", ", recipe.getMealType());
        holder.mealTypeTextView.setText(mealTypes); // Set the mealType text
        holder.caloriesTextView.setText(String.format("Calories: %.1f kcal", recipe.getCalories()));
        Picasso.get().load(recipe.getImage()).into(holder.imageView); // Adjust based on your Recipe class
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, mealTypeTextView, caloriesTextView;
        ImageView imageView;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.recipe_title);
            mealTypeTextView = itemView.findViewById(R.id.meal_type);
            caloriesTextView = itemView.findViewById(R.id.calories);
            imageView = itemView.findViewById(R.id.recipe_image);
        }
    }
}
