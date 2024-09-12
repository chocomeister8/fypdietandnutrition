package com.example.dietandnutritionapplication;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;


public class MealLogUFragment extends Fragment {

    private TextView calorieTextView;
    private LinearLayout breakfastImageContainer;
    private LinearLayout lunchImageContainer;
    private LinearLayout dinnerImageContainer;
    private LinearLayout snackImageContainer;
    private ImageView cameraIcon;


    private TextView dateTextView;
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_log, container, false);

        breakfastImageContainer = view.findViewById(R.id.breakfastImageContainer);
        lunchImageContainer = view.findViewById(R.id.lunchImageContainer);
        dinnerImageContainer = view.findViewById(R.id.dinnerImageContainer);
        snackImageContainer = view.findViewById(R.id.snackImageContainer);
        calorieTextView = view.findViewById(R.id.progress_calorielimit);

        dateTextView = view.findViewById(R.id.dateTextView);

        // Set today's date by default
        updateDateTextView(calendar);

        dateTextView.setOnClickListener(v -> showDatePickerDialog());


        CardView cardViewBreakfast = view.findViewById(R.id.breakfastCard);
        CardView cardViewLunch = view.findViewById(R.id.lunchCard);
        CardView cardViewDinner = view.findViewById(R.id.dinnerCard);
        CardView cardViewSnack = view.findViewById(R.id.snackCard);

        cameraIcon = view.findViewById(R.id.camera_icon);

        // Set a click listener on the camera icon
        cameraIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDummyMeal();
                updateCardViews();
            }
        });

        return view;
    }

    private void updateCardViews() {
        // Get the current time
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        CardView cardViewBreakfast = getView().findViewById(R.id.breakfastCard);
        CardView cardViewLunch = getView().findViewById(R.id.lunchCard);
        CardView cardViewDinner = getView().findViewById(R.id.dinnerCard);
        CardView cardViewSnack = getView().findViewById(R.id.snackCard);

        // Hide all CardViews initially
        cardViewBreakfast.setVisibility(View.GONE);
        cardViewLunch.setVisibility(View.GONE);
        cardViewDinner.setVisibility(View.GONE);
        cardViewSnack.setVisibility(View.GONE);

        // Show the CardView based on the current time
        if (hour >= 6 && hour < 12) {
            cardViewBreakfast.setVisibility(View.VISIBLE);
        } else if (hour >= 12 && hour < 15) {
            cardViewLunch.setVisibility(View.VISIBLE);
        } else if (hour >= 18 && hour < 21) {
            cardViewDinner.setVisibility(View.VISIBLE);
        } else {
            cardViewSnack.setVisibility(View.VISIBLE);
        }
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

        // Add the meal entry to the appropriate container
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

    private void showDatePickerDialog() {
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            updateDateTextView(calendar);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void updateDateTextView(Calendar calendar) {
        String formattedDate = dateFormat.format(calendar.getTime());
        if (isToday(calendar)) {
            dateTextView.setText("Today");
        } else {
            dateTextView.setText(formattedDate);
        }
    }

    private boolean isToday(Calendar calendar) {
        Calendar today = Calendar.getInstance();
        return calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                calendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH);
    }
}