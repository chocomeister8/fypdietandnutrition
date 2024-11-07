package com.fyp.dietandnutritionapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.firestore.ListenerRegistration;

import com.github.mikephil.charting.components.AxisBase;
import com.google.firebase.Timestamp;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.components.XAxis;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;


public class userHomePageFragment extends Fragment {

    private FirebaseFirestore firestore;
    private String userId;
    private double userCalorieGoal;
    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipeList;
    private List<Recipe> recipeNewList;
    private List<Recipe> APIRecipeList;
    private BarChart barChart;
    private static final int DAYS_TO_SHOW = 7;
    private Calendar currentStartDate;
    private final Random random = new Random();
    private List<String> simpleFoodSearches = Arrays.asList(
            "chicken", "beef", "steak", "fish","lamb"
    );

    private List<String> mealtype = Arrays.asList(
            "lunch","dinner"
    );
    private TextView carbsTextView, proteinsTextView, fatsTextView, noRecommendationText;
    private int totalCarbs;
    private int totalProteins;
    private int totalFats;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private UserMealRecordController userMealRecordController;
    private LineChart lineChart;
    private EditText etWeight, etHeight, etBMI, etWeightGoal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.user_homepage, container, false);
        firestore = FirebaseFirestore.getInstance();
        fetchUserCalorieGoal();

        userMealRecordController = new UserMealRecordController();
        carbsTextView = view.findViewById(R.id.carbohydrates_value);
        proteinsTextView = view.findViewById(R.id.proteins_value);
        fatsTextView = view.findViewById(R.id.fats_value);

        getTodaysTotalMacronutrients(getUserId());

        ImageView reviewIcon = view.findViewById(R.id.reviewIcon);
        ImageView logoutIcon = view.findViewById(R.id.logout_icon);


        db = FirebaseFirestore.getInstance();

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            userId = user.getUid();

            // Initialize buttons using view.findViewById
            Button button_recipes = view.findViewById(R.id.button_recipes);
            Button button_mealLog = view.findViewById(R.id.button_MealLog);
            Button button_diary = view.findViewById(R.id.diary);
            Button button_bmiCalculator = view.findViewById(R.id.bmiCalculator);
            Button button_consultation = view.findViewById(R.id.consultation);
            Button button_healthReport = view.findViewById(R.id.healthReport);
            Button button_faq = view.findViewById(R.id.FAQ);
            Button button_profile = view.findViewById(R.id.profile);
            Button button_mealLog1 = view.findViewById(R.id.button_MealLog1);
            noRecommendationText = view.findViewById(R.id.no_recommendation_text);
            lineChart = view.findViewById(R.id.LineChart);
            barChart = view.findViewById(R.id.bar_chart);
            currentStartDate = Calendar.getInstance();
            currentStartDate.setTimeZone(TimeZone.getTimeZone("GMT+8"));


            // Initialize EditText fields
            etWeight = view.findViewById(R.id.etWeight);
            etHeight = view.findViewById(R.id.etHeight);
            etBMI = view.findViewById(R.id.etBMI);
            etWeightGoal = view.findViewById(R.id.etWeightGoal);

            recyclerView = view.findViewById(R.id.recipeRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


            // Initialize the recipe list and adapter
            recipeList = new ArrayList<>();
            APIRecipeList = new ArrayList<>();
            recipeNewList = new ArrayList<>();
            recipeAdapter = new RecipeAdapter(recipeList, this::openRecipeDetailFragment, false);
            recyclerView.setAdapter(recipeAdapter);


            // Setup and update the line chart after view creation
            displayWeightProgressChart();
            fetchUserData();
            fetchWeeklyCalorieData();

            // Fetch recipes
            fetchRecipes();
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                // Start fetching recipes after the delay
                fetchRecipesFromRecommended();
            }, 1000);

            // Set up button click listeners to navigate between fragments
            button_recipes.setOnClickListener(v -> {
                // Replace current fragment with NavAllRecipesFragment
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, new navCreateFolderFragment())
                        .addToBackStack(null)  // Add to back stack to enable back navigation
                        .commit();
            });
            reviewIcon.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).replaceFragment(new userReviewAppFragment());
                }
            });
            logoutIcon.setOnClickListener(v -> {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MealPref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                FirebaseAuth.getInstance().signOut();
                // Create an AlertDialog to confirm logout
                new AlertDialog.Builder(getContext())
                        .setTitle("Confirm Logout")
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton("Log out", (dialog, which) -> {
                            // User confirmed to log out
                            Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();
                            if (getActivity() instanceof MainActivity) {
                                UserMealRecordFragment userMealRecordFragment = (UserMealRecordFragment) getFragmentManager().findFragmentByTag("UserMealRecordFragment");
                                if (userMealRecordFragment != null) {
                                    userMealRecordFragment.clearMealLogUI();
                                }
                                ((MainActivity) getActivity()).switchToGuestMode();
                                ((MainActivity) getActivity()).replaceFragment(new LoginFragment());
                            }
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            // User cancelled the logout action
                            dialog.dismiss();
                        })
                        .show();
            });

            // Define the OnClickListener
            View.OnClickListener buttonClickListener = v -> {
                // Replace current fragment with MealLogPreviewFragment
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, new UserMealRecordFragment())
                        .addToBackStack(null)
                        .commit();
            };

// Assign the OnClickListener to both buttons
            button_mealLog.setOnClickListener(buttonClickListener);
            button_mealLog1.setOnClickListener(buttonClickListener);

            button_diary.setOnClickListener(v -> {
                // Replace current fragment with NavFavouriteRecipesFragment
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, new UserDiaryFragment())
                        .addToBackStack(null)
                        .commit();
            });

            button_bmiCalculator.setOnClickListener(v -> {
                // Replace current fragment with NavRecipesStatusFragment
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, new BMICalculatorController())
                        .addToBackStack(null)
                        .commit();
            });

            button_consultation.setOnClickListener(v -> {
                // Replace current fragment with NavConsultationFragment
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, new ConsultationsUFragment())
                        .addToBackStack(null)
                        .commit();
            });

            button_healthReport.setOnClickListener(v -> {
                // Replace current fragment with NavHealthReportFragment
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, new healthReportFragment())
                        .addToBackStack(null)
                        .commit();
            });

            button_faq.setOnClickListener(v -> {
                // Replace current fragment with NavFAQFragment
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, new userViewFAQFragment())
                        .addToBackStack(null)
                        .commit();
            });

            button_profile.setOnClickListener(v -> {
                // Replace current fragment with NavProfileFragment
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, new ProfileUFragment())
                        .addToBackStack(null)
                        .commit();
            });

        }



        return view;
    }



    private void displayWeightProgressChart() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Set up a listener to monitor changes in the user's document
        ListenerRegistration listenerRegistration = db.collection("Users").document(userId)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        Log.w("Firestore", "Listen failed.", e);
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        double currentWeight = documentSnapshot.getDouble("currentWeight");
                        double weightGoal = documentSnapshot.getDouble("weightGoal");

                        // Create entries for the line chart with corresponding date
                        List<Entry> entries = new ArrayList<>();
                        entries.add(new Entry(0, (float) currentWeight));  // Current weight
                        entries.add(new Entry(1, (float) weightGoal));     // Weight goal

                        // Create a dataset and style it
                        LineDataSet lineDataSet = new LineDataSet(entries, "Weight Progress");
                        lineDataSet.setColor(Color.RED);
                        lineDataSet.setLineWidth(2f);
                        lineDataSet.setCircleRadius(4f);
                        lineDataSet.setCircleColor(Color.RED);
                        lineDataSet.setValueTextSize(12f);

                        // Create line data from dataset
                        LineData lineData = new LineData(lineDataSet);

                        lineChart.setData(lineData);
                        lineChart.getDescription().setEnabled(false);       // Disable description text
                        lineChart.getXAxis().setEnabled(false);              // Disable the X-axis
                        lineChart.getAxisLeft().setDrawGridLines(false);    // Disable Y-axis left grid lines
                        lineChart.getAxisRight().setEnabled(false);         // Disable right Y-axis and labels

                        // Remove grid background and borders
                        lineChart.setDrawGridBackground(false);             // Remove grid background
                        lineChart.setDrawBorders(false);                    // Remove borders if any

                        // Refresh the chart
                        lineChart.invalidate(); // Refresh chart after setting data

                    } else {
                        Log.d("Database", "No such document");
                    }
                });
    }



    private void fetchUserData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = getUserId();
        db.collection("Users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            Log.d("Firestore", "User document retrieved successfully.");

                            // Fetch weight, height, and weight goal
                            Double weight = document.getDouble("currentWeight");
                            Double height = document.getDouble("currentHeight");
                            Double weightGoal = document.getDouble("weightGoal");

                            // Set the values in EditTexts if available
                            if (weight != null) etWeight.setText(String.valueOf(weight));
                            if (height != null) etHeight.setText(String.valueOf(height));
                            if (weightGoal != null) etWeightGoal.setText(String.valueOf(weightGoal));

                            // Optional: Calculate BMI if height and weight are available
                            if (weight != null && height != null) {
                                double heightInMeters = height / 100; // Convert cm to meters
                                double bmi = weight / (heightInMeters * heightInMeters);
                                etBMI.setText(String.format("%.2f", bmi));
                            }

                            // Set up change listeners for EditTexts
                            setupChangeListeners(weight, height, weightGoal, userId);
                        } else {
                            Log.d("Firestore", "User document not found.");
                            Toast.makeText(getActivity(), "User data not found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.w("Firestore", "Error fetching user data.", task.getException());
                    }
                });
    }

    private void checkForChangesAndPrompt(String field, Double originalValue, EditText editText, String userId) {
        Double newValue;
        try {
            newValue = Double.valueOf(editText.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "Invalid input for " + field, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newValue.equals(originalValue)) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Confirm Changes")
                    .setMessage("Do you want to update " + field + " to " + newValue + "?")
                    .setPositiveButton("Confirm", (dialog, which) -> {
                        // Update the user data
                        updateUserData(field, newValue, userId);

                        // Recalculate BMI after update
                        recalculateBMI(userId);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> editText.setText(String.valueOf(originalValue)))
                    .show();
        }
    }

    private void recalculateBMI(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            // Fetch the updated weight and height
                            Double weight = document.getDouble("currentWeight");
                            Double height = document.getDouble("currentHeight");

                            if (weight != null && height != null) {
                                double heightInMeters = height / 100; // Convert cm to meters
                                double bmi = weight / (heightInMeters * heightInMeters);
                                etBMI.setText(String.format("%.2f", bmi)); // Update BMI field with the recalculated value
                            }
                        }
                    }
                });
    }

    private void setupChangeListeners(Double initialWeight, Double initialHeight, Double initialWeightGoal, String userId) {
        etWeight.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) checkForChangesAndPrompt("currentWeight", initialWeight, etWeight, userId);
        });

        etHeight.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) checkForChangesAndPrompt("currentHeight", initialHeight, etHeight, userId);
        });

        etWeightGoal.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) checkForChangesAndPrompt("weightGoal", initialWeightGoal, etWeightGoal, userId);
        });
    }

    private void updateUserData(String field, Double newValue, String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(userId)
                .update(field, newValue)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", field + " updated successfully.");
                    Toast.makeText(getActivity(), field + " updated.", Toast.LENGTH_SHORT).show();
                    // Optionally store the change in a history collection
                    // addMeasurementToHistory(userId, weight, height, weightGoal);
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error updating " + field, e);
                    Toast.makeText(getActivity(), "Failed to update " + field, Toast.LENGTH_SHORT).show();
                });
    }


    private void fetchUserCalorieGoal() {
        String userId = getUserId(); // Assume you have a method to retrieve the current user's ID
        firestore.collection("Users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        userCalorieGoal = documentSnapshot.getDouble("calorieLimit");
                        Log.d("Calorie Goal", "User's calorie goal: " + userCalorieGoal);
                    } else {
                        Log.w("Calorie Goal", "User document not found");
                    }
                })
                .addOnFailureListener(e -> Log.e("Calorie Goal", "Failed to retrieve calorie goal", e));
    }

    private String getUserId() {
        // Return the current user's ID, possibly from FirebaseAuth or another source
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void openRecipeDetailFragment(Recipe recipe) {
        // From NavAllRecipesFragment
        Bundle bundle = new Bundle();
        bundle.putParcelable("selected_recipe", recipe);  // Assuming selectedRecipe is the clicked recipe object
        bundle.putString("source", "recommended");  // Pass "all" as the source


        RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
        recipeDetailFragment.setArguments(bundle);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, recipeDetailFragment)
                .addToBackStack(null)
                .commit();

    }


    public void getTodaysTotalMacronutrients(String userId) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String todayDateString = dateFormat.format(new Date());

        userMealRecordController.fetchUsernameAndCalorieLimit(userId, new MealRecord.OnUsernameAndCalorieLimitFetchedListener() {
            @Override
            public void onDataFetched(String username, double calorieLimit) {
                if (username != null) {
                    userMealRecordController.fetchMealsLogged(username, todayDateString, new MealRecord.OnMealsFetchedListener() {
                        @Override
                        public void onMealsFetched(List<MealRecord> mealRecords) {
                            if (mealRecords != null && !mealRecords.isEmpty()) {
                                userMealRecordController.calculateRemainingCalories(userId, todayDateString, new MealRecord.OnRemainingCaloriesCalculatedListener() {
                                    @Override
                                    public void onRemainingCaloriesCalculated(double calorieLimit, double remainingCalories) {
                                        totalCarbs = 0;
                                        totalProteins = 0;
                                        totalFats = 0;

                                        // Calculate totals
                                        for (MealRecord mealRecord : mealRecords) {
                                            totalCarbs += mealRecord.getCarbs();
                                            totalProteins += mealRecord.getProteins();
                                            totalFats += mealRecord.getFats();
                                        }

                                        if (carbsTextView != null) carbsTextView.setText(totalCarbs + "g");

                                        if (proteinsTextView != null) proteinsTextView.setText(totalProteins + "g");

                                        if (fatsTextView != null) fatsTextView.setText(totalFats + "g");
                                    }
                                });
                            } else {
                                Log.w("MealLogFragment", "No meal records found.");
                            }

                        }
                    });
                }
            }
        });
    }

    private void fetchRecipes() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            NavRecommendedRecipesController navRecommendedRecipesController = new NavRecommendedRecipesController();

            // Call retrieveRecommendedRecipes with userId and an implementation of the listener
            navRecommendedRecipesController.retrieveRecommendedRecipes(userId, getContext(), new NavRecommendedRecipesController.OnRecommendedRecipesRetrievedListener() {
                @Override
                public void onRecipesRetrieved(ArrayList<Recipe> recipes) {
                    // Handle the retrieved recipes
                    recipeList.clear();
                    recipeList.addAll(recipes);
//                    recipeAdapter.notifyDataSetChanged();

                    if (recipes.isEmpty()) {
                        noRecommendationText.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        noRecommendationText.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onError(Exception e) {
                    // Handle errors
                    Toast.makeText(getContext(), "Error retrieving recommended recipes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "User is not logged in.", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchRecipesFromRecommended() {
        // Clear previous recipe lists
        APIRecipeList.clear();


        String app_id = "2c7710ea"; // Your Edamam API app ID
        String app_key = "97f5e9187c865600f74e2baa358a9efb";
        String type = "public";

        // Fetch favorite recipes
//        fetchFavoriteRecipes(null, null, null);

        // Initialize AtomicInteger to track completed requests
        AtomicInteger completedRequests = new AtomicInteger(0);
        int totalRecipes = recipeList.size(); // Total number of recipes to fetch

        // Log the number of recipes being fetched
        Log.d("Fetch Recipes", "Total recipes to fetch: " + totalRecipes);


        // Iterate through each recipe label
        for (Recipe recipe : recipeList) {
            String labelQuery = recipe.getLabel(); // Use the label from the current recipe
            Log.d("API Call", "Fetching recipes for: " + labelQuery);

            EdamamApi api = ApiClient.getRetrofitInstance().create(EdamamApi.class);

            // Call the API to fetch recipes based on the label
            Call<RecipeResponse> call = api.searchRecipes(labelQuery, app_id, app_key, type, null, null, null, null);

            call.enqueue(new Callback<RecipeResponse>() {
                @Override
                public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<RecipeResponse.Hit> hits = response.body().getHits(); // Get hits from response

                        // Log the number of recipes fetched
                        Log.d("Fetched Recipes", "Number of recipes fetched for " + labelQuery + ": " + hits.size());

                        for (RecipeResponse.Hit hit : hits) {
                            Recipe apiRecipe = hit.getRecipe(); // Extract the Recipe from Hit

                            // Calculate calories per 100g
                            double caloriesPer100g = apiRecipe.getCaloriesPer100g();
                            if (apiRecipe.getTotalWeight() > 0) {
                                caloriesPer100g = (apiRecipe.getCalories() / apiRecipe.getTotalWeight()) * 100;
                            }
                            apiRecipe.setCaloriesPer100g(caloriesPer100g); // Update recipe object

                            APIRecipeList.add(apiRecipe); // Add to the APIRecipeList
                        }

                        // Compare the retrieved recipes with the user's favorite recipes
                        for (Recipe favoriteRecipe : recipeList) {
                            for (Recipe apiRecipe : APIRecipeList) {
                                // Compare by label
                                if (favoriteRecipe.getLabel().equals(apiRecipe.getLabel())) {
                                    Log.d("Matching Recipe:", favoriteRecipe.getLabel());
                                    recipeNewList.add(apiRecipe); // Add matching recipe to the new list
                                    break; // Exit inner loop once a match is found
                                } else {
                                    Log.d("No Match Found:", favoriteRecipe.getLabel() + " vs " + apiRecipe.getLabel());
                                }
                            }
                        }
                    } else {
                        Log.d("Fetch Recipes", "Response was not successful or body is null. Code: " + response.code());
                    }

                    // Increment completed requests
                    if (completedRequests.incrementAndGet() == totalRecipes) {

                        // Update the UI after all requests have completed
                        recipeList.clear();
                        recipeList.addAll(recipeNewList);
                        HashSet<Recipe> recipeSet = new HashSet<>(recipeList);
                        recipeList.clear();
                        recipeList.addAll(recipeSet);
                        Log.d("Recipe List Size", "Size before notify: " + recipeList.size());
                        recipeAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<RecipeResponse> call, Throwable t) {
                    Log.e("Fetch Recipes", "Error: " + t.getMessage());
                    // Increment completed requests even if there was a failure
                    if (completedRequests.incrementAndGet() == totalRecipes) {
                        // Update the UI in case of failure as well
                        recipeList.clear();
                        recipeList.addAll(recipeNewList);
                        Log.d("Recipe List Size", "Size before notify: " + recipeList.size());
                        recipeAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    private void fetchWeeklyCalorieData() {
        // Calculate start and end dates for the past 7 days from the current date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        // Get current date as the end date
        Date endDate = new Date(System.currentTimeMillis());
        Log.d("CalorieData", "End date: " + sdf.format(endDate));

        // Calculate start date as 6 days before the current date
        Date startDate = new Date(System.currentTimeMillis() - 6L * 24 * 60 * 60 * 1000);
        Log.d("CalorieData", "Start date: " + sdf.format(startDate));

        db.collection("MealRecords")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Initialize maps for each meal type to store calorie data by date
                        Map<String, Float> breakfastData = new HashMap<>();
                        Map<String, Float> lunchData = new HashMap<>();
                        Map<String, Float> dinnerData = new HashMap<>();
                        Map<String, Float> snacksData = new HashMap<>();

                        // Process each document from the Firestore response
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Timestamp timestamp = document.getTimestamp("createdDate");
                            String mealType = document.getString("mealType");
                            Double calories = document.getDouble("calories");

                            if (timestamp != null && timestamp.toDate().after(startDate) &&
                                    timestamp.toDate().compareTo(endDate) <= 0 &&
                                    mealType != null && calories != null) {

                                // Format the date for map keys
                                String dateKey = new SimpleDateFormat("d MMM", Locale.getDefault()).format(timestamp.toDate());
                                Log.d("CalorieData", "Date: " + dateKey + " MealType: " + mealType + " Calories: " + calories);

                                // Select the appropriate map based on meal type
                                Map<String, Float> targetMap;
                                switch (mealType.toLowerCase()) {
                                    case "breakfast":
                                        targetMap = breakfastData;
                                        break;
                                    case "lunch":
                                        targetMap = lunchData;
                                        break;
                                    case "dinner":
                                        targetMap = dinnerData;
                                        break;
                                    default:
                                        targetMap = snacksData;
                                        break;
                                }

                                // Update the calorie count for the specific date and meal type
                                targetMap.merge(dateKey, calories.floatValue(), Float::sum);
                            }
                        }

                        // Log the processed data
                        Log.d("CalorieData", "Breakfast Data: " + breakfastData);
                        Log.d("CalorieData", "Lunch Data: " + lunchData);
                        Log.d("CalorieData", "Dinner Data: " + dinnerData);
                        Log.d("CalorieData", "Snacks Data: " + snacksData);

                        // Update the chart with the collected data
                        updateCalorieChart(breakfastData, lunchData, dinnerData, snacksData);
                    } else {
                        Log.e("FetchCalorieData", "Error fetching data: ", task.getException());
                    }
                });
    }


    private void updateCalorieChart(Map<String, Float> breakfastData,
                                    Map<String, Float> lunchData,
                                    Map<String, Float> dinnerData,
                                    Map<String, Float> snacksData) {

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        Calendar calendar = (Calendar) currentStartDate.clone();
        calendar.add(Calendar.DAY_OF_MONTH, -5);  // Start from 5 days before the selected date
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM", Locale.getDefault());

        float maxCalories = 0f;  // Variable to hold the maximum calorie intake

        for (int i = 0; i < 7; i++) {  // Loop for 7 days (5 days prior + selected day + 1 day after)
            String dateKey = sdf.format(calendar.getTime());
            labels.add(dateKey);

            float breakfast = breakfastData.getOrDefault(dateKey, 0f);
            float lunch = lunchData.getOrDefault(dateKey, 0f);
            float dinner = dinnerData.getOrDefault(dateKey, 0f);
            float snacks = snacksData.getOrDefault(dateKey, 0f);

            float totalCalories = breakfast + lunch + dinner + snacks;  // Calculate total calories for the day
            entries.add(new BarEntry(i, new float[]{breakfast, lunch, dinner, snacks}));

            // Update maxCalories if totalCalories is greater
            if (totalCalories > maxCalories) {
                maxCalories = totalCalories;
            }

            calendar.add(Calendar.DAY_OF_MONTH, 1);  // Move to the next day
        }

        // Log the entries and labels
        Log.d("ChartData", "Entries: " + entries);
        Log.d("ChartData", "Labels: " + labels);

        BarDataSet dataSet = new BarDataSet(entries, "Daily Calories");
        dataSet.setColors(
                Color.rgb(176, 224, 230),
                Color.rgb(152, 251, 152),
                Color.rgb(255, 228, 181),
                Color.rgb(255, 182, 193)
        );
        dataSet.setStackLabels(new String[]{"Breakfast", "Lunch", "Dinner", "Snacks"});

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);

        // Set the data for the chart
        barChart.setData(barData);

        // Set X-axis properties
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        // Set Y-axis properties
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setAxisMinimum(0f);

        barChart.getDescription().setEnabled(false);

        // Set chart legend properties
        Legend legend = barChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBorders(false);
        barChart.setBackgroundColor(Color.WHITE);

        // Refresh the chart
        barChart.invalidate();
    }

}








