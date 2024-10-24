package com.fyp.dietandnutritionapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class userHomePageFragment extends Fragment {

    private FirebaseFirestore firestore;
    private double userCalorieGoal;
    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipeList;

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

    private UserMealRecordController userMealRecordController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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

        recyclerView = view.findViewById(R.id.recipeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        // Initialize the recipe list and adapter
        recipeList = new ArrayList<>();
        recipeAdapter = new RecipeAdapter(recipeList, this::openRecipeDetailFragment, false);
        recyclerView.setAdapter(recipeAdapter);

        // Fetch recipes
        fetchRecipes();

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

        return view;
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
                    recipeAdapter.notifyDataSetChanged();

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
}
