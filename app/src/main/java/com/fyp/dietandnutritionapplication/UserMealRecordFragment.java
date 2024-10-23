package com.fyp.dietandnutritionapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
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

import com.fyp.dietandnutritionapplication.RecognizedIngredient;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


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


    private static final int MAX_MEAL_RECORDS = 3;
    private int mealCount = 0;
    private static final int REQUEST_CODE_PERMISSIONS = 1001;
    private static final int REQUEST_IMAGE_PICK = 103;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_log, container, false);

        userMealRecordController = new UserMealRecordController();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        sharedPreferences = getActivity().getSharedPreferences("MealPref", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        dateTextView = view.findViewById(R.id.dateTextView);
        calorieLimitTextView = view.findViewById(R.id.progress_calorielimit);

        notificationBadgeTextView = view.findViewById(R.id.notificationBadgeTextView);

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

        if (currentUser != null) {
            String userId = currentUser.getUid();
            Log.d("MealLogFragment", "User is logged in with ID: " + userId);
            initializeLoggedInUser(view, userId);
        } else {
            Log.d("MealLogFragment", "No user is logged in. Initializing guest mode.");
            clearMealLogUI();
            resetMealInsertionCount();
            initializeGuestUser(view);
        }

        return view;

    }

    private void initializeLoggedInUser(View view, String userId) {
        // Setup for logged-in users
        notificationUController = new NotificationUController();
        notificationUController.fetchNotifications(userId, notifications -> {
            // Handle notifications
            notificationUController.countNotifications(userId, count -> {
                if (count > 0) {
                    notificationBadgeTextView.setText(String.valueOf(count));
                    notificationBadgeTextView.setVisibility(View.VISIBLE);
                } else {
                    notificationBadgeTextView.setVisibility(View.GONE);
                }
            });
        });

        mealOptionButton1 = view.findViewById(R.id.camera_icon);
        mealOptionButton2 = view.findViewById(R.id.snap_photo_text);
        mealOptionButton1.setOnClickListener(v -> showMealOptionDialog(userId));
        mealOptionButton2.setOnClickListener(v -> showMealOptionDialog(userId));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String selectedDateString = sdf.format(calendar.getTime());
        dateTextView.setText(selectedDateString);

        userMealRecordController.fetchUsernameAndCalorieLimit(userId, (username, calorieLimit) -> {
            updateDateTextView(calendar, username, userId);
            dateTextView.setOnClickListener(v -> showDatePickerDialog(username, userId));
        });
    }

    private void initializeGuestUser(View view) {
        clearMealLogUI();
        hideNotificationBadge();
        mealOptionButton1 = view.findViewById(R.id.camera_icon);
        mealOptionButton2 = view.findViewById(R.id.snap_photo_text);
        mealOptionButton1.setOnClickListener(v -> checkMealInsertLimit(null));
        mealOptionButton2.setOnClickListener(v -> checkMealInsertLimit(null));
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {

                // First, handle camera data if present
                if (data != null && data.getExtras() != null) {
                    Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");

                    if (imageBitmap != null) {
                        String userId = getCurrentUserId();
                        String selectedMealType = this.selectedMealType;

                        uploadImageToFirebaseStorage(imageBitmap, imageUrl -> {
                            downloadImageFromFirebase(imageUrl, downloadedBitmap -> {
                                sendImageToFoodvisor(imageUrl, downloadedBitmap, userId, selectedMealType);
                            });
                        });
                    }
                }

                // If no camera data, check if the result is from gallery selection (URI)
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    try {
                        // Convert the selected image URI to a bitmap
                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImageUri);

                        if (imageBitmap != null) {
                            String userId = getCurrentUserId();
                            String selectedMealType = this.selectedMealType;

                            sendImageToFoodvisor(selectedImageUri.toString(), imageBitmap, userId, selectedMealType);
                        }
                    } catch (IOException e) {
                        Log.e("DEBUG", "Error getting bitmap from URI", e);
                    }
                } else {
                    Toast.makeText(getActivity(), "Failed to capture or select image.", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(getActivity(), "Operation canceled.", Toast.LENGTH_SHORT).show();
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

    private void downloadImageFromFirebase(String imageUrl, OnImageDownloadListener listener) {
        Glide.with(getContext())
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        // Pass the downloaded image to the listener
                        listener.onImageDownloaded(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Handle cleanup, if needed
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        Log.e("Firebase Image", "Failed to download image");
                    }
                });
    }

    public interface OnImageDownloadListener {
        void onImageDownloaded(Bitmap bitmap);
    }

    private void sendImageToFoodvisor(String imageURL, Bitmap imageBitmap, String userId, String selectedMealType) {
        userMealRecordController.fetchUsernameAndCalorieLimit(userId, new MealRecord.OnUsernameAndCalorieLimitFetchedListener() {
            @Override
            public void onDataFetched(String username, double calorielimit) {
                if (username != null) {
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
                                            JSONObject nutrition = foodInfo.getJSONObject("nutrition");

                                            String foodName = foodInfo.optString("display_name", "Unknown");
                                            double calories = nutrition.optDouble("calories_100g", 0);
                                            double carbs = nutrition.optDouble("carbs_100g", 0);
                                            double fats = nutrition.optDouble("fat_100g", 0);
                                            double proteins = nutrition.optDouble("proteins_100g", 0);
                                            double fibers = nutrition.optDouble("fibers_100g", 0);

                                            RecognizedIngredient ingredient = new RecognizedIngredient();
                                            ingredient.setDisplayName(foodName);
                                            ingredient.setCalories((float) calories);
                                            ingredient.setCarbs((float) carbs);
                                            ingredient.setFats((float) fats);
                                            ingredient.setProteins((float) proteins);
                                            ingredient.setFibers((float) fibers);

                                            ingredients.add(ingredient);
                                        }
                                    }

                                    // Show the ingredients in a confirmation dialog for the user to review
                                    showConfirmationDialog(ingredients, selectedMealType, userId, username, imageURL);

                                } catch (IOException | JSONException e) {
                                    Log.e("Foodvisor", "Error parsing response: " + e.getMessage());
                                    showFallbackOptions(userId, selectedMealType);  // If an error occurs, show fallback options
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.e("Foodvisor", "API call failed: " + t.getMessage());
                        }
                    });
                }
            }
        });
    }

    private void showConfirmationDialog(List<RecognizedIngredient> ingredients, String selectedMealType, String userId, String username, String imageURL) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Review Ingredients");

        // Create a LinearLayout to hold the checkboxes
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        // Create an array to store the user's selections
        final boolean[] checkedItems = new boolean[ingredients.size()];

        // Create checkboxes for each ingredient
        for (int i = 0; i < ingredients.size(); i++) {
            RecognizedIngredient ingredient = ingredients.get(i);
            CheckBox checkBox = new CheckBox(requireContext());
            checkBox.setText(ingredient.getDisplayName() + " - Calories: " + ingredient.calories + " kcal");
            layout.addView(checkBox);

            // Set the checkbox listener to track selections
            final int index = i;
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) ->
            {
                Log.d("DEBUG", "Checkbox for ingredient: " + ingredient.getDisplayName() + " checked: " + isChecked);
                checkedItems[index] = isChecked;
            });
        }

        TextView manualEntryLabel = new TextView(requireContext());
        manualEntryLabel.setText("Or Add a New Ingredient:");
        layout.addView(manualEntryLabel);

        EditText manualIngredientName = new EditText(requireContext());
        manualIngredientName.setHint("Ingredient Name");
        layout.addView(manualIngredientName);

        builder.setView(layout);

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            for (int i = 0; i < ingredients.size(); i++) {
                if (checkedItems[i]) {
                    RecognizedIngredient ingredient = ingredients.get(i);
                    userMealRecordController.storeMealData(
                            userId,
                            username,
                            ingredient.getDisplayName(),
                            selectedMealType,
                            "1 Serving",
                            ingredient.calories,
                            ingredient.carbs,
                            ingredient.proteins,
                            ingredient.fats,
                            ingredient.fibers,
                            dateTextView.getText().toString(),
                            imageURL
                    );
                    refreshMealData();
                }
            }


            //Toast.makeText(requireContext(), "Selected meals saved successfully", Toast.LENGTH_SHORT).show();
            String newIngredientName = manualIngredientName.getText().toString().trim();
            if (!newIngredientName.isEmpty()) {
                Log.d("DEBUG", "Fetching nutrition info for manually entered ingredient: " + newIngredientName);
                // Fetch nutrition info from Foodvisor
                fetchNutritionalInfoFromFoodvisor(newIngredientName, (fetchedIngredient) -> {
                    if (fetchedIngredient != null) {
                        Log.d("DEBUG", "Fetched ingredient: " + fetchedIngredient.getDisplayName());
                        userMealRecordController.storeMealData(
                                userId,
                                username,
                                fetchedIngredient.getDisplayName(),
                                selectedMealType,
                                "1 Serving",
                                fetchedIngredient.calories,
                                fetchedIngredient.carbs,
                                fetchedIngredient.proteins,
                                fetchedIngredient.fats,
                                fetchedIngredient.fibers,
                                dateTextView.getText().toString(),
                                imageURL
                        );
                        Toast.makeText(requireContext(), "Ingredient saved successfully", Toast.LENGTH_SHORT).show();
                        refreshMealData();
                    } else {
                        Toast.makeText(requireContext(), "Failed to fetch nutrition info", Toast.LENGTH_SHORT).show();
                    }
                });
            }


        });

        // Retry button
        builder.setNeutralButton("Retry", (dialog, which) -> {
            // Retry taking a picture
            openCameraWithMealType(selectedMealType);
        });

        // Manual input button
        builder.setNegativeButton("Enter Manually", (dialog, which) -> {
            // Show the manual input dialog
            handleFoodNameInput(userId, selectedMealType);
        });

        builder.show();
    }

    private void fetchNutritionalInfoFromFoodvisor(String ingredientName, OnIngredientFetchedListener listener) {
        Log.d("DEBUG", "Fetching nutritional info for ingredient: " + ingredientName);
        String apiKey = "Api-Key RXpIy6iK.phqqXRhf9UwSh0EjBr4Rui8VticNXlB4";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://vision.foodvisor.io/api/1.0/en/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FoodvisorApi apiService = retrofit.create(FoodvisorApi.class);

        Call<List<RecognizedIngredient>> call = apiService.getIngredientInfo(ingredientName, apiKey);

        call.enqueue(new Callback<List<RecognizedIngredient>>() {
            @Override
            public void onResponse(Call<List<RecognizedIngredient>> call, Response<List<RecognizedIngredient>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    List<RecognizedIngredient> fetchedIngredients = response.body();
                    if (!fetchedIngredients.isEmpty()) {
                        Log.d("DEBUG", "!fetchedIngredients.isEmpty()");
                        RecognizedIngredient firstIngredient = fetchedIngredients.get(0);

                        Log.d("DEBUG", "Fetched ingredient: " + firstIngredient.getDisplayName());
                        Log.d("DEBUG", "Calories: " + firstIngredient.getCalories());
                        Log.d("DEBUG", "Proteins: " + firstIngredient.getProteins());
                        Log.d("DEBUG", "Carbs: " + firstIngredient.getCarbs());
                        Log.d("DEBUG", "Fats: " + firstIngredient.getFats());
                        Log.d("DEBUG", "Fibers: " + firstIngredient.getFibers());


                        listener.onIngredientFetched(firstIngredient);
                    } else {
                        Log.e("DEBUG", "No ingredients found for: " + ingredientName);
                        listener.onIngredientFetched(null);
                    }
                } else {
                    try {
                        Log.e("DEBUG", "Error body: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e("DEBUG", "Failed to log error body");
                    }
                    listener.onIngredientFetched(null);
                }
            }

            @Override
            public void onFailure(Call<List<RecognizedIngredient>> call, Throwable t) {
                Log.e("DEBUG", "API call failed: " + t.getMessage(), t);
                listener.onIngredientFetched(null);
            }
        });
    }

    interface OnIngredientFetchedListener {
        void onIngredientFetched(RecognizedIngredient ingredient);
    }


    private void showFallbackOptions(String userId, String selectedMealType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Recognition Failed")
                .setMessage("We couldn't recognize the food item. What would you like to do?")
                .setPositiveButton("Enter Manually", (dialog, which) -> {
                    handleFoodNameInput(userId, selectedMealType);
                })
                .setNegativeButton("Retry", (dialog, which) -> {
                    openCameraWithMealType(selectedMealType);
                })
                .setCancelable(false)
                .show();
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
        if (userId == null) {
            // For guests, proceed without fetching user-specific data
            Log.d("MealLogFragment", "Guest is logging a meal. Proceeding without user-specific data.");

            String username = "Guest";
            double calorieLimit = 2000;

            performMealLogOperations(userId, foodName, servingSize, servingUnit, selectedMealType, selectedDate, isUpdate, mealRecordID, imageURL);

        } else {
            if (userId != null) {
                Log.d("Test3 10/24/2024", userId);
                // Proceed with the flow for logged-in users
                performMealLogOperations(userId, foodName, servingSize, servingUnit, selectedMealType, selectedDate, isUpdate, mealRecordID, imageURL);
            } else {
                Log.e("MealLogFragment", "Failed to fetch user data.");
            }
        }
    }

    private void performMealLogOperations(String userId, String foodName, Double servingSize, String servingUnit, String selectedMealType, String selectedDate, boolean isUpdate, String mealRecordID, String imageURL) {
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

    private void hideNotificationBadge() {
        notificationBadgeTextView.setVisibility(View.GONE);
    }

    private void checkMealInsertLimit(String userId) {
        int mealCount = getMealInsertionCount();
        if (mealCount >= MAX_MEAL_RECORDS) {
            showRegisterPrompt();
        } else {
            incrementMealInsertionCount();
            showMealOptionDialog(userId);
        }
    }

    private int getMealInsertionCount() {
        return sharedPreferences.getInt("mealCount", 0); // Default is 0
    }

    private void incrementMealInsertionCount() {
        int currentCount = getMealInsertionCount();
        editor.putInt("mealCount", currentCount + 1);
        editor.apply();
    }

    private void showRegisterPrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Limit Reached")
                .setMessage("You have reached the limit of 5 meal entries. Please register to add more.")
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

        if (userId == null) {
            checkMealInsertLimit(null);
        }

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
                                        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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

        if (userId == null || username == null) {
            // If no user is logged in, stop here
            Log.d("MealLogFragment", "No user logged in. Skipping meal data fetch.");
            return;
        }

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
            return user.getUid();
        } else {
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