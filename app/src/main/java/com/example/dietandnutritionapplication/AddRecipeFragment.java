package com.example.dietandnutritionapplication;

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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class AddRecipeFragment extends Fragment {

    private LinearLayout ingredientsSection;
    private Button addIngredientButton, saveRecipeButton;
    private LinearLayout mealTypeCheckboxes, cuisineTypeCheckboxes, dishTypeCheckboxes;
    private EditText recipeTitleInput, caloriesInput, weightInput, totalTimeInput;
    private ImageView imagePreview;

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_recipe, container, false);

        recipeTitleInput = view.findViewById(R.id.recipe_title);
        caloriesInput = view.findViewById(R.id.recipe_calories);
        weightInput = view.findViewById(R.id.recipe_weight);
        totalTimeInput = view.findViewById(R.id.recipe_total_time);

        ingredientsSection = view.findViewById(R.id.ingredients_section);
        addIngredientButton = view.findViewById(R.id.add_ingredient_button);
        saveRecipeButton = view.findViewById(R.id.save_recipe_button);

        // CheckBox Layouts
        mealTypeCheckboxes = view.findViewById(R.id.meal_type_checkboxes);
        cuisineTypeCheckboxes = view.findViewById(R.id.cuisine_type_checkboxes);
        dishTypeCheckboxes = view.findViewById(R.id.dish_type_checkboxes);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Add ingredient fields dynamically
        addIngredientButton.setOnClickListener(v -> addIngredientField());

        // Handle the recipe submission
        saveRecipeButton.setOnClickListener(v -> saveRecipeToFirestore());

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

    // Save Recipe Data to Firestore
    private void saveRecipeToFirestore() {

        String recipeTitle = recipeTitleInput.getText().toString();
        double calories = 0;
        double weight = 0;
        double totalTime = 0;

        String caloriesInputValue = caloriesInput.getText().toString().trim();
        if (!caloriesInputValue.isEmpty()) {
            try {
                calories = Double.parseDouble(caloriesInputValue);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid input for calories. Please enter a valid number.", Toast.LENGTH_SHORT).show();
                return; // Stop execution if input is invalid
            }
        }

        // Attempt to parse weight
        String weightInputValue = weightInput.getText().toString().trim();
        if (!weightInputValue.isEmpty()) {
            try {
                weight = Double.parseDouble(weightInputValue);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid input for weight. Please enter a valid number.", Toast.LENGTH_SHORT).show();
                return; // Stop execution if input is invalid
            }
        }

        // Attempt to parse total time
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
        List<String> cuisineTypes = getSelectedCheckboxes(cuisineTypeCheckboxes, false);
        List<String> dishTypes = getSelectedCheckboxes(dishTypeCheckboxes, false);

        // Collect dynamic ingredients
        List<Map<String, String>> ingredientsList = new ArrayList<>();
        int ingredientCount = ingredientsSection.getChildCount();
        for (int i = 0; i < ingredientCount; i++) {
            View ingredientRow = ingredientsSection.getChildAt(i);
            if (ingredientRow instanceof LinearLayout) {
                LinearLayout rowLayout = (LinearLayout) ingredientRow;
                EditText ingredientNameInput = (EditText) rowLayout.getChildAt(0);
                EditText ingredientWeightInput = (EditText) rowLayout.getChildAt(1);

                String ingredientName = ingredientNameInput.getText().toString();
                String ingredientWeight = ingredientWeightInput.getText().toString();

                if (!ingredientName.isEmpty() && !ingredientWeight.isEmpty()) {
                    Map<String, String> ingredientMap = new HashMap<>();
                    ingredientMap.put("name", ingredientName);
                    ingredientMap.put("weight", ingredientWeight);
                    ingredientsList.add(ingredientMap);
                }
            }
        }

        // Create a recipe data map
        Map<String, Object> recipeData = new HashMap<>();
        String uniqueRecipeId = UUID.randomUUID().toString(); // Generate a unique ID
        recipeData.put("recipe_id", uniqueRecipeId); // Add the unique ID to the recipe data
        recipeData.put("label", recipeTitle); // Add recipe title
        recipeData.put("calories", calories); // Add calories
        recipeData.put("totalWeight", weight); // Add weight
        recipeData.put("total_time", totalTime); // Add total time
        recipeData.put("mealType", mealTypes); // Store mealTypes as a List<String>
        recipeData.put("cuisineType", cuisineTypes); // Store cuisineTypes as a List<String>
        recipeData.put("dishType", dishTypes); // Store dishTypes as a List<String>
        recipeData.put("ingredientsList", ingredientsList); // Store ingredients as a list of maps

        // Add data to Firestore
        db.collection("Recipes")
                .document(uniqueRecipeId) // Use the unique ID as the document ID
                .set(recipeData) // Use set() to create or overwrite the document
                .addOnSuccessListener(documentReference -> {
                    // Show success toast
                    Toast.makeText(getContext(), "Recipe added successfully!", Toast.LENGTH_SHORT).show();

                    // Redirect to NavRecipesStatusFragment
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout, new NavRecipesStatusFragment())
                            .addToBackStack(null)  // Optional: Add to back stack
                            .commit();

                })
                .addOnFailureListener(e -> {
                    // Show failure toast
                    Toast.makeText(getContext(), "Failed to add recipe: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


}
