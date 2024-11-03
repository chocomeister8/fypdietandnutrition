package com.fyp.dietandnutritionapplication;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.UUID;

public class MealLogPreviewFragment extends Fragment {

    private TextView dateTextView;
    private Calendar calendar;

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ActivityResultLauncher<Intent> takePictureLauncher;

    private double calorieLimit;
    private double remainingCalories;

    private TextView calorieLimitTextView;

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

    private int totalCarbs;
    private int totalProteins;
    private int totalFats;
    private double totalCalories;
    private double consumedCalories;


    private TextView carbsTextView, proteinsTextView, fatsTextView, calorieLimitView, remainingCaloriesView;

    private String selectedMealType;

    private UserMealRecordController userMealRecordController;
    private LinearLayout breakfastLinearLayout;
    private LinearLayout lunchLinearLayout;
    private LinearLayout dinnerLinearLayout;
    private LinearLayout snackLinearLayout;
    private static final int MAX_MEAL_RECORDS = 3;

    private static final int PICK_IMAGE_REQUEST = 1;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guest_meallogpreview, container, false);

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

        breakfastLinearLayout = view.findViewById(R.id.breakfastLinearLayout);
        lunchLinearLayout = view.findViewById(R.id.lunchLinearLayout);
        dinnerLinearLayout = view.findViewById(R.id.dinnerLinearLayout);
        snackLinearLayout = view.findViewById(R.id.snackLinearLayout);

        calendar = Calendar.getInstance();

        calorieLimitView = view.findViewById(R.id.progress_calorielimit);
        calorieLimit = 1500;
        calorieLimitView.setText("Calorie Limit: " + calorieLimit);

        remainingCaloriesView = view.findViewById(R.id.progress_remainingcalorie);
        totalCalories = 0;
        remainingCalories = calorieLimit - totalCalories;
        remainingCaloriesView.setText("Remaining: \n" + remainingCalories);
        updateDateTextView(calendar);

        sharedPreferences = getActivity().getSharedPreferences("MealPref", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        dateTextView.setOnClickListener(v -> showDatePickerDialog());

        clearMealLogUI();
        resetMealInsertionCount();
        initializeGuestUser(view);


        return view;
    }

    private void initializeGuestUser(View view) {
        clearMealLogUI();
        mealOptionButton1 = view.findViewById(R.id.camera_icon);
        mealOptionButton2 = view.findViewById(R.id.snap_photo_text);
        mealOptionButton1.setOnClickListener(v -> checkMealInsertLimit(null));
        mealOptionButton2.setOnClickListener(v -> checkMealInsertLimit(null));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, open the camera
                openCameraWithMealType(selectedMealType);
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(getContext(), "Camera permission is required to take photos.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {

                // First, handle camera data if present
                if (data != null && data.getExtras() != null) {
                    Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");

                    if (imageBitmap != null) {
                        String selectedMealType = this.selectedMealType;

                      sendImageToFoodvisor(null, imageBitmap, selectedMealType);

                    }
                }

                // If no camera data, check if the result is from gallery selection (URI)
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    try {
                        // Convert the selected image URI to a bitmap
                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImageUri);

                        if (imageBitmap != null) {

                            String selectedMealType = this.selectedMealType;

                            sendImageToFoodvisor(selectedImageUri.toString(), imageBitmap, selectedMealType);
                        }
                    } catch (IOException e) {
                        Log.e("DEBUG", "Error getting bitmap from URI", e);
                    }
                }
            }
        } else {
            Toast.makeText(getActivity(), "Operation canceled.", Toast.LENGTH_SHORT).show();
        }
    }


    public void clearMealLogUI() {
        // Reset the totals
        totalCarbs = 0;
        totalProteins = 0;
        totalFats = 0;

        breakfastTextView.setText("");
        lunchTextView.setText("");
        dinnerTextView.setText("");
        snackTextView.setText("");

        cardViewBreakfast.setVisibility(View.GONE);
        cardViewLunch.setVisibility(View.GONE);
        cardViewDinner.setVisibility(View.GONE);
        cardViewSnack.setVisibility(View.GONE);

        carbsTextView.setText("0g");
        proteinsTextView.setText("0g");
        fatsTextView.setText("0g");
    }

    public interface OnImageDownloadListener {
        void onImageDownloaded(Bitmap bitmap);
    }

    private void showDatePickerDialog() {
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            updateDateTextView(calendar);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                .show();
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

    private void openGalleryForImage(String mealType) {
        selectedMealType = mealType;
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void updateDateTextView(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));

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

    private void displayMealLogForGuest(String foodName, String servingSize, double calories, double carbs, double proteins, double fats, String imageUrl, String mealType, Bitmap imageBitmap) {
        totalCalories += calories;
        totalCarbs += carbs;
        totalProteins += proteins;
        totalFats += fats;

        LinearLayout mealEntryLayout = new LinearLayout(getContext());
        mealEntryLayout.setOrientation(LinearLayout.HORIZONTAL);
        mealEntryLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Create and set up the ImageView for the meal image
        ImageView mealImageView = new ImageView(getContext());
        mealImageView.setLayoutParams(new LinearLayout.LayoutParams(160, 160)); // Set image size
        mealImageView.setScaleType(ImageView.ScaleType.CENTER_CROP); // Set scale type
        mealImageView.setPadding(8, 0, 8, 16);

        if (imageBitmap != null) {

            Glide.with(getContext())
                    .load(imageBitmap)
                    .placeholder(R.drawable.foodimage)
                    .into(mealImageView);
        } else if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.foodimage)
                    .into(mealImageView);
        } else {
            mealImageView.setImageResource(R.drawable.foodimage);
        }

        // Add the image view to the meal entry layout
        mealEntryLayout.addView(mealImageView);

        // Create a vertical layout for text information (food name, serving size, calories)
        LinearLayout textLayout = new LinearLayout(getContext());
        textLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        textLayout.setLayoutParams(textParams);
        textLayout.setPadding(25, 0, 0, 0);

        // Set up the TextView for food name and serving size
        TextView titleTextView = new TextView(getContext());
        titleTextView.setText(foodName + " (" + servingSize + ")");
        titleTextView.setTextSize(16);
        textLayout.addView(titleTextView);

        // Set up the TextView for calories
        TextView caloriesTextView = new TextView(getContext());
        caloriesTextView.setText(String.format("%.2f Cal", calories));
        caloriesTextView.setTextSize(14);
        textLayout.addView(caloriesTextView);

        // Add the text layout to the meal entry layout
        mealEntryLayout.addView(textLayout);

        // Append the meal entry to the appropriate meal type layout (breakfast, lunch, dinner, or snack)
        switch (mealType.toLowerCase()) {
            case "breakfast":
                breakfastLinearLayout.addView(mealEntryLayout);
                cardViewBreakfast.setVisibility(View.VISIBLE);
                break;
            case "lunch":
                lunchLinearLayout.addView(mealEntryLayout);
                cardViewLunch.setVisibility(View.VISIBLE);
                break;
            case "dinner":
                dinnerLinearLayout.addView(mealEntryLayout);
                cardViewDinner.setVisibility(View.VISIBLE);
                break;
            case "snack":
                snackLinearLayout.addView(mealEntryLayout);
                cardViewSnack.setVisibility(View.VISIBLE);
                break;
            default:
                Log.e("MealLogGuest", "Unknown meal type: " + mealType);
        }
        updateGuestTotalsUI();
    }

    private void updateGuestTotalsUI() {
        Double calorielimit = 1500.00;
        calorieLimitView.setText("Calorie Limit: " + calorielimit);
        remainingCalories = calorielimit - totalCalories;

        String formattedCalorieLimit = String.format("%.2f", calorieLimit);
        String formattedRemainingCalories = String.format("%.2f", remainingCalories);

        calorieLimitView.setText("Calorie Limit: " + formattedCalorieLimit);
        remainingCaloriesView.setText("Remaining: \n" + formattedRemainingCalories);


        if (remainingCaloriesView != null) {
            remainingCaloriesView.setText("Remaining: \n" + formattedRemainingCalories);
        }

        if (carbsTextView != null) {
            carbsTextView.setText(totalCarbs + "g");
        }

        if (proteinsTextView != null) {
            proteinsTextView.setText(totalProteins + "g");
        }

        if (fatsTextView != null) {
            fatsTextView.setText(totalFats + "g");
        }
    }

    private void checkMealInsertLimit(String userId) {
        int mealCount = getMealInsertionCount();
        Log.d("DEBUG", "Current meal count: " + mealCount);

        if (mealCount >= MAX_MEAL_RECORDS) {
            showRegisterPrompt();
        } else {
            incrementMealInsertionCount();
            showMealOptionDialog(userId);
        }
    }

    private int getMealInsertionCount() {
        return sharedPreferences.getInt("mealCount", 0);
    }


    private void incrementMealInsertionCount() {
        int currentCount = getMealInsertionCount();
        editor.putInt("mealCount", currentCount + 1);
        editor.apply();
    }

    private void showRegisterPrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Limit Reached")
                .setMessage("You have reached the limit of 3 meal entries. Please register to add more.")
                .setPositiveButton("Register", (dialog, which) -> {
                    // Redirect to registration page
                    ((MainActivity) getActivity()).replaceFragment(new URegisterFragment());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void resetMealInsertionCount() {
        editor.putInt("mealCount", 0);
        editor.apply();
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
                        .setItems(new CharSequence[]{"Snap a Photo", "Upload from Gallery", "Enter Manually"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        // Handle snapping a photo
                                        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                                        } else {
                                            openCameraWithMealType(selectedMealType);
                                        }
                                        break;
                                    case 1:
                                        openGalleryForImage(selectedMealType);
                                        break;
                                    case 2:
                                        // Handle entering manually
                                        handleFoodNameInput(selectedMealType, null);
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

    private void handleFoodNameInput(String selectedMealType, String imageUrl) {
        // Prompt for Food Name
        AlertDialog.Builder foodNameBuilder = new AlertDialog.Builder(requireContext());
        foodNameBuilder.setTitle("Enter Food Name");

        // Inflate custom view for food name input
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View foodNameDialogView = inflater.inflate(R.layout.user_enter_food_name, null);
        foodNameBuilder.setView(foodNameDialogView);

        // Find food name input field
        EditText foodNameInput = foodNameDialogView.findViewById(R.id.food_name_input);

        foodNameBuilder.setPositiveButton("Next", (dialog, which) -> {
            String foodName = foodNameInput.getText().toString();
            if (!foodName.isEmpty()) {
                Log.d("MealLogFragment", "Food Name Entered: " + foodName);

                // After food name is entered, proceed to fetch serving units
                fetchFoodServingUnits(selectedMealType, foodName, imageUrl);
            } else {
                Toast.makeText(requireContext(), "Please enter a valid food name", Toast.LENGTH_SHORT).show();
            }
        });

        foodNameBuilder.setNegativeButton("Cancel", null);
        foodNameBuilder.create().show();
    }

    private void fetchFoodServingUnits(String selectedMealType, String foodName, String imageUrl) {
        EdamamApiService apiService = ApiClient.getRetrofitInstance().create(EdamamApiService.class);
        String appId = "997e8d42";
        String appKey = "4483ab153d93c4a64d6f156fcffa78ff";
        Call<FoodResponse> call = apiService.parseFood(appId, appKey, foodName);

        call.enqueue(new retrofit2.Callback<FoodResponse>() {
            @Override
            public void onResponse(Call<FoodResponse> call, retrofit2.Response<FoodResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<FoodResponse.Hint> hints = response.body().getHints();
                    if (hints != null && !hints.isEmpty()) {
                        // Get the Food object from the first hint
                        FoodResponse.Hint hint = hints.get(0);
                        FoodResponse.Food food = hint.getFood();
                        List<FoodResponse.Measure> measures = hint.getMeasures();
                        Log.d("MealLogFragment", "Measures: " + measures);
                        if (measures != null && !measures.isEmpty()) {
                            for (int j = 0; j < measures.size(); j++) {
                                FoodResponse.Measure measure = measures.get(j);
                                Log.d("MealLogFragment", "Measure " + j + ": ");
                                Log.d("MealLogFragment", "Measure Label: " + measure.getLabel());
                                Log.d("MealLogFragment", "Measure Weight: " + measure.getWeight());
                            }
                        }
                        // Check if measures are not null and populate the list of serving units
                        if (measures != null && !measures.isEmpty()) {
                            List<String> servingUnits = new ArrayList<>();
                            for (FoodResponse.Measure measure : measures) {
                                servingUnits.add(measure.getLabel()); // Add available serving units to the list
                            }
                            Log.d("MealLogFragment", "Available Serving Units: " + servingUnits.toString());

                            // Proceed to show dialog for selecting serving unit and entering size
                            handleServingInput(selectedMealType, foodName, servingUnits, imageUrl);
                        } else {
                            Toast.makeText(requireContext(), "No serving units found for: " + foodName, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), "No food hints found for: " + foodName, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("MealLogFragment", "API call unsuccessful, check response.");
                }
            }

            @Override
            public void onFailure(Call<FoodResponse> call, Throwable t) {
                Log.e("MealLogFragment", "Error fetching food data", t);
            }
        });
    }

    private void handleServingInput(String selectedMealType, String foodName, List<String> servingUnits, String imageURL) {
        // Prompt for Serving Unit and Size
        AlertDialog.Builder servingBuilder = new AlertDialog.Builder(requireContext());
        servingBuilder.setTitle("Enter Serving Details");

        // Inflate custom view for serving unit and size input
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View servingDialogView = inflater.inflate(R.layout.user_enter_serving, null);
        servingBuilder.setView(servingDialogView);

        // Find views in the custom layout
        Spinner servingUnitSpinner = servingDialogView.findViewById(R.id.serving_unit_spinner);
        EditText servingSizeInput = servingDialogView.findViewById(R.id.serving_size_input);

        // Populate the Spinner with dynamically fetched serving units
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, servingUnits);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        servingUnitSpinner.setAdapter(adapter);


        servingBuilder.setPositiveButton("Add", (dialog, which) -> {
            String selectedServingUnit = servingUnitSpinner.getSelectedItem().toString();
            String servingSizeStr = servingSizeInput.getText().toString();

            // Ensure the user enters a valid serving size
            if (!servingSizeStr.isEmpty()) {
                double servingSize = Double.parseDouble(servingSizeStr);

                Log.d("MealLogFragment", "Serving Size Entered: " + servingSize);
                Log.d("MealLogFragment", "Serving Unit Selected: " + selectedServingUnit);

                String selectedDate =  dateTextView.getText().toString();
                // Call the function to search for the food and scale nutrients accordingly
                searchFoodInEdamam(foodName, servingSize, selectedServingUnit, selectedMealType, selectedDate, false, "", imageURL);
            } else {
                Toast.makeText(requireContext(), "Please enter a valid serving size", Toast.LENGTH_SHORT).show();
            }
        });

        servingBuilder.setNegativeButton("Cancel", null);
        servingBuilder.create().show();
    }

    public void searchFoodInEdamam(String foodName, Double servingSize, String servingUnit, String selectedMealType, String selectedDate, boolean isUpdate, String mealRecordID, String imageUrl) {

        String appId = "997e8d42";
        String appKey = "4483ab153d93c4a64d6f156fcffa78ff";

        EdamamApiService apiService = ApiClient.getRetrofitInstance().create(EdamamApiService.class);
        Call<FoodResponse> call = apiService.parseFood(appId, appKey, foodName);
        Log.d("FoodAPI", "API call initiated for food: " + foodName);

        call.enqueue(new retrofit2.Callback<FoodResponse>() {
            @Override
            public void onResponse(Call<FoodResponse> call, retrofit2.Response<FoodResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("FoodAPI", "Full API Response: " + response.body().toString());

                    List<FoodResponse.Hint> hints = response.body().getHints();
                    if (hints != null && !hints.isEmpty()) {
                        FoodResponse.Hint hint = hints.get(0);
                        FoodResponse.Food food = hint.getFood();
                        if (food != null) {

                            List<FoodResponse.Measure> measures = hints.get(0).getMeasures();
                            if (measures != null && !measures.isEmpty()) {
                                for (FoodResponse.Measure measure : measures) {
                                    if (measure.getLabel().equalsIgnoreCase(servingUnit)) {
                                        double referenceWeight = measure.getWeight();
                                        if (referenceWeight == 0) {
                                            Log.e("FoodAPI", "Reference weight is zero, cannot scale nutrients.");
                                            return;
                                        }

                                        double scaleFactor = (servingSize * referenceWeight) / 100;

                                        // Extract and adjust nutrients
                                        FoodResponse.Nutrients nutrients = food.getNutrients();

                                        if (nutrients != null) {
                                            // Log all nutrient values from the API response
                                            Log.d("FoodAPI", "Nutrient values from Edamam:");
                                            Log.d("FoodAPI", "Calories: " + nutrients.getCalories());
                                            Log.d("FoodAPI", "Carbs: " + nutrients.getCarbohydrates());
                                            Log.d("FoodAPI", "Protein: " + nutrients.getProtein());
                                            Log.d("FoodAPI", "Fat: " + nutrients.getFat());
                                            Log.d("FoodAPI", "Fiber: " + nutrients.getFiber());

                                        }
                                        if (nutrients == null) {
                                            Log.e("FoodAPI", "Nutrients are missing from the API response.");
                                            return;
                                        }
                                        String imageURL = (imageUrl != null) ? imageUrl : food.getImage();
                                        if (imageURL == null) {
                                            Log.e("FoodDebug", "Image URL is null for food: " + food.getLabel());
                                        } else {
                                            Log.d("FoodDebug", "Image URL: " + imageURL);
                                        }
                                        double adjustedCalories = nutrients.getCalories() * scaleFactor;
                                        double adjustedProtein = nutrients.getProtein() * scaleFactor;
                                        double adjustedFat = nutrients.getFat() * scaleFactor;
                                        double adjustedCarbohydrates = nutrients.getCarbohydrates() * scaleFactor;
                                        double adjustedFiber = nutrients.getFiber() * scaleFactor;

                                        // Log all the nutrients for verification
                                        Log.d("FoodAPI", "Adjusted Calories: " + adjustedCalories);
                                        Log.d("FoodAPI", "Adjusted Protein: " + adjustedProtein);
                                        Log.d("FoodAPI", "Adjusted Fat: " + adjustedFat);
                                        Log.d("FoodAPI", "Adjusted Carbs: " + adjustedCarbohydrates);
                                        Log.d("FoodAPI", "Adjusted Fiber: " + adjustedFiber);

                                        displayMealLogForGuest(foodName, servingSize + " " + servingUnit, adjustedCalories, adjustedCarbohydrates, adjustedProtein, adjustedFat, imageURL, selectedMealType, null);
                                    }
                                }
                            } else {
                                Log.e("FoodAPI", "'measures' is null or empty for food: " + foodName);
                            }
                        } else {
                            Log.e("FoodAPI", "'food' object is null in the hint for food: " + foodName);
                        }
                    } else {
                        Log.e("FoodAPI", "'hints' is null or empty for food: " + foodName);
                    }
                } else {
                    Log.e("FoodAPI", "API response unsuccessful or empty.");
                }
            }

            @Override
            public void onFailure(Call<FoodResponse> call, Throwable t) {
                Log.e("FoodAPI", "Error fetching food data", t);
            }
        });


    }

    private void showConfirmationDialog(List<RecognizedIngredient> ingredients, String selectedMealType, Bitmap imageBitmap, String imageUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Review Ingredients");

        Set<String> uniqueIngredientNames = new HashSet<>();
        List<RecognizedIngredient> uniqueIngredients = new ArrayList<>();

        for (RecognizedIngredient ingredient : ingredients) {
            if (uniqueIngredientNames.add(ingredient.getDisplayName().toLowerCase())) {
                uniqueIngredients.add(ingredient);
            }
        }

        // Create a LinearLayout to hold the checkboxes
        LinearLayout mainLayout = new LinearLayout(requireContext());
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        TextView scrollReminderText = new TextView(requireContext());
        scrollReminderText.setText("Scroll to see more ingredients");
        scrollReminderText.setGravity(Gravity.CENTER);
        scrollReminderText.setTextColor(Color.DKGRAY);
        scrollReminderText.setPadding(0, 10, 0, 10);
        mainLayout.addView(scrollReminderText);

        ScrollView scrollView = new ScrollView(requireContext());
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                500
        ));


        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP,
                new int[]{Color.parseColor("#FFFFFF"), Color.parseColor("#DDFFFFFF")}
        );
        scrollView.setBackground(gradient);

        LinearLayout checkboxContainer = new LinearLayout(requireContext());
        checkboxContainer.setOrientation(LinearLayout.VERTICAL);

        // Create an array to store the user's selections
        final boolean[] checkedItems = new boolean[uniqueIngredients.size()];

        // Create checkboxes for each ingredient
        for (int i = 0; i < uniqueIngredients.size(); i++) {
            RecognizedIngredient ingredient = uniqueIngredients.get(i);
            CheckBox checkBox = new CheckBox(requireContext());
            checkBox.setText(ingredient.getDisplayName() + " - Calories: " + ingredient.getNutrition().getCalories() + " kcal");
            checkboxContainer.addView(checkBox);

            // Set the checkbox listener to track selections
            final int index = i;
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) ->
            {
                Log.d("DEBUG", "Checkbox for ingredient: " + ingredient.getDisplayName() + " checked: " + isChecked);
                checkedItems[index] = isChecked;
            });
        }
        scrollView.addView(checkboxContainer);
        mainLayout.addView(scrollView);


        TextView manualEntryLabel = new TextView(requireContext());
        manualEntryLabel.setText("Or Add a New Ingredient:");
        mainLayout.addView(manualEntryLabel);

        EditText manualIngredientName = new EditText(requireContext());
        manualIngredientName.setHint("Ingredient Name");
        mainLayout.addView(manualIngredientName);

        builder.setView(mainLayout);
        builder.setPositiveButton("Confirm", (dialog, which) -> {
            boolean hasSelectedIngredient = false;
            for (boolean isSelected : checkedItems) {
                if (isSelected) {
                    hasSelectedIngredient = true;
                    break;
                }
            }

            String newIngredientName = manualIngredientName.getText().toString().trim();

            if (!hasSelectedIngredient && newIngredientName.isEmpty()) {
                // Show toast if neither condition is met
                Toast.makeText(requireContext(), "Please select at least one ingredient or enter a new ingredient.", Toast.LENGTH_SHORT).show();
                return;
            }



            for (int i = 0; i <  uniqueIngredients.size(); i++) {
                if (checkedItems[i]) {
                    RecognizedIngredient ingredient = uniqueIngredients.get(i);
                    // Display or handle selected ingredient (replace displayMealLogForGuest with your logic)
                    displayMealLogForGuest(
                            ingredient.getDisplayName(),
                            "1 Serving",
                            ingredient.getNutrition().getCalories(),
                            ingredient.getNutrition().getCarbs(),
                            ingredient.getNutrition().getProteins(),
                            ingredient.getNutrition().getFats(),
                            imageUrl,
                            selectedMealType,
                            imageBitmap
                    );
                }
            }


            if (!newIngredientName.isEmpty()) {
                String selectedDate =  dateTextView.getText().toString();
                Log.d("DEBUG", "Fetching nutrition info for manually entered ingredient: " + newIngredientName);
                searchFoodInEdamam(newIngredientName, 1.0 , "Serving", selectedMealType, selectedDate, false, "", imageUrl);
                Log.d("DEBUG", "Confirmation Dialog Imageurl: " + imageUrl);
            }


        });

        // Retry button
        builder.setNeutralButton("Retry", (dialog, which) -> {
            // Retry taking a picture
            showMealOptionDialog(null);
        });

        // Manual input button
        builder.setNegativeButton("Enter Manually", (dialog, which) -> {
            // Show the manual input dialog
            handleFoodNameInput(selectedMealType, null);
        });

        builder.show();
    }


    private void sendImageToFoodvisor(String imageUrl, Bitmap imageBitmap, String selectedMealType) {

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("https://vision.foodvisor.io/api/1.0/en/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    FoodvisorApi api = retrofit.create(FoodvisorApi.class);
                    Log.d("Foodvisor", "Sending image URL to Foodvisor API");

                    File file = new File(getContext().getCacheDir(), "meal_image.jpg");
                    try (FileOutputStream out = new FileOutputStream(file)) {
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    } catch (IOException e) {
                        Log.e("Foodvisor", "Failed to save bitmap to file: " + e.getMessage());
                        return;
                    }

                    // Convert the image file to RequestBody
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
                    MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

                    // Correct way to pass scopes as a single string
                    MultipartBody.Part scopesPart = MultipartBody.Part.createFormData("scopes", "nutrition:macro,nutrition:micro");

                    Call<ResponseBody> call = api.recognizeFood("Api-Key RXpIy6iK.phqqXRhf9UwSh0EjBr4Rui8VticNXlB4", imagePart);

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.d("Foodvisor", "API response code: " + response.code());

                            if (response.isSuccessful()) {
                                Log.d("Foodvisor", "API call successful");
                                try {
                                    String result = response.body().string();
                                    Log.d("Foodvisor", "API Response: " + result);

                                    // Start parsing the JSON response
                                    JSONObject jsonObject = new JSONObject(result);
                                    JSONArray items = jsonObject.getJSONArray("items");

                                    List<RecognizedIngredient> ingredients = new ArrayList<>();

                                    for (int i = 0; i < items.length(); i++) {
                                        JSONObject item = items.getJSONObject(i);
                                        JSONArray foodArray = item.getJSONArray("food");

                                        for (int j = 0; j < foodArray.length(); j++) {
                                            JSONObject food = foodArray.getJSONObject(j);
                                            JSONObject foodInfo = food.getJSONObject("food_info");
                                            JSONObject nutritionJson = foodInfo.getJSONObject("nutrition");

                                            String foodName = foodInfo.optString("display_name", "Unknown");
                                            double calories = nutritionJson.optDouble("calories_100g", 0);
                                            double carbs = nutritionJson.optDouble("carbs_100g", 0);
                                            double fats = nutritionJson.optDouble("fat_100g", 0);
                                            double proteins = nutritionJson.optDouble("proteins_100g", 0);
                                            double fibers = nutritionJson.optDouble("fibers_100g", 0);

                                            RecognizedIngredient.Nutrition nutrition = new RecognizedIngredient.Nutrition();
                                            nutrition.setCalories((float) calories);
                                            nutrition.setCarbs((float) carbs);
                                            nutrition.setFats((float) fats);
                                            nutrition.setProteins((float) proteins);
                                            nutrition.setFibers((float) fibers);

                                            RecognizedIngredient ingredient = new RecognizedIngredient();
                                            ingredient.setDisplayName(foodName);
                                            ingredient.setNutrition(nutrition);

                                            ingredients.add(ingredient);
                                        }
                                    }

                                    // Show the ingredients in a confirmation dialog for the user to review
                                    showConfirmationDialog(ingredients, selectedMealType, imageBitmap, imageUrl);

                                } catch (IOException | JSONException e) {
                                    Log.e("Foodvisor", "Error parsing response: " + e.getMessage());
                                    showFallbackOptions(selectedMealType);  // If an error occurs, show fallback options
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.e("Foodvisor", "API call failed: " + t.getMessage());
                        }
                    });
                }

    private void showFallbackOptions(String selectedMealType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Recognition Failed")
                .setMessage("We couldn't recognize the food item. What would you like to do?")
                .setPositiveButton("Enter Manually", (dialog, which) -> {
                    handleFoodNameInput(selectedMealType, null);
                })
                .setNegativeButton("Retry", (dialog, which) -> {
                    openCameraWithMealType(selectedMealType);
                })
                .setCancelable(false)
                .show();
    }



}

