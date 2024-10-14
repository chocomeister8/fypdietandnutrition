package com.fyp.dietandnutritionapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {
    private List<MealRecord> mealList;

    public MealAdapter(List<MealRecord> mealList) {
        this.mealList = mealList;
    }

    @Override
    public MealViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meal_item, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MealViewHolder holder, int position) {
        MealRecord meal = mealList.get(position);
        holder.titleTextView.setText(meal.getMealName());
        holder.calorieTextView.setText(String.valueOf(meal.getCalories()));
        //holder.imageView.setImageBitmap(meal.getImage()); // Set image to ImageView
        holder.mealTypeTextView.setText(meal.getMealType());
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    public static class MealViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView calorieTextView;
        ImageView imageView;
        TextView mealTypeTextView;

        public MealViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            calorieTextView = itemView.findViewById(R.id.calorieTextView);
            imageView = itemView.findViewById(R.id.imageView);
            mealTypeTextView = itemView.findViewById(R.id.mealTypeTextView);
        }
    }
}
