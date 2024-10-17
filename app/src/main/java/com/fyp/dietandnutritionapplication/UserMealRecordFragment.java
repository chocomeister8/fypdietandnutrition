package com.fyp.dietandnutritionapplication;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import android.Manifest;
import androidx.activity.result.ActivityResultLauncher;


import retrofit2.Call;


public class UserMealRecordFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
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

    private TextView carbsTextView, proteinsTextView, fatsTextView, calorieLimitView, remainingCaloriesView;

    private String selectedMealType;

    private UserMealRecordController userMealRecordController;
    private NotificationUController notificationUController;

    private int totalCarbs;
    private int totalProteins;
    private int totalFats;
    private double totalCalories;

    private LinearLayout breakfastLinearLayout;
    private LinearLayout lunchLinearLayout;
    private LinearLayout dinnerLinearLayout;
    private LinearLayout snackLinearLayout;

    private TextView notificationBadgeTextView;

    private static final int PICK_IMAGE_REQUEST = 1;

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

        notificationBadgeTextView = view.findViewById(R.id.notificationBadgeTextView);

        notificationUController = new NotificationUController();
        notificationUController.fetchNotifications(userId, new Notification.OnNotificationsFetchedListener() {
            @Override
            public void onNotificationsFetched(List<Notification> notifications) {
                // Notifications can be processed if needed

                // After fetching notifications, count them
                notificationUController.countNotifications(userId, new Notification.OnNotificationCountFetchedListener() {
                    @Override
                    public void onCountFetched(int count) {
                        if (count > 0) {
                            notificationBadgeTextView.setText(String.valueOf(count));
                            notificationBadgeTextView.setVisibility(View.VISIBLE);
                        } else {
                            notificationBadgeTextView.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

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

        calorieLimitView = view.findViewById(R.id.progress_calorielimit);
        remainingCaloriesView = view.findViewById(R.id.progress_remainingcalorie);

        calendar = Calendar.getInstance();

        ImageView notiImage = view.findViewById(R.id.noti_icon);
        notiImage.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NotificationUFragment())
                    .addToBackStack(null)
                    .commit();

        });

        if (currentUser != null) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            String selectedDateString = sdf.format(calendar.getTime());
            dateTextView.setText(selectedDateString);

            userMealRecordController.fetchUsernameAndCalorieLimit(userId, new MealRecord.OnUsernameAndCalorieLimitFetchedListener() {
                @Override
                public void onDataFetched(String username, double calorieLimit) {
                    updateDateTextView(calendar, username, userId);
                    dateTextView.setOnClickListener(v -> showDatePickerDialog(username, userId));
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
                        uploadImageToFirebaseStorage(imageBitmap, imageUrl -> {

                            performImageRecognition(getCurrentUserId(), selectedMealType, imageBitmap, imageUrl);
                        });

                    } else {
                        Log.e("Error", "Captured image bitmap is null.");
                        Toast.makeText(getActivity(), "Failed to capture image.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void uploadImageToFirebaseStorage(Bitmap imageBitmap, OnImageUploadListener listener) {
        // Create a file to save the bitmap
        File file = new File(getContext().getCacheDir(), "meal_image.jpg");
        try (FileOutputStream out = new FileOutputStream(file)) {
            // Compress the bitmap and save it to the file
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (IOException e) {
            Log.e("Meal Record", "Failed to save bitmap to file: " + e.getMessage());
            Toast.makeText(getContext(), "Failed to save image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the Uri of the file
        Uri imageUri = Uri.fromFile(file);

        // Upload the image to Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference()
                .child("meal_Records/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");

        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageReference.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                listener.onImageUploaded(uri.toString());
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed to retrieve download URL", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("Meal Record", "Failed to upload image: " + e.getMessage());
                    Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }

    public interface OnImageUploadListener {
        void onImageUploaded(String imageUrl);
    }

    private void performImageRecognition(String userId, String selectedMealType, Bitmap bitmap, String imageUrl) {
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
                            searchFoodInEdamam(userId, labelText, 100.00, "grams", selectedMealType, selectedDate, false, "", imageUrl);
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

    private void handleFoodNameInput(String userId, String selectedMealType) {
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
                fetchFoodServingUnits(userId, selectedMealType, foodName);
            } else {
                Toast.makeText(requireContext(), "Please enter a valid food name", Toast.LENGTH_SHORT).show();
            }
        });

        foodNameBuilder.setNegativeButton("Cancel", null);
        foodNameBuilder.create().show();
    }

    private void fetchFoodServingUnits(String userId, String selectedMealType, String foodName) {

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
                            handleServingInput(userId, selectedMealType, foodName, servingUnits);
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

    private void handleServingInput(String userId, String selectedMealType, String foodName, List<String> servingUnits) {
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
                String imageURL = "url_of_the_uploaded_image";
                // Call the function to search for the food and scale nutrients accordingly
                searchFoodInEdamam(userId, foodName, servingSize, selectedServingUnit, selectedMealType, selectedDate, false, "", imageURL);
            } else {
                Toast.makeText(requireContext(), "Please enter a valid serving size", Toast.LENGTH_SHORT).show();
            }
        });

        servingBuilder.setNegativeButton("Cancel", null);
        servingBuilder.create().show();
    }

    public void searchFoodInEdamam(String userId, String foodName, Double servingSize, String servingUnit, String selectedMealType, String selectedDate, boolean isUpdate, String mealRecordID, String imageURL) {
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
                                Log.d("FoodAPI", "Full API Response: " + response.body().toString());

                                List<FoodResponse.Hint> hints = response.body().getHints();
                                if (hints != null && !hints.isEmpty()) {
                                    FoodResponse.Hint hint = hints.get(0);
                                    FoodResponse.Food food = hint.getFood();
                                    if (food != null) {
                                        // Correctly access the measures list from Hint, not Food
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
                                                    String imageURL = food.getImage();
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

                                                    boolean apiCallSuccessful = true;


                                                    if (apiCallSuccessful) {
                                                    if (isUpdate) {
                                                        MealRecord mealRecord = new MealRecord();
                                                        mealRecord.setMealType(selectedMealType);
                                                        mealRecord.setServingSize(servingSize + " " + servingUnit);
                                                        mealRecord.setCalories(adjustedCalories);
                                                        mealRecord.setProteins(adjustedProtein);
                                                        mealRecord.setFats(adjustedFat);
                                                        mealRecord.setCarbs(adjustedCarbohydrates);
                                                        mealRecord.setFiber(adjustedFiber);
                                                        mealRecord.setImageUrl(imageURL);

                                                        userMealRecordController.updateMealRecord(mealRecordID, mealRecord);
                                                        Toast.makeText(getActivity(), "Meal updated successfully", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        userMealRecordController.storeMealData(userId, username, foodName, selectedMealType,
                                                                servingSize + " " + servingUnit, adjustedCalories, adjustedCarbohydrates, adjustedProtein,
                                                                adjustedFat, adjustedFiber, selectedDate, imageURL);
                                                    }
                                                    refreshMealData();
                                                  }
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
                } else {
                    Log.e("FoodAPI", "Username not fetched or doesn't exist.");
                }
            }
        });
    }

    private void updateMealLogUI(List<MealRecord> mealRecords, String selectedDateString, String userId) {
        Log.d("MealLogFragment", "Selected Date: " + selectedDateString);

        breakfastLinearLayout.removeAllViews();
        lunchLinearLayout.removeAllViews();
        dinnerLinearLayout.removeAllViews();
        snackLinearLayout.removeAllViews();

        totalCarbs = 0;
        totalProteins = 0;
        totalFats = 0;

        breakfastTextView.setText("");
        lunchTextView.setText("");
        dinnerTextView.setText("");
        snackTextView.setText("");
        Log.d("MealLogFragment", "Meal Records size: " + mealRecords.size());

        for (MealRecord mealRecord : mealRecords) {
            Log.d("MealLogFragment", "line432 Meal Records: " + mealRecord);
            String mealName = mealRecord.getMealName();
            double mealCalories = mealRecord.getCalories();
            double carbs = mealRecord.getCarbs();
            double proteins = mealRecord.getProteins();
            double fats = mealRecord.getFats();
            String servingsize = mealRecord.getServingSize();
            String imageUrl = mealRecord.getImageUrl();

            totalCarbs += carbs;
            totalProteins += proteins;
            totalFats += fats;

            // Log meal details
            Log.d("MealLogFragment", "Added meal: " + mealName + ", Calories: " + mealCalories + ", Carbs: " + carbs + ", Proteins: " + proteins + ", Fats: " + fats);

            // Format the meal entry
            DecimalFormat decimalFormat = new DecimalFormat("#");
            String formattedCalories = decimalFormat.format(mealCalories);

            LinearLayout mealEntryLayout = new LinearLayout(getContext());
            mealEntryLayout.setOrientation(LinearLayout.HORIZONTAL);
            mealEntryLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            ImageView mealImageView = new ImageView(getContext());
            mealImageView.setLayoutParams(new LinearLayout.LayoutParams(160, 160)); // Slightly larger size (e.g., 120x120)
            mealImageView.setScaleType(ImageView.ScaleType.CENTER_CROP); // Adjust scale type
            mealImageView.setPadding(8, 0, 8, 16);
            Log.d("MealLogFragment", "Loading image from URL: " + imageUrl);
            // Load the image into the ImageView using Glide
            Glide.with(getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.foodimage)
                    .into(mealImageView);

            mealEntryLayout.addView(mealImageView);

            LinearLayout textLayout = new LinearLayout(getContext());
            textLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            textLayout.setLayoutParams(textParams);
            textLayout.setPadding(25, 0, 0, 0);

            TextView titleTextView = new TextView(getContext());
            titleTextView.setText(mealName + " (" + servingsize + ")");
            titleTextView.setTextSize(16);
            textLayout.addView(titleTextView);

            // Calories TextView
            TextView caloriesTextView = new TextView(getContext());
            caloriesTextView.setText(formattedCalories + " Cal");
            caloriesTextView.setTextSize(14);
            textLayout.addView(caloriesTextView);

            mealEntryLayout.addView(textLayout);

            ImageView moreOptionsIcon = new ImageView(getContext());
            moreOptionsIcon.setImageResource(R.drawable.baseline_more_vert_24);
            moreOptionsIcon.setContentDescription("More Options");
            mealEntryLayout.addView(moreOptionsIcon);


            // Append meal entry to the appropriate StringBuilder based on meal type
            String mealType = mealRecord.getMealType();
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
                    Log.e("MealLogFragment", "Unknown meal type: " + mealType);
            }

            moreOptionsIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create a PopupMenu
                    PopupMenu popup = new PopupMenu(getContext(), moreOptionsIcon);
                    // Inflate the popup menu from a menu resource
                    popup.getMenuInflater().inflate(R.menu.meal_record_menu, popup.getMenu());

                    // Set a click listener for menu item clicks
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            String mealRecordID = mealRecord.getMealRecordID();
                            switch (item.getItemId()) {
                                case R.id.update_meal:
                                    updateMealRecord(mealRecord, mealRecordID);
                                    return true;
                                case R.id.delete_meal:
                                    String mealTypeD = mealRecord.getMealType();
                                    deleteMealRecord(mealRecordID, mealTypeD);
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });

                    // Show the popup menu
                    popup.show();
                }
            });



            if (carbsTextView != null) carbsTextView.setText(totalCarbs + "g");
            Log.d("MealLogFragment", "Total Carbs: " + totalCarbs + "g");
            if (proteinsTextView != null) proteinsTextView.setText(totalProteins + "g");
            Log.d("MealLogFragment", "Total Proteins: " + totalProteins + "g");
            if (fatsTextView != null) fatsTextView.setText(totalFats + "g");
            Log.d("MealLogFragment", "Total Fats: " + totalFats + "g");

            Log.d("MealLogFragment", "Finished updating meal log UI for user: " + userId);

        }

    }

    private void updateMealRecord(MealRecord mealRecord, String mealRecordID) {
        // Inflate the dialog layout
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View dialogView = inflater.inflate(R.layout.dialog_update_meal, null);

        Spinner spinnerMealType = dialogView.findViewById(R.id.spinnerMealType);
        EditText editTextServingSize = dialogView.findViewById(R.id.editTextServingSize);
        Spinner servingUnitSpinner = dialogView.findViewById(R.id.servingUnitSpinner);

        ArrayAdapter<CharSequence> mealadapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.meal_types_array, android.R.layout.simple_spinner_item);
        mealadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMealType.setAdapter(mealadapter);

        int spinnerPosition = mealadapter.getPosition(mealRecord.getMealType());
        spinnerMealType.setSelection(spinnerPosition);

        String servingSizeWithUnit = mealRecord.getServingSize(); // e.g., "50.0 grams"
        String[] sizeAndUnit = servingSizeWithUnit.split(" ", 2); // Split into two parts: size and unit
        if (sizeAndUnit.length == 2) {
            editTextServingSize.setText(sizeAndUnit[0]); // Set serving size (first part)
        }

        // Fetch available serving measures from Edamam API for the food name
        String foodName = mealRecord.getMealName();
        String appId = "997e8d42";
        String appKey = "4483ab153d93c4a64d6f156fcffa78ff";

        EdamamApiService apiService = ApiClient.getRetrofitInstance().create(EdamamApiService.class);
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

                        // Check if measures are not null and populate the list of serving units
                        if (measures != null && !measures.isEmpty()) {
                            List<String> servingUnits = new ArrayList<>();
                            for (FoodResponse.Measure measure : measures) {
                                servingUnits.add(measure.getLabel());
                                Log.d("FoodAPI", "Serving Unit Label: " + measure.getLabel());
                            }

                            // Dynamically populate the servingUnitSpinner with available serving units
                            ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(getActivity(),
                                    android.R.layout.simple_spinner_item, servingUnits);
                            unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            servingUnitSpinner.setAdapter(unitAdapter);

                            // Set the spinner to the unit currently in the meal record
                            if (sizeAndUnit.length == 2) {
                                int unitPosition = unitAdapter.getPosition(sizeAndUnit[1]);
                                if (unitPosition >= 0) {
                                    servingUnitSpinner.setSelection(unitPosition); // Set spinner to the correct unit
                                }
                            }

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Update Meal Record")
                                    .setView(dialogView)
                                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Retrieve updated information from input fields
                                            String updatedMealType = spinnerMealType.getSelectedItem().toString();
                                            String servingSizeText = editTextServingSize.getText().toString();
                                            double updatedServingSize = 0;
                                            try {
                                                updatedServingSize = Double.parseDouble(servingSizeText);
                                            } catch (NumberFormatException e) {
                                                Toast.makeText(getActivity(), "Invalid serving size", Toast.LENGTH_SHORT).show();
                                                return; // Exit if the input is invalid
                                            }

                                            String updatedServingUnit = servingUnitSpinner.getSelectedItem().toString();
                                            String updatedServingSizeWithUnit = updatedServingSize + " " + updatedServingUnit; // Combine size and unit

                                            String previousServingSize = mealRecord.getServingSize();
                                            String previousMealType = mealRecord.getMealType();
                                            if (!previousServingSize.equals(updatedServingSizeWithUnit) || !previousMealType.equals(updatedMealType)) {
                                                String userId = getCurrentUserId();
                                                String selectedDate = dateTextView.getText().toString();
                                                String mealName = mealRecord.getMealName();
                                                searchFoodInEdamam(userId, mealName, updatedServingSize, updatedServingUnit, updatedMealType, selectedDate, true, mealRecordID, "");
                                                checkAndHideEmptyMealTypeCard(updatedMealType);
                                            }
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss(); // Close the dialog without any action
                                        }
                                    })
                                    .create()
                                    .show();
                         } else {
                            Toast.makeText(getActivity(), "No serving units found for: " + foodName, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "No food hints found for: " + foodName, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("MealLogFragment", "API response unsuccessful or empty.");
                }
            }

            @Override
            public void onFailure(Call<FoodResponse> call, Throwable t) {
                Log.e("MealLogFragment", "Error fetching food data: " + t.getMessage());
            }
        });
    }

    private void deleteMealRecord(String mealRecordID, String mealTypeD) {
        // Call the controller to delete the meal record from the database
        userMealRecordController.deleteMealRecord(mealRecordID, new MealRecord.OnMealDeletedListener() {
            @Override
            public void onMealDeleted() {
                // Show success message to the user
                Toast.makeText(getActivity(), "Meal deleted successfully", Toast.LENGTH_SHORT).show();
                refreshMealData();
                checkAndHideEmptyMealTypeCard(mealTypeD);
                Log.d("MealLogFragment", "After refresh - " + mealTypeD);
            }

            @Override
            public void onError(String error) {
                // Show error message if the delete operation fails
                Toast.makeText(getActivity(), "Error deleting meal: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkAndHideEmptyMealTypeCard(String mealType) {
        // Based on the meal type, check the respective LinearLayout
        if (TextUtils.isEmpty(breakfastTextView.getText())) {
            cardViewBreakfast.setVisibility(View.GONE);
        } else {
            cardViewBreakfast.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(lunchTextView.getText())) {
            cardViewLunch.setVisibility(View.GONE);
        } else {
            cardViewLunch.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(dinnerTextView.getText())) {
            cardViewDinner.setVisibility(View.GONE);
        } else {
            cardViewDinner.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(snackTextView.getText())) {
            cardViewSnack.setVisibility(View.GONE);
        } else {
            cardViewSnack.setVisibility(View.VISIBLE);
        }
    }

    private void refreshMealData() {
        userMealRecordController.fetchUsernameAndCalorieLimit(getCurrentUserId(), new MealRecord.OnUsernameAndCalorieLimitFetchedListener() {
            @Override
            public void onDataFetched(String username, double calorielimit) {
                if (username != null) {
                    fetchMealsAndUpdateUI(username);
                } else {
                    Log.e("MealLogFragment", "Username not fetched or doesn't exist");
                }
            }
        });
    }

    private void fetchMealsAndUpdateUI(String username) {
        TextView dateTextView = getView().findViewById(R.id.dateTextView);

        String selectedDateString = dateTextView.getText().toString();

        userMealRecordController.fetchMealsLogged(username, selectedDateString, new MealRecord.OnMealsFetchedListener() {
            @Override
            public void onMealsFetched(List<MealRecord> mealRecords) {
                if (mealRecords != null && !mealRecords.isEmpty()) {
                    calculateAndDisplayRemainingCalories(mealRecords);
                } else {
                    Log.w("MealLogFragment", "No meal records found.");
                }
            }
        });
    }

    private void calculateAndDisplayRemainingCalories(List<MealRecord> mealRecords) {
        TextView dateTextView = getView().findViewById(R.id.dateTextView);

        String selectedDateString = dateTextView.getText().toString();

        userMealRecordController.calculateRemainingCalories(getCurrentUserId(), selectedDateString, new MealRecord.OnRemainingCaloriesCalculatedListener() {
            @Override
            public void onRemainingCaloriesCalculated(double calorieLimit, double remainingCalories) {
                updateCalorieDisplay(calorieLimit, remainingCalories);
                updateMealLogUI(mealRecords, selectedDateString, getCurrentUserId());
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
                                        handleFoodNameInput(userId, selectedMealType);
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
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        String formattedDate = dateFormat.format(calendar.getTime());
        dateTextView.setText(formattedDate);

        clearMealLogUI();

        Log.d("MealLogFragment", "updateDateTextView Selected Date: " + formattedDate);
        userMealRecordController.fetchMealsLogged(username, formattedDate, new MealRecord.OnMealsFetchedListener() {
            @Override
            public void onMealsFetched(List<MealRecord> mealRecords) {
                if (mealRecords != null && !mealRecords.isEmpty()) {
                    Log.d("MealLogFragment", "updateDateTextView Fetched " + mealRecords.size() + " meals for date: " + formattedDate);


                } else {
                    Log.w("MealLogFragment", "updateDateTextView No meal records found.");
                    clearMealLogUI();
                }
                updateMealLogUI(mealRecords, formattedDate, userId);
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
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
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