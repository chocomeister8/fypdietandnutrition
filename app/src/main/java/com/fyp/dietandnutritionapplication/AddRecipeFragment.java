package com.fyp.dietandnutritionapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class AddRecipeFragment extends Fragment {
    private int recipeStepCounter = 1;

    private LinearLayout ingredientsSection, recipeStepsSection;
    private Button addIngredientButton, addRecipeStepsButton, saveRecipeButton;
    private LinearLayout mealTypeCheckboxes, dishTypeCheckboxes;
    private EditText recipeTitleInput, caloriesInput, weightInput, totalTimeInput;
    private ImageView imagePreview;

    private FirebaseFirestore db;

    private AddRecipeController addRecipeController;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private TextView notificationBadgeTextView;
    private NotificationUController notificationUController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_recipe, container, false);


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

        addRecipeController = new AddRecipeController();

        recipeTitleInput = view.findViewById(R.id.recipe_title);
        caloriesInput = view.findViewById(R.id.recipe_calories);
        weightInput = view.findViewById(R.id.recipe_weight);
        totalTimeInput = view.findViewById(R.id.recipe_total_time);

        ingredientsSection = view.findViewById(R.id.ingredients_section);
        addIngredientButton = view.findViewById(R.id.add_ingredient_button);
        recipeStepsSection = view.findViewById(R.id.recipe_steps_section);
        addRecipeStepsButton = view.findViewById(R.id.add_recipe_step);
        saveRecipeButton = view.findViewById(R.id.save_recipe_button);

        // CheckBox Layouts
        mealTypeCheckboxes = view.findViewById(R.id.meal_type_checkboxes);
        dishTypeCheckboxes = view.findViewById(R.id.dish_type_checkboxes);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Add ingredient fields dynamically
        addIngredientButton.setOnClickListener(v -> addIngredientField());

        addRecipeStepsButton.setOnClickListener(v -> addRecipeStep());

        // Handle the recipe submission
        saveRecipeButton.setOnClickListener(v -> saveRecipeToFirestore());

        if (mealTypeCheckboxes != null && dishTypeCheckboxes != null) {
            Log.d("LayoutStatus", "Checkbox containers are found.");
        } else {
            Log.e("LayoutStatus", "Checkbox containers are not found.");
        }
        return view;
    }

    private List<String> getSelectedCheckboxes(LinearLayout checkboxGroup, boolean singleSelection) {
        List<String> selectedItems = new ArrayList<>();
        int count = checkboxGroup.getChildCount();

        for (int i = 0; i < count; i++) {
            View view = checkboxGroup.getChildAt(i);
            if (view instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) view;

                if (checkBox.isChecked()) {
                    // Log the checked checkbox
                    Log.d("CheckboxSelection", "Checked: " + checkBox.getText().toString());

                    // If single selection is allowed, clear the list before adding
                    if (singleSelection) {
                        selectedItems.clear(); // Clear previous selections
                    }
                    selectedItems.add(checkBox.getText().toString());

                    // If single selection is allowed, break the loop after adding the first checked item
                    if (singleSelection) {
                        break; // Only one item can be selected
                    }
                }
            }
        }
        return selectedItems;
    }

    // Add Dynamic Ingredient Fields
    private void addIngredientField() {
        LinearLayout ingredientRow = new LinearLayout(getContext());
        ingredientRow.setOrientation(LinearLayout.HORIZONTAL);
        ingredientRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Ingredient Name
        EditText ingredientInput = new EditText(getContext());
        ingredientInput.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
        ingredientInput.setHint("Ingredient Name");

        // Ingredient Weight
        EditText ingredientWeight = new EditText(getContext());
        ingredientWeight.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
        ingredientWeight.setHint("Weight (g)");
        ingredientWeight.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);

        // Remove Button
        Button removeButton = new Button(getContext());
        removeButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        removeButton.setText("Remove");
        removeButton.setOnClickListener(v -> ingredientsSection.removeView(ingredientRow));

        ingredientRow.addView(ingredientInput);
        ingredientRow.addView(ingredientWeight);
        ingredientRow.addView(removeButton);

        ingredientsSection.addView(ingredientRow);
    }
    private void addRecipeStep() {
        LinearLayout recipeSteps = new LinearLayout(getContext());
        recipeSteps.setOrientation(LinearLayout.HORIZONTAL);
        recipeSteps.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Step description (EditText)
        EditText recipeStepsInput = new EditText(getContext());
        recipeStepsInput.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));

        // Set the hint to show the current step number (e.g., "Step 1")
        recipeStepsInput.setHint("Step " + recipeStepCounter);

        // Remove Button
        Button removeButton = new Button(getContext());
        removeButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        removeButton.setText("Remove");

        // Add click listener to remove the step when clicked
        removeButton.setOnClickListener(v -> {
            recipeStepsSection.removeView(recipeSteps);
            // Decrement the counter and update step numbers
            recipeStepCounter--;
            updateStepHints();
        });

        // Add the input field and remove button to the layout
        recipeSteps.addView(recipeStepsInput);
        recipeSteps.addView(removeButton);

        // Add the row to the section for recipe steps
        recipeStepsSection.addView(recipeSteps);

        // Increment the counter for the next step
        recipeStepCounter++;
    }

    // Update hints of all steps to reflect correct step numbers after a step is removed
    private void updateStepHints() {
        int count = recipeStepsSection.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = recipeStepsSection.getChildAt(i);
            if (view instanceof LinearLayout) {
                LinearLayout rowLayout = (LinearLayout) view;
                EditText stepInput = (EditText) rowLayout.getChildAt(0); // The input field
                if (stepInput != null) {
                    // Update the hint to reflect the correct step number
                    stepInput.setHint("Step " + (i + 1));
                }
            }
        }
    }

    // Save Recipe Data to Firestore
    private void saveRecipeToFirestore() {

        String recipeTitle = recipeTitleInput.getText().toString();
        double calories = 0;
        double weight = 0;
        double totalTime = 0;
        String status = "Pending";

        // Fetch current user ID from Firebase Authentication
        String userId = getCurrentUserId();
        if (userId == null) {
            Toast.makeText(getContext(), "User not authenticated. Please log in.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse calories input
        String caloriesInputValue = caloriesInput.getText().toString().trim();
        if (!caloriesInputValue.isEmpty()) {
            try {
                calories = Double.parseDouble(caloriesInputValue);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid input for calories. Please enter a valid number.", Toast.LENGTH_SHORT).show();
                return; // Stop execution if input is invalid
            }
        }

        // Parse weight input
        String weightInputValue = weightInput.getText().toString().trim();
        if (!weightInputValue.isEmpty()) {
            try {
                weight = Double.parseDouble(weightInputValue);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid input for weight. Please enter a valid number.", Toast.LENGTH_SHORT).show();
                return; // Stop execution if input is invalid
            }
        }

        // Parse total time input
        String totalTimeInputValue = totalTimeInput.getText().toString().trim();
        if (!totalTimeInputValue.isEmpty()) {
            try {
                totalTime = Double.parseDouble(totalTimeInputValue);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid input for total time. Please enter a valid number.", Toast.LENGTH_SHORT).show();
                return; // Stop execution if input is invalid
            }
        }

        Log.d("RecipeInput", "Calories: " + calories);
        Log.d("RecipeInput", "Weight: " + weight);
        Log.d("RecipeInput", "Total Time: " + totalTime);

        // Get selected values from checkboxes
        List<String> mealTypes = getSelectedCheckboxes(mealTypeCheckboxes, false);
        List<String> dishTypes = getSelectedCheckboxes(dishTypeCheckboxes, false);

        // Collect dynamic ingredients
        List<String> ingredientsList = new ArrayList<>(); // Change to List<String>
        int ingredientCount = ingredientsSection.getChildCount();
        for (int i = 0; i < ingredientCount; i++) {
            View ingredientRow = ingredientsSection.getChildAt(i);
            if (ingredientRow instanceof LinearLayout) {
                LinearLayout rowLayout = (LinearLayout) ingredientRow;
                EditText ingredientNameInput = (EditText) rowLayout.getChildAt(0);
                EditText ingredientWeightInput = (EditText) rowLayout.getChildAt(1);

                String ingredientName = ingredientNameInput.getText().toString();
                String ingredientWeight = ingredientWeightInput.getText().toString();

                // Check if both fields are filled
                if (!ingredientName.isEmpty() && !ingredientWeight.isEmpty()) {
                    // Format the ingredient as a string (e.g., "ingredientName: ingredientWeight")
                    String ingredientString = ingredientName + ": " + ingredientWeight;
                    ingredientsList.add(ingredientString); // Add the formatted string to the list
                }
            }
        }

        List<String> recipeStepsList = new ArrayList<>(); // Change to List<String>
        int recipeStepsCount = recipeStepsSection.getChildCount();
        for (int i = 0; i < recipeStepsCount; i++) {
            View recipeSteps = recipeStepsSection.getChildAt(i);
            if (recipeSteps instanceof LinearLayout) {
                LinearLayout rowLayout = (LinearLayout) recipeSteps;
                EditText recipeStepInput = (EditText) rowLayout.getChildAt(0);

                String recipeStep = recipeStepInput.getText().toString();

                // Check if both fields are filled
                if (!recipeStep.isEmpty()) {
                    // Format the ingredient as a string (e.g., "ingredientName: ingredientWeight")
                    recipeStepsList.add(recipeStep); // Add the formatted string to the list
                }
            }
        }

        AddRecipeController addRecipeController = new AddRecipeController();
        addRecipeController.addRecipe(recipeTitle, calories, weight, totalTime, mealTypes, dishTypes, ingredientsList, recipeStepsList, userId, status);
        redirectToViewPendingRecipes();
        sendNotification(userId, recipeTitle);
    }

    private void redirectToViewPendingRecipes(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, new NavPendingRecipesFragment()); // Use your container ID
        fragmentTransaction.addToBackStack(null); // Optional: Add to back stack
        fragmentTransaction.commit();
    }

    // Method to retrieve the current user's ID from Firebase Authentication
    private String getCurrentUserId() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            return FirebaseAuth.getInstance().getCurrentUser().getUid(); // Return the user ID
        } else {
            return null; // User not logged in
        }
    }

    private void sendNotification(String userId, String recipeTitle) {

    Map<String, Object> notificationData = new HashMap<>();
    notificationData.put("userId", userId);
    notificationData.put("message", "Your recipe (ID: " + recipeTitle + ") has been submitted and is waiting for approval.");
    notificationData.put("type", "Recipe Submission");
    notificationData.put("isRead", false);
    Timestamp entryDateTime = new Timestamp(System.currentTimeMillis());
    notificationData.put("timestamp", entryDateTime);

        db.collection("Notifications")
                .add(notificationData) // Use add() to create a new document
                .addOnSuccessListener(documentReference -> {
                    String notificationId = documentReference.getId(); // Get the document ID

                    // Now update the document to include the ID as a field
                    db.collection("Notifications").document(notificationId)
                            .update("notificationId", notificationId) // Store the document ID
                            .addOnSuccessListener(aVoid -> {
                                Log.d("Notification", "Notification added with ID: " + notificationId);
                            })
                            .addOnFailureListener(e -> {
                                Log.w("Notification", "Error updating notification ID", e);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("Notification", "Failed to send notification: " + e.getMessage());
                });
    }

}
