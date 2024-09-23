package com.example.dietandnutritionapplication;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import android.Manifest;
import androidx.activity.result.ActivityResultLauncher;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MealLogUFragment extends Fragment {

    private FirebaseFirestore db;

    private boolean isBreakfastExpanded = false;
    private boolean isLunchExpanded = false;
    private boolean isDinnerExpanded = false;
    private boolean isSnackExpanded = false;

    private int totalCarbs = 0;
    private int totalProteins = 0;
    private int totalFats = 0;

    private int totalCalories = 1500;
    private int remainingCalories = totalCalories;

    private TextView calorieTextView;
    private TextView carbsTextView, proteinsTextView, fatsTextView;
    private TextView nutritionInfoTextView;
    private LinearLayout breakfastImageContainer;
    private LinearLayout lunchImageContainer;
    private LinearLayout dinnerImageContainer;
    private LinearLayout snackImageContainer;
    private ImageView cameraIcon;
    private String username;

    private TextView dateTextView;
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private ActivityResultLauncher<Intent> takePictureLauncher;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_log, container, false);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Proceed with meal log logic
            db = FirebaseFirestore.getInstance();

            breakfastImageContainer = view.findViewById(R.id.breakfastImageContainer);
            lunchImageContainer = view.findViewById(R.id.lunchImageContainer);
            dinnerImageContainer = view.findViewById(R.id.dinnerImageContainer);
            snackImageContainer = view.findViewById(R.id.snackImageContainer);
            calorieTextView = view.findViewById(R.id.progress_calorielimit);

            // Initialize TextViews for macronutrients
            carbsTextView = view.findViewById(R.id.carbohydrates_value);
            proteinsTextView = view.findViewById(R.id.proteins_value);
            fatsTextView = view.findViewById(R.id.fats_value);

            dateTextView = view.findViewById(R.id.dateTextView);
            nutritionInfoTextView = view.findViewById(R.id.nutritionInfoTextView);
            // Set today's date by default
            updateDateTextView(calendar);

            dateTextView.setOnClickListener(v -> showDatePickerDialog());

            cameraIcon = view.findViewById(R.id.camera_icon);
            TextView snapPhotoText = view.findViewById(R.id.snap_photo_text);

            // Set a click listener on the camera icon
        /*cameraIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDummyMeal();
                updateCardViews();
            }
        });*/

            // Set a click listener on the camera icon
            cameraIcon.setOnClickListener(v -> showMealOptionDialog());

            // Set a click listener on the snap photo text
            snapPhotoText.setOnClickListener(v -> showMealOptionDialog());

        }
        else {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
        return view;

    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            Log.e("CameraIntent", "No camera apps installed.");
            Toast.makeText(getActivity(), "No camera app found!", Toast.LENGTH_SHORT).show();
            // display error state to the user
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    if (imageBitmap != null) {
                        Log.d("Debug", "Image captured successfully.");

                        // Downscale the bitmap if necessary
                        Bitmap scaledBitmap = downscaleBitmap(imageBitmap, 1024); // max size 1024

                        // Perform image recognition
                        performImageRecognition(scaledBitmap);
                    } else {
                        Log.e("Error", "Captured image bitmap is null.");
                        Toast.makeText(getActivity(), "Failed to capture image.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }


    private void performImageRecognition(Bitmap bitmap) {
        // Convert the Bitmap to InputImage
        InputImage image = InputImage.fromBitmap(bitmap, 0);

        // Create ImageLabeler with confidence threshold options
        ImageLabelerOptions options =
                new ImageLabelerOptions.Builder()
                        .setConfidenceThreshold(0.8f)  // Set confidence threshold
                        .build();

        // Initialize the labeler
        ImageLabeler labeler = com.google.mlkit.vision.label.ImageLabeling.getClient(options);

        labeler.process(image)
                .addOnSuccessListener(labels -> {
                    for (ImageLabel label : labels) {
                        String labelText = label.getText();
                        // Trigger the Nutritionix API call with the recognized label

                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                    e.printStackTrace();
                });
    }

    private Bitmap downscaleBitmap(Bitmap original, int maxSize) {
        int width = original.getWidth();
        int height = original.getHeight();
        if (width > maxSize || height > maxSize) {
            float scale = Math.min((float) maxSize / width, (float) maxSize / height);
            width = Math.round(scale * width);
            height = Math.round(scale * height);
            return Bitmap.createScaledBitmap(original, width, height, true);
        }
        return original;
    }

    private void saveImageUrlToFirestore(String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> mealRecord = new HashMap<>();
        mealRecord.put("foodItemImage", imageUrl); // Save the image URL

        db.collection("mealRecords").add(mealRecord)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Image URL saved successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error saving image URL", e);
                });
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
        int carbs = 30;
        int proteins = 40;
        int fats = 15;

        remainingCalories -= mealCalories;
        if (remainingCalories >= 0) {
            calorieTextView.setText("Remaining:\n" + remainingCalories + " Cal");
        } else {
            int exceededBy = Math.abs(remainingCalories);
            calorieTextView.setText("Exceeded by\n " + exceededBy + " Cal");
            calorieTextView.setTextSize(14);
        }
        totalCarbs += carbs;
        totalProteins += proteins;
        totalFats += fats;

        carbsTextView.setText(totalCarbs + "g");
        proteinsTextView.setText(totalProteins + "g");
        fatsTextView.setText(totalFats + "g");

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

    private void showMealOptionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Choose an Option")
                .setItems(new CharSequence[]{"Snap a Photo", "Enter Manually"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                // Handle snapping a photo
                                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                                } else {
                                    openCamera();
                                }
                                //handleEnterManually();
                                break;
                            case 1:
                                // Handle entering manually
                                handleEnterManually();
                                break;
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void handleEnterManually() {
        // Create a dialog to enter meal details
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Enter Meal Details");

        // Inflate a custom view for the dialog
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.user_enter_meal, null);
        builder.setView(dialogView);

        // Find views in the custom layout
        EditText foodNameInput = dialogView.findViewById(R.id.food_name_input);
        Spinner mealTypeSpinner = dialogView.findViewById(R.id.meal_type_spinner);
        EditText servingInput = dialogView.findViewById(R.id.serving_size_input);
        EditText caloriesInput = dialogView.findViewById(R.id.calories_input);

        // Setup the Spinner for meal types
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.meal_types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealTypeSpinner.setAdapter(adapter);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String foodName = foodNameInput.getText().toString();
            String mealType = mealTypeSpinner.getSelectedItem().toString();
            String servingSizeValue = servingInput.getText().toString();
            String calories = caloriesInput.getText().toString();

            addMealToLog(foodName, mealType, servingSizeValue, calories, username);
        });

        builder.setNegativeButton("Cancel", null);

        builder.create().show();
    }

    private void addMealToLog(String foodName, String mealType, String servingSizeValue, String calories, String userId) {
        if (foodName.isEmpty() || mealType.isEmpty() || calories.isEmpty()) {
            return;
        }

        int caloriesValue;
        try {
            caloriesValue = Integer.parseInt(calories);
        } catch (NumberFormatException e) {
            return;
        }

        // Create a Map to hold the meal data
        Map<String, Object> mealData = new HashMap<>();
        mealData.put("foodName", foodName);
        mealData.put("mealType", mealType);
        mealData.put("servingSize", servingSizeValue);
        mealData.put("carbs", null);
        mealData.put("proteins", null);
        mealData.put("fats", null);
        mealData.put("calories", caloriesValue);
        mealData.put("createdDate", dateFormat.format(calendar.getTime()));
        mealData.put("modifiedDate", null);
        mealData.put("username", username);

        db.collection("MealRecords")
                .add(mealData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("MealLogUFragment", "Meal added with ID: " + documentReference.getId());
                    Toast.makeText(getActivity(), "Meal added successfully", Toast.LENGTH_SHORT).show();
                    updateUIWithNewMeal(foodName, mealType, caloriesValue);
                })
                .addOnFailureListener(e -> {
                    Log.w("MealLogUFragment", "Error adding meal", e);
                });
    }

    private void updateUIWithNewMeal(String foodName, String mealType, int calories) {
        LinearLayout mealEntryLayout = new LinearLayout(getContext());
        mealEntryLayout.setOrientation(LinearLayout.HORIZONTAL);

        ImageView mealImageView = new ImageView(getContext());
        //mealImageView.setImageResource(R.drawable.placeholder_image); // Use a default or placeholder image
        mealImageView.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
        mealEntryLayout.addView(mealImageView);

        TextView mealTextView = new TextView(getContext());
        mealTextView.setText(foodName + " - " + calories + " Cal (" + mealType + ")");
        mealEntryLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        // Add the meal entry to the correct container based on meal type
        switch (mealType) {
            case "Breakfast":
                breakfastImageContainer.addView(mealEntryLayout);
                break;
            case "Lunch":
                lunchImageContainer.addView(mealEntryLayout);
                break;
            case "Dinner":
                dinnerImageContainer.addView(mealEntryLayout);
                break;
            case "Snack":
                snackImageContainer.addView(mealEntryLayout);
                break;
        }
    }

}