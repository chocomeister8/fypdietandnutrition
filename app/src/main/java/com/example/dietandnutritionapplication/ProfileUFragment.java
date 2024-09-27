package com.example.dietandnutritionapplication;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ProfileUFragment extends Fragment {

    private ViewUserProfileController viewUserProfileController;
    private EditText aOthersText, dpOthersText;
    private EditText calorieLimitData, userNameData, firstNameData, lastNameData, dateOfBirthData, phoneNumberData;
    private EditText emailAddressData, currentWeightData, currentHeightData;
    private Spinner genderSpinner, activityLevelSpinner, healthGoalsSpinner;
    private Button saveButton;
    private CheckBox peanutsCheckbox, dairyCheckbox, eggsCheckbox, soyCheckbox, seafoodCheckbox, wheatCheckbox, aOthersCheckbox;
    private CheckBox glutenFreeCheckBox, lactoseIntoleranceCheckBox, vegetarianCheckBox, dpOthersCheckbox;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_uprofile, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        Log.d("UserProfileFragment", "Current User: " + (currentUser != null ? currentUser.getEmail() : "No user logged in"));

        initializeUI(view);
        initializeSpinners();

        if (currentUser != null) {
            userId = currentUser.getUid();
            Log.d("First:UserProfileFragment", "User ID: " + userId);
            viewUserProfileController = new ViewUserProfileController((MainActivity) requireActivity());
            viewUserProfileController.checkUserProfileCompletion(userId, getContext(), (MainActivity) requireActivity());
            loadUserProfile();

            saveButton.setOnClickListener(v -> updateProfile(currentUser.getUid()));

        } else {
            Toast.makeText(getContext(), "No user logged in", Toast.LENGTH_SHORT).show();
        }

        ImageView logoutImage = view.findViewById(R.id.logout_icon);
        logoutImage.setOnClickListener(v -> {

            if (getActivity() instanceof MainActivity) {
                Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).switchToGuestMode();
                ((MainActivity) getActivity()).replaceFragment(new LandingFragment());
            }
        });

        dpOthersCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Show the EditText when "Others" is checked
                    dpOthersText.setVisibility(View.VISIBLE);
                } else {
                    // Hide the EditText when "Others" is unchecked
                    dpOthersText.setVisibility(View.GONE);
                    dpOthersText.setText(""); // Clear input when unchecked
                }
            }
        });

        aOthersCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Show the EditText when "Others" is checked
                    aOthersText.setVisibility(View.VISIBLE);
                } else {
                    // Hide the EditText when "Others" is unchecked
                    aOthersText.setVisibility(View.GONE);
                    aOthersText.setText(""); // Clear input when unchecked
                }
            }
        });

        dateOfBirthData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });


        return view;
    }

    private void initializeUI(View view) {
        // Initialize the text fields and button
        userNameData = view.findViewById(R.id.username_data);
        firstNameData = view.findViewById(R.id.first_name_data);
        lastNameData = view.findViewById(R.id.last_name_data);
        dateOfBirthData = view.findViewById(R.id.date_of_birth_data);
        phoneNumberData = view.findViewById(R.id.phone_number_data);
        emailAddressData = view.findViewById(R.id.email_address_data);
        genderSpinner = view.findViewById(R.id.gender_spinner);
        healthGoalsSpinner = view.findViewById(R.id.health_goals_spinner);
        currentWeightData = view.findViewById(R.id.current_weight_data);
        currentHeightData = view.findViewById(R.id.current_height_data);
        calorieLimitData = view.findViewById(R.id.daily_calorie_limit_data);
        glutenFreeCheckBox = view.findViewById(R.id.checkbox_gluten_free);
        lactoseIntoleranceCheckBox = view.findViewById(R.id.checkbox_lactose_intolerance);
        vegetarianCheckBox = view.findViewById(R.id.checkbox_vegetarian);
        dpOthersCheckbox = view.findViewById(R.id.checkbox_dp_other);

        peanutsCheckbox = view.findViewById(R.id.checkbox_peanuts);
        dairyCheckbox = view.findViewById(R.id.checkbox_dairy);
        eggsCheckbox = view.findViewById(R.id.checkbox_eggs);
        soyCheckbox = view.findViewById(R.id.checkbox_soy);
        seafoodCheckbox = view.findViewById(R.id.checkbox_seafood);
        wheatCheckbox = view.findViewById(R.id.checkbox_wheat);
        aOthersCheckbox = view.findViewById(R.id.checkbox_a_other);

        aOthersText = view.findViewById(R.id.other_a_input);
        dpOthersText = view.findViewById(R.id.other_dp_input);

        activityLevelSpinner = view.findViewById(R.id.activity_level_data);

        saveButton = view.findViewById(R.id.save_button);


    }

    private String getDietaryPreferences() {
        StringBuilder preferences = new StringBuilder();
        if (glutenFreeCheckBox.isChecked()) preferences.append("Gluten Free, ");
        if (lactoseIntoleranceCheckBox.isChecked()) preferences.append("Lactose Intolerance, ");
        if (vegetarianCheckBox.isChecked()) preferences.append("Vegetarian, ");
        if (dpOthersCheckbox.isChecked()) {
            String othersInput = dpOthersText.getText().toString().trim();
            if (!othersInput.isEmpty()) {
                preferences.append(othersInput).append(", ");
            }
        }
        // Remove trailing comma and space if present
        if (preferences.length() > 0) {
            preferences.setLength(preferences.length() - 2); // Remove last comma and space
        }
        return preferences.toString();
    }

    private String getAllergies() {
        StringBuilder allergies = new StringBuilder();
        if (peanutsCheckbox.isChecked()) allergies.append("Peanuts, ");
        if (dairyCheckbox.isChecked()) allergies.append("Dairy, ");
        if (eggsCheckbox.isChecked()) allergies.append("Eggs, ");
        if (soyCheckbox.isChecked()) allergies.append("Soy, ");
        if (seafoodCheckbox.isChecked()) allergies.append("Seafood, ");
        if (wheatCheckbox.isChecked()) allergies.append("Wheat, ");
        if (aOthersCheckbox.isChecked()) {
            String othersInput = aOthersText.getText().toString().trim();
            if (!othersInput.isEmpty()) {
                allergies.append(othersInput).append(", ");
            }
        }

        // Remove trailing comma and space if present
        if (allergies.length() > 0) {
            allergies.setLength(allergies.length() - 2); // Remove last comma and space
        }
        return allergies.toString();
    }

    private void updateProfile(String userId) {
        viewUserProfileController.getUserById(userId, new UserAccountEntity.UserFetchCallback() {
            @Override
            public void onUserFetched(User currentUser) {
                // Gather data from input fields
                String username = userNameData.getText().toString();
                String firstName = firstNameData.getText().toString();
                String lastName = lastNameData.getText().toString();
                String gender = genderSpinner.getSelectedItem().toString();
                String dob = dateOfBirthData.getText().toString();
                String phone = phoneNumberData.getText().toString();
                String email = emailAddressData.getText().toString();
                String healthGoals = healthGoalsSpinner.getSelectedItem().toString();
                double currentWeight;
                double currentHeight;
                String dietaryPreferences = getDietaryPreferences();
                String allergies = getAllergies();
                String activityLevel = activityLevelSpinner.getSelectedItem().toString();

                // Handle current weight
                try {
                    currentWeight = Double.parseDouble(currentWeightData.getText().toString());
                } catch (NumberFormatException e) {
                    currentWeight = 0; // or handle it as needed
                    Toast.makeText(getContext(), "Invalid weight input", Toast.LENGTH_SHORT).show();
                }

                // Handle current height
                try {
                    currentHeight = Double.parseDouble(currentHeightData.getText().toString());
                } catch (NumberFormatException e) {
                    currentHeight = 0; // or handle it as needed
                    Toast.makeText(getContext(), "Invalid height input", Toast.LENGTH_SHORT).show();
                }

                if (dob.isEmpty() || currentWeight == 0 || currentHeight == 0 || activityLevel.isEmpty()) {
                    Toast.makeText(getContext(), "Please complete all required fields (DOB, weight, height, activity level).", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean recalculateCalorieLimit = !dob.equals(currentUser.getDob()) ||
                        currentWeight != currentUser.getCurrentWeight() ||
                        currentHeight != currentUser.getCurrentHeight() ||
                        !activityLevel.equals(currentUser.getActivityLevel());

                int calorieLimit = currentUser.getCalorieLimit();

                if (recalculateCalorieLimit) {
                    double calorieGoal = calculateCalorieGoal(dob, currentWeight, currentHeight, activityLevel);
                    calorieLimit = (int) calorieGoal;
                    Log.d("Debug", "Recalculated Calorie Goal: " + calorieGoal);
                    Log.d("Debug", "Calorie Limit (int): " + calorieLimit);
                }

                User updatedUser = new User();
                updatedUser.setUsername(username.equals(currentUser.getUsername()) ? currentUser.getUsername() : username);
                updatedUser.setFirstName(firstName.equals(currentUser.getFirstName()) ? currentUser.getFirstName() : firstName);
                updatedUser.setLastName(lastName.equals(currentUser.getLastName()) ? currentUser.getLastName() : lastName);
                updatedUser.setDob(dob.equals(currentUser.getDob()) ? currentUser.getDob() : dob);
                updatedUser.setEmail(email.equals(currentUser.getEmail()) ? currentUser.getEmail() : email);
                updatedUser.setGender(gender.equals(currentUser.getGender()) ? currentUser.getGender() : gender);
                updatedUser.setPhoneNumber(phone.equals(currentUser.getPhoneNumber()) ? currentUser.getPhoneNumber() : phone);
                updatedUser.setHealthGoal(healthGoals.equals(currentUser.getHealthGoal()) ? currentUser.getHealthGoal() : healthGoals);
                updatedUser.setCurrentWeight(currentWeight);
                updatedUser.setCurrentHeight(currentHeight);
                updatedUser.setDietaryPreference(dietaryPreferences.equals(currentUser.getDietaryPreference()) ? currentUser.getDietaryPreference() : dietaryPreferences);
                updatedUser.setFoodAllergies(allergies.equals(currentUser.getFoodAllergies()) ? currentUser.getFoodAllergies() : allergies);
                updatedUser.setActivityLevel(activityLevel.equals(currentUser.getActivityLevel()) ? currentUser.getActivityLevel() : activityLevel);
                updatedUser.setCalorieLimit(calorieLimit);

                viewUserProfileController.updateUserProfile(userId, updatedUser, getContext());
                calorieLimitData.setText(String.valueOf(updatedUser.getCalorieLimit()));
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle failure
                Toast.makeText(getContext(), "Error fetching user data: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePickerDialog() {
        // Get the current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format the selected date as needed (e.g., "dd/MM/yyyy")
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear; // Months are 0-based
                    dateOfBirthData.setText(selectedDate); // Set the selected date in the EditText
                }, year, month, day);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }

    private void populateProfileFields(User user) {
        userNameData.setText(user.getUsername());
        firstNameData.setText(user.getFirstName());
        lastNameData.setText(user.getLastName());
        dateOfBirthData.setText(user.getDob());
        phoneNumberData.setText(user.getPhoneNumber());
        emailAddressData.setText(user.getEmail());

        currentWeightData.setText(String.valueOf(user.getCurrentWeight()));
        currentHeightData.setText(String.valueOf(user.getCurrentHeight()));
        calorieLimitData.setText(String.valueOf(user.getCalorieLimit()));

        Log.d("ProfileDebug", "DOB: " + user.getDob() + ", Weight: " + user.getCurrentWeight() + ", Height: " + user.getCurrentHeight() + ", Activity Level: " + user.getActivityLevel());
        setSpinnerSelection(healthGoalsSpinner, user.getHealthGoal());
        setSpinnerSelection(genderSpinner, user.getGender());
        setSpinnerSelection(activityLevelSpinner, user.getActivityLevel());

        setCheckBoxes(user);
    }

    private void setCheckBoxes(User user) {
        if (user.getDietaryPreference() != null) {
            glutenFreeCheckBox.setChecked(user.getDietaryPreference().contains("Gluten Free"));
            lactoseIntoleranceCheckBox.setChecked(user.getDietaryPreference().contains("Lactose Intolerance"));
            vegetarianCheckBox.setChecked(user.getDietaryPreference().contains("Vegetarian"));
            dpOthersCheckbox.setChecked(user.getDietaryPreference().contains("Others"));
        } else {
            glutenFreeCheckBox.setChecked(false);
            lactoseIntoleranceCheckBox.setChecked(false);
            vegetarianCheckBox.setChecked(false);
            dpOthersCheckbox.setChecked(false);
        }

        aOthersText.setVisibility(dpOthersCheckbox.isChecked() ? View.VISIBLE : View.GONE);

        if (user.getFoodAllergies() != null) {
            peanutsCheckbox.setChecked(user.getFoodAllergies().contains("Peanuts"));
            dairyCheckbox.setChecked(user.getFoodAllergies().contains("Dairy"));
            eggsCheckbox.setChecked(user.getFoodAllergies().contains("Eggs"));
            soyCheckbox.setChecked(user.getFoodAllergies().contains("Soy"));
            seafoodCheckbox.setChecked(user.getFoodAllergies().contains("Seafood"));
            wheatCheckbox.setChecked(user.getFoodAllergies().contains("Wheat"));
            aOthersCheckbox.setChecked(user.getFoodAllergies().contains("Others"));
        } else {
            peanutsCheckbox.setChecked(false);
            dairyCheckbox.setChecked(false);
            eggsCheckbox.setChecked(false);
            soyCheckbox.setChecked(false);
            seafoodCheckbox.setChecked(false);
            wheatCheckbox.setChecked(false);
            aOthersCheckbox.setChecked(false);
        }
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void initializeSpinners() {
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        ArrayAdapter<CharSequence> activityLevelAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.activity_levels, android.R.layout.simple_spinner_item);
        activityLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityLevelSpinner.setAdapter(activityLevelAdapter);

        ArrayAdapter<CharSequence> healthGoalAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.health_goals_array, android.R.layout.simple_spinner_item);
        healthGoalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        healthGoalsSpinner.setAdapter(healthGoalAdapter);;
    }

    private void loadUserProfile() {
        if (currentUser != null) {
            viewUserProfileController.getUserById(currentUser.getUid(), new UserAccountEntity.UserFetchCallback() {
                @Override
                public void onUserFetched(User user) {
                    populateProfileFields(user);
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(getContext(), "Error loading profile: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private double calculateCalorieGoal(String dob, double weight, double height, String activityLevel) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date birthDate = null;
        try {
            birthDate = dateFormat.parse(dob);  // Parse the date string
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;  // Handle error if date parsing fails
        }

        // Get the current date
        Calendar now = Calendar.getInstance();
        Calendar birthCalendar = Calendar.getInstance();
        birthCalendar.setTime(birthDate);

        // Calculate age
        int age = now.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR);
        if (now.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--;  // If current date is before birthday this year, subtract 1 from age
        }

        // Calculate BMR (Basal Metabolic Rate) based on weight, height, and age
        double bmr = (10 * weight) + (6.25 * height) - (5 * age) + 5; // Modify based on gender if needed

        // Activity multiplier
        double activityFactor;
        switch (activityLevel) {
            case "Sedentary: little or no exercise":
                activityFactor = 1.2;
                break;
            case "Exercise 1-3 times/week":
                activityFactor = 1.375;
                break;
            case "Exercise 4-5 times/week":
                activityFactor = 1.55;
                break;
            case "Daily exercise or intense exercise 3-4 times/week":
                activityFactor = 1.725;
                break;
            case "Intense exercise 6-7 times/week":
                activityFactor = 1.9;
                break;
            case "Very intense exercise daily, or physical job":
                activityFactor = 2.0;
                break;
            default:
                activityFactor = 1.2; // Default to sedentary
        }

        return bmr * activityFactor;  // Return the caloric limit
    }


}
