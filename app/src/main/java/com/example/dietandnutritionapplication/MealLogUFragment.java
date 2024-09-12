package com.example.dietandnutritionapplication;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.widget.ImageView;
import java.util.Calendar;
import java.util.TimeZone;

public class MealLogUFragment extends Fragment {

    private TextView calorieTextView;
    private TextView breakfastTextView;
    private TextView lunchTextView;
    private TextView dinnerTextView;
    private TextView snackTextView;
    private LinearLayout breakfastImageContainer;
    private LinearLayout lunchImageContainer;
    private LinearLayout dinnerImageContainer;
    private LinearLayout snackImageContainer;
    private ImageView cameraIcon;
    private TextView calorieLimitTextView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_log, container, false);

        // Initialize views
        breakfastTextView = view.findViewById(R.id.breakfastTextView);
        lunchTextView = view.findViewById(R.id.lunchTextView);
        dinnerTextView = view.findViewById(R.id.dinnerTextView);
        snackTextView = view.findViewById(R.id.snackTextView);

        breakfastImageContainer = view.findViewById(R.id.breakfastImageContainer);
        lunchImageContainer = view.findViewById(R.id.lunchImageContainer);
        dinnerImageContainer = view.findViewById(R.id.dinnerImageContainer);
        snackImageContainer = view.findViewById(R.id.snackImageContainer);
        calorieTextView = view.findViewById(R.id.progress_calorielimit);


        cameraIcon = view.findViewById(R.id.camera_icon);

        calorieLimitTextView = view.findViewById(R.id.progress_calorielimit);


        // Set a click listener on the camera icon
        cameraIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDummyMeal();
            }
        });

        return view;
    }


    private void addDummyMeal() {
        // Dummy meal data
        String mealName = "Chicken Chop";
        int mealCalories = 500;

        // Dummy total calorie count
        int totalCalories = 1500; // You can set this to any number you want for testing

        // Update calorieTextView with the dummy total calories
        calorieTextView.setText("Remaining:\n" + totalCalories + " Cal");

        // Get the current time
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        // Define the structure for each meal: a LinearLayout containing an image and text
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 10, 10, 10); // Add margins for spacing between elements

        // Create a new horizontal layout for each meal entry
        LinearLayout mealEntryLayout = new LinearLayout(getContext());
        mealEntryLayout.setOrientation(LinearLayout.HORIZONTAL); // Set it as horizontal

        // Create a new ImageView for the meal
        ImageView mealImageView = new ImageView(getContext());
        mealImageView.setImageResource(R.drawable.chicken_chop);
        mealImageView.setLayoutParams(new LinearLayout.LayoutParams(200, 200)); // Set size for the image
        mealEntryLayout.addView(mealImageView); // Add image to horizontal layout

        // Create a new TextView for the meal details
        TextView mealTextView = new TextView(getContext());
        mealTextView.setText(mealName + " - " + mealCalories + " Cal");
        mealTextView.setLayoutParams(layoutParams); // Apply layout parameters
        mealEntryLayout.addView(mealTextView); // Add text to horizontal layout

        if (hour >= 6 && hour < 12) {
            // Breakfast
            breakfastImageContainer.addView(mealEntryLayout);  // Add the image-text pair to breakfast container
        } else if (hour >= 12 && hour < 16) {
            // Lunch
            lunchImageContainer.addView(mealEntryLayout);  // Add the image-text pair to lunch container
        } else if (hour >= 18 && hour < 21) {
            // Dinner
            dinnerImageContainer.addView(mealEntryLayout);  // Add the image-text pair to dinner container
        } else {
            // Snacks
            snackImageContainer.addView(mealEntryLayout);  // Add the image-text pair to snack container
        }
    }
}