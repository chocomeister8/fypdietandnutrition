package com.example.dietandnutritionapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

    private Spinner mealTypeSpinner, cuisineTypeSpinner, dishTypeSpinner;
    private LinearLayout ingredientsSection;
    private Button addIngredientButton, saveRecipeButton;

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_recipe, container, false);

        mealTypeSpinner = view.findViewById(R.id.meal_type_spinner);
        cuisineTypeSpinner = view.findViewById(R.id.cuisine_type_spinner);
        dishTypeSpinner = view.findViewById(R.id.dish_type_spinner);

        ingredientsSection = view.findViewById(R.id.ingredients_section);
        addIngredientButton = view.findViewById(R.id.add_ingredient_button);
        saveRecipeButton = view.findViewById(R.id.save_recipe_button);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Dummy data for spinners
        String[] mealTypes = {"Breakfast", "Lunch", "Dinner", "Snack", "Tea Time"};
        String[] cuisineTypes = {"American", "Asian", "British", "Caribbean", "Central Europe", "Chinese", "Eastern Europe", "French", "Indian", "Italian", "Japanese", "Kosher", "Mediterranean", "Mexican", "Middle Eastern", "Nordic", "South American", "South East Asian"};
        String[] dishTypes = {"Biscuits and cookies", "Bread", "Cereals", "Condiments and sauces", "Desserts", "Drinks", "Main course", "Pancake", "Preps", "Preserve", "Salad", "Sandwiches", "Side dish", "Soup", "Starter", "Sweets"};

        // Fetch and populate spinners
        populateSpinners(mealTypes, cuisineTypes, dishTypes);

        // Add ingredient fields dynamically
        addIngredientButton.setOnClickListener(v -> addIngredientField());

        // Handle the recipe submission
        saveRecipeButton.setOnClickListener(v -> saveRecipeToFirestore());

        return view;
    }

    // Populate Spinners
    private void populateSpinners(String[] mealTypes, String[] cuisineTypes, String[] dishTypes) {
        ArrayAdapter<String> mealAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, mealTypes);
        mealAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealTypeSpinner.setAdapter(mealAdapter);

        ArrayAdapter<String> cuisineAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, cuisineTypes);
        cuisineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cuisineTypeSpinner.setAdapter(cuisineAdapter);

        ArrayAdapter<String> dishAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, dishTypes);
        dishAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dishTypeSpinner.setAdapter(dishAdapter);
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
        // Extract values from form fields
        String mealType = mealTypeSpinner.getSelectedItem().toString();
        String cuisineType = cuisineTypeSpinner.getSelectedItem().toString();

/*        // Get the selected dish type and store it as a single-entry list
        String dishType = dishTypeSpinner.getSelectedItem().toString();
        List<String> dishTypes = new ArrayList<>(); // Create a list for dish types
        dishTypes.add(dishType); // Add the selected dish type to the list*/

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
        recipeData.put("mealType", mealType);
        recipeData.put("cuisineType", cuisineType);
/*
        recipeData.put("dishType", dishTypes); // Store dishTypes as a List<String> with a single entry
*/
        recipeData.put("ingredientsList", ingredientsList);

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
