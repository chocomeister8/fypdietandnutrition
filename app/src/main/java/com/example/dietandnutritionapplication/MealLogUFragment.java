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
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
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

    private int totalCarbs;
    private int totalProteins;
    private int totalFats;

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

    private String selectedDateString;

    private TextView carbsTextView, proteinsTextView, fatsTextView;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_log, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

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

        calendar = Calendar.getInstance();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            updateDateTextView(view, calendar);
            String selectedDateString = getSelectedDate();
            Log.d("MealLogFragment", "get selecteddatestring : " + selectedDateString);

            fetchUsername(userId, new OnUsernameFetchedListener() {
                @Override
                public void onUsernameFetched(String username) {
                    if (username != null) {
                        fetchMealsLogged(view, username, selectedDateString);
                        calculateRemainingCalories(view, userId);
                    } else {
                        Log.w("MealLogFragment", "Username not found, cannot fetch meals.");
                    }
                }
            });
            setupMealOptionButtons(view);

            takePictureLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            // Handle the captured image here
                        }
                    }
            );
            dateTextView.setOnClickListener(v -> showDatePickerDialog());
        }
        else {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
        }

        return view;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(getActivity(), "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), "No camera app found!", Toast.LENGTH_SHORT).show();
        }
    }

    private void performImageRecognition(Bitmap bitmap) {
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

                        if (isFoodLabel(labelText)) {
                            //searchFoodInEdamam(labelText);
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

    private void calculateRemainingCalories(View view, String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        TextView dateTextView = view.findViewById(R.id.dateTextView);
        String selectedDate = dateTextView.getText().toString();
        Log.d("CalorieTracking", "SelectedDate: " + selectedDate);

        TimeZone singaporeTimeZone = TimeZone.getTimeZone("Asia/Singapore");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(singaporeTimeZone);

        // Get the calorie limit for the user
        db.collection("Users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String username = document.getString("username");
                    Log.d("CalorieTracking", "Username: " + username);
                    int calorieLimit = document.getLong("calorieLimit").intValue();
                    Log.d("CalorieTracking", "Daily Calorie Limit: " + calorieLimit);

                    // Retrieve logged meals for today
                    db.collection("MealRecords")
                            .whereEqualTo("username", username)
                            .get()
                            .addOnCompleteListener(mealTask -> {
                                if (mealTask.isSuccessful()) {
                                    int totalCalories = 0;
                                    for (DocumentSnapshot mealDocument : mealTask.getResult()) {
                                        Log.d("CalorieTracking", "Meal Document: " + mealDocument.getId() + " - Calories: " + mealDocument.getDouble("calories") + " - Created Date: " + mealDocument.getTimestamp("createdDate"));
                                        if (mealDocument.contains("calories") && mealDocument.contains("createdDate")) {
                                            Timestamp createdDateTimestamp = mealDocument.getTimestamp("createdDate");
                                            if (createdDateTimestamp != null) {
                                                String createdDate = dateFormat.format(createdDateTimestamp.toDate());
                                                Log.d("CalorieTracking", "Selected Date: " + selectedDate);
                                                Log.d("CalorieTracking", "Meal Date: " + createdDate);
                                                // Compare dates
                                                if (createdDate.equals(selectedDate)) {
                                                    double mealCalories = mealDocument.getDouble("calories");
                                                    totalCalories += mealCalories; // Accumulate total calories
                                                }
                                            }
                                        } else {
                                            Log.w("CalorieTracking", "Calories or createdDate field missing in meal document");
                                        }
                                    }

                                    // Calculate remaining calories
                                    int remainingCalories = calorieLimit - totalCalories;
                                    Log.d("CalorieTracking", "Total Calories Consumed: " + totalCalories);
                                    Log.d("CalorieTracking", "Remaining Calories: " + remainingCalories);

                                    // Display the results on UI
                                    updateCalorieDisplay(view, calorieLimit, remainingCalories);
                                }else {
                                    Log.e("CalorieTracking", "Error retrieving meals: ", mealTask.getException());
                                }
                            });
                } else {
                    Log.e("CalorieTracking", "User document does not exist.");
                }
            } else {
                Log.e("CalorieTracking", "Error fetching user data: ", task.getException());
            }
        });
    }

    private void updateCalorieDisplay(View view, int calorieLimit, int remainingCalories) {
        TextView calorieLimitView = view.findViewById(R.id.progress_calorielimit);
        TextView remainingCaloriesView = view.findViewById(R.id.progress_remainingcalorie);

        calorieLimitView.setText("Calorie Limit: " + calorieLimit);
        remainingCaloriesView.setText("Remaining:\n " + remainingCalories);
    }

    private void handleEnterManually(View view) {
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
        Spinner mealTypeSpinner = dialogView.findViewById(R.id.meal_type_spinner);

        // Setup the Spinner for serving units (e.g., grams, cups, pieces)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.serving_units_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        servingUnitSpinner.setAdapter(adapter);

        ArrayAdapter<CharSequence> mealTypeAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.meal_types_array, android.R.layout.simple_spinner_item); // Make sure you define this array in strings.xml
        mealTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealTypeSpinner.setAdapter(mealTypeAdapter);

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

                String mealType = mealTypeSpinner.getSelectedItem().toString(); // Get the selected meal type
                Log.d("MealLogFragment", "Meal Type Selected: " + mealType);

                // Call the function to search for the food and scale nutrients accordingly
                searchFoodInEdamam(view, foodName, servingSize, servingUnit, mealType);
            } else {
                // Handle empty serving size input (e.g., show an error)
                Toast.makeText(requireContext(), "Please enter a valid serving size", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void searchFoodInEdamam(View view, String foodName, Double servingSize, String servingUnit, String mealType) {
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

                        TextView dateTextView = view.findViewById(R.id.dateTextView);
                        String selectedDate = dateTextView.getText().toString();
                        Log.d("FoodAPI", "Selected Date: " + selectedDate);

                        double scaleFactor = servingSize / 100.0; // Assuming Edamam data is per 100 grams

                        double adjustedCalories = referenceCalories * scaleFactor;
                        double adjustedProtein = referenceProtein * scaleFactor;
                        double adjustedFat = referenceFat * scaleFactor;
                        double adjustedCarbohydrates = referenceCarbohydrates * scaleFactor;
                        String servingInfo = servingSize + " " + servingUnit;

                        Log.d("FoodAPI", "Adjusted Calories: " + adjustedCalories);

                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            String userId = currentUser.getUid();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            // Retrieve the user document from Firestore
                            db.collection("Users").document(userId)
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                // Get the username field
                                                String username = document.getString("username");
                                                Log.d("MealLogFragment", "Username: " + username);
                                                storeMealData(view, username, foodLabel, mealType, servingInfo, adjustedCalories, adjustedCarbohydrates, adjustedProtein, adjustedFat);
                                            }
                                        }
                                    });

                        } else {
                            Log.w("FoodAPI", "No food items found for label: " + foodName);
                        }
                    } else {
                        Log.e("FoodAPI", "Response not successful: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<FoodResponse> call, Throwable t) {
                Log.e("FoodAPI", "Error fetching food data", t);
            }
        });
        }

    private void storeMealData(View view,String username, String mealName, String mealType, String servingInfo, double calories, double carbs, double proteins, double fats) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        TextView dateTextView = view.findViewById(R.id.dateTextView);
        String selectedDateStr = dateTextView.getText().toString(); // e.g., "2024-09-25"

        // Convert the selected date string to a Date object
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));

        Map<String, Object> mealData = new HashMap<>();
        mealData.put("mealName", mealName);
        mealData.put("mealType", mealType);
        mealData.put("servingSize", servingInfo);
        mealData.put("calories", calories);
        mealData.put("carbs", carbs);
        mealData.put("proteins", proteins);
        mealData.put("fats", fats);

        try {
            // Parse the string to a Date object and convert to Timestamp directly
            Timestamp createdTimestamp = new Timestamp(sdf.parse(selectedDateStr));
            mealData.put("createdDate", createdTimestamp);
        } catch (ParseException e) {
            Log.e("MealLogUFragment", "Error parsing date", e);
            // Handle error (e.g., use current date if parsing fails)
            mealData.put("createdDate", new Timestamp(new Date())); // Current date
        }
        mealData.put("modifiedDate", null);
        mealData.put("username", username);

        db.collection("MealRecords")
                .add(mealData)
                .addOnSuccessListener(documentReference -> {
                    fetchMealsLogged(view, username, selectedDateStr);
                    calculateRemainingCalories(view, username);
                    Log.d("MealLogUFragment", "Meal added with ID: " + documentReference.getId());
                    Toast.makeText(getActivity(), "Meal added successfully", Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(e -> {
                    Log.w("MealLogUFragment", "Error adding meal", e);
                });
    }

    private void fetchMealsLogged(View view, String username, String selectedDateStr) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Log.d("MealLogUFragment", "fetchMealsLogged() date:"+ selectedDateStr);

        db.collection("MealRecords")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    Log.d("MealLogUFragment", "fetching username and date " + username);
                    if (task.isSuccessful()) {
                        Log.d("MealLogUFragment", "task.isSuccessful()");
                        List<MealRecord> mealRecords = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            MealRecord mealRecord = document.toObject(MealRecord.class);
                            // Check if createdDate is a String and convert it to Timestamp or Date
                            if (document.contains("createdDate")) {
                                Object createdDateObj = document.get("createdDate");
                                if (createdDateObj instanceof Timestamp) {
                                    mealRecord.setCreatedDate((Timestamp) createdDateObj);
                                } else if (createdDateObj instanceof String) {
                                    // If it's a string, convert to Timestamp
                                    String dateString = (String) createdDateObj;
                                    try {
                                        // Assuming the date is stored in a standard format, e.g., "yyyy-MM-dd"
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                        Date parsedDate = sdf.parse(dateString);
                                        mealRecord.setCreatedDate(new Timestamp(parsedDate)); // Convert to Timestamp
                                    } catch (ParseException e) {
                                        Log.e("MealLogUFragment", "Error parsing date string: " + dateString, e);
                                    }
                                }
                            }
                            if (isSameDate(mealRecord.getCreatedDate(), selectedDateStr)) {
                                mealRecords.add(mealRecord);
                            }

                        }
                        Log.d("MealLogUFragment", "Fetched meal records: " + mealRecords.size());
                        updateMealLogUI(view, mealRecords, selectedDateStr, getCurrentUserId());
                        calculateRemainingCalories(view, username);
                    } else {
                        Log.w("MealLogUFragment", "Error getting documents.", task.getException());
                    }
                });
    }

    private boolean isSameDate(Timestamp mealDate, String selectedDateStr) {
        if (mealDate == null) return false;

        try {
            // Convert Timestamp to Date
            Date date = mealDate.toDate();

            // Create a calendar instance for Singapore timezone
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"));
            calendar.setTime(date);

            // Format the date to "yyyy-MM-dd"
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            dateFormat.setTimeZone(calendar.getTimeZone()); // Set the formatter timezone to Singapore
            String mealDateFormatted = dateFormat.format(calendar.getTime());

            // Compare the formatted date with the selected date string
            return selectedDateStr.equals(mealDateFormatted);
        } catch (Exception e) {
            Log.e("MealLogFragment", "Error formatting date: " + e.getMessage());
            return false; // Return false in case of any formatting error
        }
    }

    private void updateMealLogUI(View view,List<MealRecord> mealRecords, String selectedDateString, String userId) {
        Log.d("MealLogFragment", "Selected Date: " + selectedDateString);
        totalCarbs = 0;
        totalProteins = 0;
        totalFats = 0;
        StringBuilder breakfastBuilder = new StringBuilder();
        StringBuilder lunchBuilder = new StringBuilder();
        StringBuilder dinnerBuilder = new StringBuilder();
        StringBuilder snackBuilder = new StringBuilder();

        // Clear previous meal entries (optional)
        breakfastTextView.setText("");  // Clear previous content
        lunchTextView.setText("");
        dinnerTextView.setText("");
        snackTextView.setText("");

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
            String mealEntry = mealName  + " - " + servingsize + " - " + formattedCalories + " Cal\n";  // New line for better formatting

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
        calculateRemainingCalories(view, userId);
        Log.d("MealLogFragment", "Finished updating meal log UI for user: " + userId);


        calculateRemainingCalories(view, userId);
        Log.d("MealLogFragment", "Finished updating meal log UI for user: " + userId);
    }

    private String getSelectedDate() {
        // Check if a date has been previously selected
        if (selectedDateString != null && !selectedDateString.isEmpty()) {
            return selectedDateString; // Return the previously selected date
        } else {
            // Return today's date in a specific format (e.g., "yyyy-MM-dd")
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return dateFormat.format(new Date()); // Return today's date
        }
    }

    private void showMealOptionDialog(View view) {
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
                                break;
                            case 1:
                                // Handle entering manually
                                handleEnterManually(view);
                                break;
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateDateTextView(View view, Calendar calendar) {
        TextView dateTextView = view.findViewById(R.id.dateTextView); // Ensure the correct ID is used
        if (dateTextView != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = dateFormat.format(calendar.getTime());
            dateTextView.setText(formattedDate);
        } else {
            Log.e("MealLogUFragment", "dateTextView is null");
        }
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

    private void fetchUsername(String userId, OnUsernameFetchedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username"); // Assuming the field name is "username"
                        listener.onUsernameFetched(username); // Notify the listener
                    } else {
                        Log.d("User", "No such user document exists!");
                        listener.onUsernameFetched(null); // Handle case when document doesn't exist
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("User", "Error fetching username", e);
                    listener.onUsernameFetched(null); // Handle error
                });
    }

    interface OnUsernameFetchedListener {
        void onUsernameFetched(String username);
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateTextView(view,calendar);
                    selectedDateString = getSelectedDate();
                    // Fetch new meal logs for the selected date
                    fetchMealsLogged(view, currentUser.getUid(), selectedDateString);
                    calculateRemainingCalories(getView(), currentUser.getUid());  // Call to update the remaining calories
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void setupMealOptionButtons(View view) {
        ImageView mealOptionButton1 = view.findViewById(R.id.camera_icon);
        TextView mealOptionButton2 = view.findViewById(R.id.snap_photo_text);

        mealOptionButton1.setOnClickListener(v -> showMealOptionDialog(view));
        mealOptionButton2.setOnClickListener(v -> showMealOptionDialog(view));
    }

}