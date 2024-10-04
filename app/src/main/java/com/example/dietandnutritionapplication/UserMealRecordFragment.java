package com.example.dietandnutritionapplication;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import android.Manifest;
import androidx.activity.result.ActivityResultLauncher;


import java.util.Map;

import retrofit2.Call;


public class UserMealRecordFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private TextView dateTextView;
    private Calendar calendar;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ActivityResultLauncher<Intent> takePictureLauncher;

    private double calorieLimit;
    private double remainingCalories;

    private TextView calorieLimitTextView;

    private LinearLayout breakfastImageContainer;
    private LinearLayout lunchImageContainer;
    private LinearLayout dinnerImageContainer;
    private LinearLayout snackImageContainer;

    private CardView cardViewBreakfast;
    private CardView cardViewLunch;
    private CardView cardViewDinner;
    private CardView cardViewSnack;

    private TextView breakfastTextView;
    private TextView lunchTextView;
    private TextView dinnerTextView;
    private TextView snackTextView;

    private ImageView mealOptionButton1;
    private TextView mealOptionButton2;
    private String selectedDateString;

    private TextView carbsTextView, proteinsTextView, fatsTextView, calorieLimitView, remainingCaloriesView;

    private String selectedMealType;

    private UserMealRecordController userMealRecordController;

    private int totalCarbs;
    private int totalProteins;
    private int totalFats;
    private double totalCalories;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_log, container, false);

        userMealRecordController = new UserMealRecordController();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        String userId = currentUser.getUid();

        dateTextView = view.findViewById(R.id.dateTextView);
        calorieLimitTextView = view.findViewById(R.id.progress_calorielimit);

        cardViewBreakfast = view.findViewById(R.id.breakfastCard);
        cardViewLunch = view.findViewById(R.id.lunchCard);
        cardViewDinner = view.findViewById(R.id.dinnerCard);
        cardViewSnack = view.findViewById(R.id.snackCard);

        breakfastTextView = view.findViewById(R.id.breakfastTextView);
        lunchTextView = view.findViewById(R.id.lunchTextView);
        dinnerTextView = view.findViewById(R.id.dinnerTextView);
        snackTextView = view.findViewById(R.id.snackTextView);

        carbsTextView = view.findViewById(R.id.carbohydrates_value);
        proteinsTextView = view.findViewById(R.id.proteins_value);
        fatsTextView = view.findViewById(R.id.fats_value);

        calorieLimitView = view.findViewById(R.id.progress_calorielimit);
        remainingCaloriesView = view.findViewById(R.id.progress_remainingcalorie);


        calendar = Calendar.getInstance();

        if (currentUser != null) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
            String selectedDateString = dateFormat.format(calendar.getTime());
            dateTextView.setText(selectedDateString);

            userMealRecordController.fetchUsernameAndCalorieLimit(userId, new MealRecord.OnUsernameAndCalorieLimitFetchedListener() {
                @Override
                public void onDataFetched(String username, double calorieLimit) {
                    if (username != null) {
                        // Fetch meals logged for the user
                        userMealRecordController.fetchMealsLogged(username, selectedDateString, new MealRecord.OnMealsFetchedListener() {
                            @Override
                            public void onMealsFetched(List<MealRecord> mealRecords) {
                                if (mealRecords != null && !mealRecords.isEmpty()) {
                                    updateMealLogUI(mealRecords, selectedDateString, userId);  // Call the method with fetched data
                                } else {
                                    Log.w("MealLogFragment", "No meal records found.");
                                }
                            }
                        });
                        String selectedDate = dateTextView.getText().toString();
                        userMealRecordController.calculateRemainingCalories(userId, selectedDate, new MealRecord.OnRemainingCaloriesCalculatedListener() {
                            @Override
                            public void onRemainingCaloriesCalculated(double calorieLimit, double remainingCalories) {
                                updateCalorieDisplay(calorieLimit, remainingCalories);
                            }
                        });
                        dateTextView.setOnClickListener(v -> showDatePickerDialog(username, userId));
                    } else {
                        Log.w("MealLogFragment", "Username not found, cannot fetch meals.");
                    }
                }
            });


            mealOptionButton1 = view.findViewById(R.id.camera_icon);
            mealOptionButton2 = view.findViewById(R.id.snap_photo_text);
            mealOptionButton1.setOnClickListener(v -> showMealOptionDialog(userId));
            mealOptionButton2.setOnClickListener(v -> showMealOptionDialog(userId));

        }
        else {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
        }

        return view;

    }

    private void openCameraWithMealType(String mealType) {
        selectedMealType = mealType;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), "No camera app found!", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap downscaleBitmap(Bitmap bitmap, int maxSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float aspectRatio = (float) width / height;

        if (width > height) {
            width = maxSize;
            height = Math.round(maxSize / aspectRatio);
        } else {
            height = maxSize;
            width = Math.round(maxSize * aspectRatio);
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true);
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

                        Bitmap scaledBitmap = downscaleBitmap(imageBitmap, 1024); // max size 1024

                        performImageRecognition(getCurrentUserId(), selectedMealType, scaledBitmap);
                    } else {
                        Log.e("Error", "Captured image bitmap is null.");
                        Toast.makeText(getActivity(), "Failed to capture image.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void performImageRecognition(String userId, String selectedMealType, Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);

        ImageLabelerOptions options =
                new ImageLabelerOptions.Builder()
                        .setConfidenceThreshold(0.8f)  // Set confidence threshold
                        .build();

        ImageLabeler labeler = ImageLabeling.getClient(options);

        labeler.process(image)
                .addOnSuccessListener(labels -> {
                    Log.d("ImageRecognition", "Number of labels recognized: " + labels.size());
                    if (labels.isEmpty()) {
                        Log.w("ImageRecognition", "No labels detected.");
                        return;
                    }

                    for (ImageLabel label : labels) {
                        Log.d("ImageRecognition", label.getText() + " - Confidence: " + label.getConfidence());
                        String labelText = label.getText();

                        String selectedDate = dateTextView.getText().toString();
                        if (isFoodLabel(labelText)) {
                            searchFoodInEdamam(userId, labelText, 100.00, "grams", selectedMealType, selectedDate);


                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ImageRecognition", "Error processing image", e);
                });
    }

    private boolean isFoodLabel(String label) {
        Log.d("FoodAPI", "Received label for checking: " + label);
        List<String> foodKeywords = Arrays.asList("apple", "banana", "pizza", "sushi", "noodle", "fruit", "vegetable", "dish");
        boolean isFood = foodKeywords.stream().anyMatch(keyword -> label.toLowerCase().contains(keyword));
        Log.d("FoodAPI", "Is food label? " + isFood + " for label: " + label);
        return isFood;
    }

    private void updateCalorieDisplay(double calorieLimit, double remainingCalories) {
        String formattedCalorieLimit = String.format("%.2f", calorieLimit);
        String formattedRemainingCalories = String.format("%.2f", remainingCalories);

        calorieLimitView.setText("Calorie Limit: " + formattedCalorieLimit);
        remainingCaloriesView.setText("Remaining: \n" + formattedRemainingCalories);
    }

    private void handleEnterManually(String userId, String selectedMealType) {
        // Create a dialog to enter meal details
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Enter Meal Details");

        // Inflate a custom view for the dialog
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.user_enter_meal, null);
        builder.setView(dialogView);

        // Find views in the custom layout
        EditText foodNameInput = dialogView.findViewById(R.id.food_name_input);
        EditText servingSizeInput = dialogView.findViewById(R.id.serving_size_input);
        Spinner servingUnitSpinner = dialogView.findViewById(R.id.serving_unit_spinner);

        // Setup the Spinner for serving units (e.g., grams, cups, pieces)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.serving_units_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        servingUnitSpinner.setAdapter(adapter);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String foodName = foodNameInput.getText().toString();
            Log.d("MealLogFragment", "Food Name Entered: " + foodName);
            // Ensure the serving size input is not empty before parsing
            String servingSizeStr = servingSizeInput.getText().toString();
            if (!servingSizeStr.isEmpty()) {
                double servingSize = Double.parseDouble(servingSizeStr); // Convert to double
                Log.d("MealLogFragment", "Serving Size Entered: " + servingSize);

                String servingUnit = servingUnitSpinner.getSelectedItem().toString();
                Log.d("MealLogFragment", "Serving Unit Selected: " + servingUnit);
                  Log.d("MealLogFragment", "Meal Type Selected: " + selectedMealType);

                  String selectedDate = dateTextView.getText().toString();
                // Call the function to search for the food and scale nutrients accordingly
                searchFoodInEdamam(userId, foodName, servingSize, servingUnit, selectedMealType, selectedDate);
            } else {
                // Handle empty serving size input (e.g., show an error)
                Toast.makeText(requireContext(), "Please enter a valid serving size", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    public void searchFoodInEdamam(String userId, String foodName, Double servingSize, String servingUnit, String selectedMealType, String selectedDate) {
        userMealRecordController.fetchUsernameAndCalorieLimit(userId, new MealRecord.OnUsernameAndCalorieLimitFetchedListener() {
            @Override
            public void onDataFetched(String username, double calorielimit) {
                if (username != null) {
                    String appId = "997e8d42";
                    String appKey = "4483ab153d93c4a64d6f156fcffa78ff";

                    EdamamApiService apiService = ApiClient.getRetrofitInstance().create(EdamamApiService.class);
                    Call<FoodResponse> call = apiService.parseFood(appId, appKey, foodName);
                    Log.d("FoodAPI", "API call initiated for food: " + foodName);

                    call.enqueue(new retrofit2.Callback<FoodResponse>() {
                        @Override
                        public void onResponse(Call<FoodResponse> call, retrofit2.Response<FoodResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                List<FoodResponse.Hint> hints = response.body().getHints();
                                Log.d("FoodAPI", "API call successful, hints received: " + hints.size());
                                if (!hints.isEmpty()) {
                                    FoodResponse.Food food = hints.get(0).getFood();
                                    String foodLabel = food.getLabel();
                                    double referenceCalories = food.getNutrients().getCalories();
                                    double referenceProtein = food.getNutrients().getProtein();
                                    double referenceFat = food.getNutrients().getFat();
                                    double referenceCarbohydrates = food.getNutrients().getCarbohydrates();

                                    Log.d("FoodAPI", "Food Label: " + foodLabel);
                                    Log.d("FoodAPI", "Reference Calories: " + referenceCalories);
                                    Log.d("FoodAPI", "Selected Date: " + selectedDate);

                                    double scaleFactor = servingSize / 100.0; // Assuming Edamam data is per 100 grams

                                    double adjustedCalories = referenceCalories * scaleFactor;
                                    double adjustedProtein = referenceProtein * scaleFactor;
                                    double adjustedFat = referenceFat * scaleFactor;
                                    double adjustedCarbohydrates = referenceCarbohydrates * scaleFactor;
                                    String servingInfo = servingSize + " " + servingUnit;

                                    Log.d("FoodAPI", "Adjusted Calories: " + adjustedCalories);
                                    userMealRecordController.storeMealData(userId, username, foodLabel, selectedMealType, servingInfo, adjustedCalories, adjustedCarbohydrates, adjustedProtein, adjustedFat, selectedDate);
                                    userMealRecordController.fetchMealsLogged(username, selectedDateString, new MealRecord.OnMealsFetchedListener() {
                                        @Override
                                        public void onMealsFetched(List<MealRecord> mealRecords) {
                                            if (mealRecords != null && !mealRecords.isEmpty()) {
                                                userMealRecordController.calculateRemainingCalories(userId, selectedDate, new MealRecord.OnRemainingCaloriesCalculatedListener() {
                                                    @Override
                                                    public void onRemainingCaloriesCalculated(double calorieLimit, double remainingCalories) {
                                                        updateCalorieDisplay(calorieLimit, remainingCalories);
                                                        updateMealLogUI(mealRecords, selectedDateString, userId);
                                                    }
                                                });

                                            } else {
                                                Log.w("MealLogFragment", "No meal records found.");
                                            }
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<FoodResponse> call, Throwable t) {
                            Log.e("FoodAPI", "Error fetching food data", t);
                        }
                    });
                } else {
                    Log.e("FoodAPI", "Username not fetched or doesn't exist");
                    // Handle the error case, such as showing a message to the user
                }
            }
        });

    }

    private void updateMealLogUI(List<MealRecord> mealRecords, String selectedDateString, String userId) {
        Log.d("MealLogFragment", "Selected Date: " + selectedDateString);


                totalCarbs = 0;
                totalProteins = 0;
                totalFats = 0;
                StringBuilder breakfastBuilder = new StringBuilder();
                StringBuilder lunchBuilder = new StringBuilder();
                StringBuilder dinnerBuilder = new StringBuilder();
                StringBuilder snackBuilder = new StringBuilder();

                // Clear previous meal entries (optional)
                breakfastTextView.setText("");
                lunchTextView.setText("");
                dinnerTextView.setText("");
                snackTextView.setText("");
                Log.d("MealLogFragment", "Meal Records size: " + mealRecords.size());

                userMealRecordController.fetchUsernameAndCalorieLimit(userId, new MealRecord.OnUsernameAndCalorieLimitFetchedListener() {
            @Override
            public void onDataFetched(String username, double calorieLimit) {
                for (MealRecord mealRecord : mealRecords) {
                    String mealName = mealRecord.getMealName();
                    double mealCalories = mealRecord.getCalories();
                    double carbs = mealRecord.getCarbs();
                    double proteins = mealRecord.getProteins();
                    double fats = mealRecord.getFats();
                    String servingsize = mealRecord.getServingSize();

                    totalCarbs += carbs;
                    totalProteins += proteins;
                    totalFats += fats;

                    // Log meal details
                    Log.d("MealLogFragment", "Added meal: " + mealName + ", Calories: " + mealCalories + ", Carbs: " + carbs + ", Proteins: " + proteins + ", Fats: " + fats);

                    // Format the meal entry
                    DecimalFormat decimalFormat = new DecimalFormat("#");
                    String formattedCalories = decimalFormat.format(mealCalories);
                    String mealEntry = mealName + " - " + servingsize + " - " + formattedCalories + " Cal\n";  // New line for better formatting

                    // Append meal entry to the appropriate StringBuilder based on meal type
                    String mealType = mealRecord.getMealType();
                    switch (mealType.toLowerCase()) {
                        case "breakfast":
                            breakfastBuilder.append(mealEntry);
                            cardViewBreakfast.setVisibility(View.VISIBLE);
                            break;
                        case "lunch":
                            lunchBuilder.append(mealEntry);
                            cardViewLunch.setVisibility(View.VISIBLE);
                            break;
                        case "dinner":
                            dinnerBuilder.append(mealEntry);
                            cardViewDinner.setVisibility(View.VISIBLE);
                            break;
                        case "snack":
                            snackBuilder.append(mealEntry);
                            cardViewSnack.setVisibility(View.VISIBLE);
                            break;
                        default:
                            Log.e("MealLogFragment", "Unknown meal type: " + mealType);
                    }
                }

                // Set the text for each TextView after processing all meal records
                breakfastTextView.setText(breakfastBuilder.toString());
                lunchTextView.setText(lunchBuilder.toString());
                dinnerTextView.setText(dinnerBuilder.toString());
                snackTextView.setText(snackBuilder.toString());

                if (carbsTextView != null) carbsTextView.setText(totalCarbs + "g");
                Log.d("MealLogFragment", "Total Carbs: " + totalCarbs + "g");
                if (proteinsTextView != null) proteinsTextView.setText(totalProteins + "g");
                Log.d("MealLogFragment", "Total Proteins: " + totalProteins + "g");
                if (fatsTextView != null) fatsTextView.setText(totalFats + "g");
                Log.d("MealLogFragment", "Total Fats: " + totalFats + "g");

                Log.d("MealLogFragment", "Finished updating meal log UI for user: " + userId);

            }
        });
    }


    private void clearMealLogUI() {
        // Reset the totals
        totalCarbs = 0;
        totalProteins = 0;
        totalFats = 0;

        // Clear text fields and hide card views
        breakfastTextView.setText("");
        lunchTextView.setText("");
        dinnerTextView.setText("");
        snackTextView.setText("");

        // Hide the card views since no meals exist for this date
        cardViewBreakfast.setVisibility(View.GONE);
        cardViewLunch.setVisibility(View.GONE);
        cardViewDinner.setVisibility(View.GONE);
        cardViewSnack.setVisibility(View.GONE);

        // Optionally update the totals view to show zeros or an empty state
        carbsTextView.setText("0g");
        proteinsTextView.setText("0g");
        fatsTextView.setText("0g");
    }

    private void showMealOptionDialog(String userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.meal_log_dialog_meal_type_selection, null);

        Spinner mealTypeSpinner = dialogView.findViewById(R.id.mealTypeSpinner);

        // Populate the spinner with meal types (same as earlier setup)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.meal_types_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealTypeSpinner.setAdapter(adapter);

        // Add the custom view to the dialog
        builder.setView(dialogView);
        builder.setTitle("Choose a Meal Type and Option");

        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get the selected meal type from the spinner
                String selectedMealType = mealTypeSpinner.getSelectedItem().toString();

                // Validate that a meal type has been selected
                if (selectedMealType == null || selectedMealType.isEmpty()) {
                    Toast.makeText(requireContext(), "Please select a meal type!", Toast.LENGTH_SHORT).show();
                    return; // Stop if no meal type is selected
                }

                // Show the options dialog (Snap a Photo or Enter Manually)
                AlertDialog.Builder optionBuilder = new AlertDialog.Builder(requireContext());
                optionBuilder.setTitle("Choose an Option")
                        .setItems(new CharSequence[]{"Snap a Photo", "Enter Manually"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        // Handle snapping a photo
                                        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                                        } else {
                                            openCameraWithMealType(selectedMealType); // Pass the selected meal type
                                        }
                                        break;
                                    case 1:
                                        // Handle entering manually
                                        handleEnterManually(userId, selectedMealType); // Pass the selected meal type
                                        break;
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void updateDateTextView(Calendar calendar, String username, String userId) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = dateFormat.format(calendar.getTime());
        dateTextView.setText(formattedDate);

        clearMealLogUI();

        Log.d("MealLogFragment", "updateDateTextView Selected Date: " + formattedDate);
        userMealRecordController.fetchMealsLogged(username, formattedDate, new MealRecord.OnMealsFetchedListener() {
            @Override
            public void onMealsFetched(List<MealRecord> mealRecords) {
                if (mealRecords != null && !mealRecords.isEmpty()) {
                    Log.d("MealLogFragment", "updateDateTextView Fetched " + mealRecords.size() + " meals for date: " + formattedDate);

                    updateMealLogUI(mealRecords, formattedDate, userId);
                } else {
                    Log.w("MealLogFragment", "updateDateTextView No meal records found.");
                    clearMealLogUI();
                }

            }
        });
        userMealRecordController.calculateRemainingCalories(userId, formattedDate, new MealRecord.OnRemainingCaloriesCalculatedListener() {
            @Override
            public void onRemainingCaloriesCalculated(double calorieLimit, double remainingCalories) {
                updateCalorieDisplay(calorieLimit, remainingCalories);
            }
        });

    }

    private String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();  // Returns the current user's unique ID
        } else {
            // Handle the case where the user is not signed in
            Log.d("Auth", "No user is currently signed in.");
            return null; // Or handle appropriately (e.g., show an error message)
        }
    }

    private void showDatePickerDialog(String username, String userId) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    Log.d("DatePickerDialog", "Selected Date: " + dayOfMonth + "-" + (month + 1) + "-" + year);
                    updateDateTextView(calendar, username, userId);

                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }


}