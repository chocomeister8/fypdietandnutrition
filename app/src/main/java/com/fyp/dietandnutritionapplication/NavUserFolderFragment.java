package com.fyp.dietandnutritionapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NavUserFolderFragment extends Fragment {

    private String folderName;
    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipeList = new ArrayList<>();
    private List<Recipe> originalRecipeList = new ArrayList<>();
    private EditText searchEditText;
    private Spinner mealTypeSpinner;
    private Spinner dishTypeSpinner;
    private List<Recipe> recipeNewList;
    private List<Recipe> APIRecipeList;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private TextView notificationBadgeTextView;
    private NotificationUController notificationUController;

    private final String[] mealTypes = {"--Select Meal Type--", "Breakfast", "Lunch", "Dinner", "Snack", "Teatime"};
    private final String[] dishTypes = {"--Select Dish Type--", "Starter", "Main course", "Side dish", "Soup", "Condiments and sauces", "Desserts", "Drinks", "Salad"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_folder, container, false);



        // Retrieve folder name from arguments if provided
        if (getArguments() != null) {
            folderName = getArguments().getString("folder_name");
            TextView folderTitle = view.findViewById(R.id.folder_title);
            folderTitle.setText(folderName);
        }

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

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

        }

        ImageView notiImage = view.findViewById(R.id.noti_icon);
        notiImage.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NotificationUFragment())
                    .addToBackStack(null)
                    .commit();

        });


        initializeViews(view);
        loadRecipes(); // Load recipes based on the folder
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Start fetching recipes after the delay
            fetchRecipesFromFolder();
        }, 1000);
        restorePreviousState();
        setupButtonListeners(view);

        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recipe_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        APIRecipeList = new ArrayList<>();
        recipeNewList = new ArrayList<>();
        recipeAdapter = new RecipeAdapter(recipeList, this::openRecipeDetailFragment, false);
        recyclerView.setAdapter(recipeAdapter);

        mealTypeSpinner = view.findViewById(R.id.spinner_meal_type);
        dishTypeSpinner = view.findViewById(R.id.spinner_dish_type);
        setupSpinners();

        searchEditText = view.findViewById(R.id.search_recipe);
        setupSearchBar();
    }

//    private void loadRecipes() {
//        // Fetch the recipes from Firestore based on the folder name
//        RecipesEntity recipesEntity = new RecipesEntity();
//        recipesEntity.fetchRecipesFromFolder(folderName, new RecipesEntity.OnRecipesFetchedListener() {
//            @Override
//            public void onRecipesFetched(ArrayList<Recipe> fetchedRecipes) {
//                recipeList.clear(); // Clear the current recipe list
//                recipeList.addAll(fetchedRecipes); // Add fetched recipes
//                originalRecipeList = new ArrayList<>(recipeList); // Keep a copy of the original list
//                recipeAdapter.updateRecipeList(recipeList); // Notify the adapter to update
//            }
//        });
//    }

    private void loadRecipes() {
        NavUserFolderController controller = new NavUserFolderController(folderName);
        controller.checkFetchRecipesFolder(new NavUserFolderController.OnRecipesFetchedListener() {
            @Override
            public void onRecipesFetched(ArrayList<Recipe> fetchedRecipes) {
                recipeList.clear(); // Clear the current recipe list
                recipeList.addAll(fetchedRecipes); // Add fetched recipes
                originalRecipeList = new ArrayList<>(recipeList); // Keep a copy of the original list
//                Toast.makeText(getContext(), "Fetched " + fetchedRecipes.size() + " recipes", Toast.LENGTH_SHORT).show();
//                recipeAdapter.notifyDataSetChanged(); // Notify the adapter to update
            }
        });
    }

    private void fetchRecipesFromFolder() {
        // Clear previous recipe lists
        APIRecipeList.clear();
        recipeNewList.clear();

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

    private void setupSpinners() {
        ArrayAdapter<String> mealTypeAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, mealTypes);
        mealTypeSpinner.setAdapter(mealTypeAdapter);

        ArrayAdapter<String> dishTypeAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, dishTypes);
        dishTypeSpinner.setAdapter(dishTypeAdapter);

        setupSpinnerListeners();
    }

    private void setupSpinnerListeners() {
        mealTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterRecipes();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        dishTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterRecipes();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupSearchBar() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterRecipes();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void filterRecipes() {
        String searchQuery = searchEditText.getText().toString().trim().toLowerCase();
        String selectedMealType = mealTypeSpinner.getSelectedItem().toString();
        String selectedDishType = dishTypeSpinner.getSelectedItem().toString();

        List<Recipe> filteredList = new ArrayList<>();

        for (Recipe recipe : originalRecipeList) {
            boolean matchesSearchQuery = recipe.getLabel().toLowerCase().contains(searchQuery);
            boolean matchesMealType = selectedMealType.equals("--Select Meal Type--") || recipe.getMealType().contains(selectedMealType);
            boolean matchesDishType = selectedDishType.equals("--Select Dish Type--") || recipe.getDishType().contains(selectedDishType);

            if (matchesSearchQuery && matchesMealType && matchesDishType) {
                filteredList.add(recipe);
            }
        }

        recipeAdapter.updateRecipeList(filteredList); // Update the adapter with the filtered list
    }

    private void setupButtonListeners(View view) {
        Button button_add_recipe = view.findViewById(R.id.add_recipe_button);
        Button clearFiltersButton = view.findViewById(R.id.clear_filters_button);

        button_add_recipe.setOnClickListener(v -> navigateToFragment(new AddRecipeFragment()));
        clearFiltersButton.setOnClickListener(v -> clearFilters());
    }

    private void clearFilters() {
        mealTypeSpinner.setSelection(0); // Reset meal type spinner
        dishTypeSpinner.setSelection(0); // Reset dish type spinner
        searchEditText.setText(""); // Clear search bar

        recipeAdapter.updateRecipeList(originalRecipeList); // Reset to original recipe list
    }

    private void navigateToFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void openRecipeDetailFragment(Recipe recipe) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("selected_recipe", recipe);
        Fragment recipeDetailFragment = new RecipeDetailFragment();
        recipeDetailFragment.setArguments(bundle);
        navigateToFragment(recipeDetailFragment);
    }

    private void restorePreviousState() {
        // Add code to restore previous state if necessary
    }
}
