package com.fyp.dietandnutritionapplication;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.google.gson.annotations.SerializedName;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.view.View;
import android.content.Context;

public class ProfileUFragment extends Fragment {

    private ViewUserProfileController viewUserProfileController;
    private EditText aOthersText, dpOthersText;
    private EditText calorieLimitData, userNameData, firstNameData, lastNameData, dateOfBirthData, phoneNumberData;
    private EditText emailAddressData, currentWeightData, currentHeightData, weightGoalData;
    private Spinner genderSpinner, activityLevelSpinner, healthGoalsSpinner;
    private Button saveButton;
    private CheckBox aNoneCheckbox, peanutsCheckbox, dairyCheckbox, eggsCheckbox, soyCheckbox, seafoodCheckbox, wheatCheckbox, aOthersCheckbox;
    private CheckBox dpNoneCheckbox, glutenFreeCheckBox, lactoseIntoleranceCheckBox, vegetarianCheckBox, dpOthersCheckbox;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private String userId;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView profileImageView, chevronIcon ;
    private Button uploadImageButton, toggleAllergy;
    private static final int REQUEST_CODE_PERMISSIONS = 1001;

    private NotificationUController notificationUController;
    private TextView notificationBadgeTextView;

    private LinearLayout dietaryContainer, allergyContainer, allergySection;

    private boolean isExpanded = false;

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
            allergyContainer.setVisibility(View.VISIBLE);
            chevronIcon.setImageResource(R.drawable.ic_arrow_up);
            isExpanded = true;
            allergySection.setOnClickListener(v -> toggleAllergyContainer(v));
            uploadImageButton.setOnClickListener(v -> openFileChooser());

            saveButton.setOnClickListener(v -> updateProfile(currentUser.getUid()));

        } else {
            Toast.makeText(getContext(), "No user logged in", Toast.LENGTH_SHORT).show();
        }

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


        ImageView notiImage = view.findViewById(R.id.noti_icon);
        notiImage.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NotificationUFragment())
                    .addToBackStack(null)
                    .commit();

        });



        ImageView logoutImage = view.findViewById(R.id.logout_icon);
        logoutImage.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MealPref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            FirebaseAuth.getInstance().signOut();

            if (getActivity() instanceof MainActivity) {
                UserMealRecordFragment userMealRecordFragment = (UserMealRecordFragment) getFragmentManager().findFragmentByTag("UserMealRecordFragment");
                if (userMealRecordFragment != null) {
                    userMealRecordFragment.clearMealLogUI();
                }
                Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).switchToGuestMode();
                ((MainActivity) getActivity()).replaceFragment(new LoginFragment());
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
        weightGoalData = view.findViewById(R.id.weight_goal);

        activityLevelSpinner = view.findViewById(R.id.activity_level_data);

        saveButton = view.findViewById(R.id.save_button);

        profileImageView = view.findViewById(R.id.imageView);
        uploadImageButton = view.findViewById(R.id.upload_picture_button);

        dietaryContainer = view.findViewById(R.id.dietaryContainer);
        allergyContainer = view.findViewById(R.id.allergyContainer);

        chevronIcon = view.findViewById(R.id.chevronIcon);
        allergySection = view.findViewById(R.id.allergy_section);
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            if (selectedImageUri != null) {
                Glide.with(getContext())
                        .load(selectedImageUri)
                        .into(profileImageView);

                // Store the URI for later use
                imageUri = selectedImageUri;
                Log.d("ProfileImage", "Selected image URI: " + selectedImageUri.toString());

                uploadImageToFirebaseStorage();
            } else {
                Log.e("ProfileImage", "Selected image URI is null");
                Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("ProfileImage", "Image selection failed or request code mismatch");
        }
    }

    private void uploadImageToFirebaseStorage() {
        if (imageUri != null) {
            Log.d("ProfileImage", "Uploading image to Firebase Storage");

            // Show a progress dialog or progress bar
            ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading Image");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference()
                    .child("profile_pictures/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");

            storageReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d("ProfileImage", "Image uploaded successfully");
                        storageReference.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    Log.d("ProfileImage", "Download URL obtained: " + uri.toString());
                                    viewUserProfileController.uploadProfilePic(uri.toString(), getContext());
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("ProfileImage", "Failed to get download URL: " + e.getMessage());
                                    Toast.makeText(getContext(), "Failed to retrieve download URL", Toast.LENGTH_SHORT).show();
                                })
                                .addOnCompleteListener(task -> progressDialog.dismiss()); // Dismiss on completion
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ProfileImage", "Failed to upload image: " + e.getMessage());
                        Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss(); // Dismiss on failure
                    });
        } else {
            Log.e("ProfileImage", "Image URI is null");
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void getCheckboxOptions(User user) {
        viewUserProfileController.getCheckboxOptions(getContext(), dietaryContainer, allergyContainer, user);
    }

    private String getDietaryPreferences() {
        StringBuilder preferences = new StringBuilder();

        for (int i = 0; i < dietaryContainer.getChildCount(); i++) {
            View view = dietaryContainer.getChildAt(i);
            if (view instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) view;
                if (checkBox.isChecked()) {
                    preferences.append(checkBox.getText().toString()).append(", ");
                }
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

        for (int i = 0; i < allergyContainer.getChildCount(); i++) {
            View view = allergyContainer.getChildAt(i);
            if (view instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) view;
                if (checkBox.isChecked()) {
                    allergies.append(checkBox.getText().toString()).append(", ");
                }
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
                double weightGoal;
                double currentWeight;
                double currentHeight;
                String dietaryPreferences = getDietaryPreferences();
                String allergies = getAllergies();
                String activityLevel = activityLevelSpinner.getSelectedItem().toString();

                // Handle current weight
                try {
                    currentWeight = Double.parseDouble(currentWeightData.getText().toString());
                } catch (NumberFormatException e) {
                    currentWeight = 0;
                    Toast.makeText(getContext(), "Invalid weight input", Toast.LENGTH_SHORT).show();
                }

                // Handle current height
                try {
                    currentHeight = Double.parseDouble(currentHeightData.getText().toString());
                } catch (NumberFormatException e) {
                    currentHeight = 0;
                    Toast.makeText(getContext(), "Invalid height input", Toast.LENGTH_SHORT).show();
                }

                try {
                    weightGoal = Double.parseDouble(weightGoalData.getText().toString());
                } catch (NumberFormatException e) {
                    weightGoal = 0;
                    Toast.makeText(getContext(), "Invalid weight input", Toast.LENGTH_SHORT).show();
                }


                if (dob.isEmpty() || currentWeight == 0 || currentHeight == 0 ||
                        "Select your activity level".equals(activityLevel) ||
                        "Select your health goal".equals(healthGoals)) {
                    Toast.makeText(getContext(), "Please complete all required fields (DOB, weight, height, activity level, health goal).", Toast.LENGTH_SHORT).show();
                    return;
                }

                healthGoalsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedHealthGoal = parent.getItemAtPosition(position).toString();

                        validateHealthGoal(currentUser.getCurrentWeight(), currentUser.getCurrentHeight(), selectedHealthGoal);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                boolean recalculateCalorieLimit = !dob.equals(currentUser.getDob()) ||
                        currentWeight != currentUser.getCurrentWeight() ||
                        currentHeight != currentUser.getCurrentHeight() ||
                        !activityLevel.equals(currentUser.getActivityLevel())||
                        !healthGoals.equals(currentUser.getHealthGoal()) || !gender.equals(currentUser.getGender());

                int calorieLimit = currentUser.getCalorieLimit();

                if (recalculateCalorieLimit) {
                    double calorieGoal = calculateCalorieGoal(gender, dob, currentWeight, currentHeight, activityLevel, healthGoals);
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
                updatedUser.setWeightGoal(weightGoal);

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

        if (user != null && profileImageView != null) {
            String profileImageUrl = user.getProfileImageUrl();

            if (profileImageUrl != null) {
                Glide.with(this)
                        .load(profileImageUrl)
                        .placeholder(R.drawable.profile)  // Placeholder if the image is loading
                        .into(profileImageView);  // Ensure profileImageView is not null
            } else {
                // If the URL is null, set a default image or placeholder
                profileImageView.setImageResource(R.drawable.profile);
            }
        } else {
            // Log an error or handle the null case accordingly
            if (user == null) {
                Log.e("ProfileUFragment", "User object is null");
            }
            if (profileImageView == null) {
                Log.e("ProfileUFragment", "ImageView is null");
            }
        }


        currentWeightData.setText(String.valueOf(user.getCurrentWeight()));
        currentHeightData.setText(String.valueOf(user.getCurrentHeight()));
        calorieLimitData.setText(String.valueOf(user.getCalorieLimit()));
        weightGoalData.setText(String.valueOf(user.getWeightGoal()));

        Log.d("ProfileDebug", "DOB: " + user.getDob() + ", Weight: " + user.getCurrentWeight() + ", Height: " + user.getCurrentHeight() + ", Activity Level: " + user.getActivityLevel());
        setSpinnerSelection(healthGoalsSpinner, user.getHealthGoal());
        setSpinnerSelection(genderSpinner, user.getGender());
        setSpinnerSelection(activityLevelSpinner, user.getActivityLevel());

        setCheckBoxes(user);
    }

    private void setCheckBoxes(User user) {
        // Clear existing checkboxes
        dietaryContainer.removeAllViews();
        allergyContainer.removeAllViews();

        // Fetch dietary preferences from the user's data
        if (user.getDietaryPreference() != null) {
            String[] diets = user.getDietaryPreference().split(", ");
            for (String diet : diets) {
                CheckBox dietCheckBox = new CheckBox(getContext());
                dietCheckBox.setText(diet);
                dietCheckBox.setChecked(true);
                dietaryContainer.addView(dietCheckBox);
            }
        }

        // Fetch allergies/health preferences from the user's data
        if (user.getFoodAllergies() != null) {
            String[] healthPreferences = user.getFoodAllergies().split(", ");
            for (String health : healthPreferences) {
                CheckBox healthCheckBox = new CheckBox(getContext());
                healthCheckBox.setText(health);
                healthCheckBox.setChecked(true);  // Mark them as checked
                allergyContainer.addView(healthCheckBox);
            }
        }
    }

    public void toggleAllergyContainer(View view) {

        if (isExpanded) {
            allergyContainer.setVisibility(View.GONE);
            chevronIcon.setImageResource(R.drawable.ic_arrow_down);
        } else {
            allergyContainer.setVisibility(View.VISIBLE);
            chevronIcon.setImageResource(R.drawable.ic_arrow_up);
        }

        isExpanded = !isExpanded;
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
                    getCheckboxOptions(user);
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(getContext(), "Error loading profile: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private double calculateCalorieGoal(String gender, String dob, double weight, double height, String activityLevel, String healthGoal) {
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
        double bmr;
        if ("Male".equalsIgnoreCase(gender)) {
            bmr = (10 * weight) + (6.25 * height) - (5 * age) + 5;
        } else if ("Female".equalsIgnoreCase(gender)) {
            bmr = (10 * weight) + (6.25 * height) - (5 * age) - 161;
        } else {
            bmr = (10 * weight) + (6.25 * height) - (5 * age) - 78;
        }

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

        double dailyCalories = bmr * activityFactor;

        switch (healthGoal) {
            case "Lose Weight":
                dailyCalories *= 0.80;
                break;
            case "Slowly Lose Weight":
                dailyCalories *= 0.90;
                break;
            case "Maintain Current Weight":
                break;
            case "Build Muscle (Moderate Gain)":
                dailyCalories += 300;
                break;
            case "Build Muscle (Aggressive Gain)":
                dailyCalories += 500;
                break;
            default:
                break;
        }


        return dailyCalories;
    }

    private double calculateBMI(double weight, double height) {
        // Convert height from cm to meters
        height = height / 100;
        return weight / (height * height);
    }

    private void validateHealthGoal(double weight, double height, String healthGoal) {
        double bmi = calculateBMI(weight, height);


        double bmiThreshold = 18.5;


        if (bmi < bmiThreshold && ("Lose Weight".equals(healthGoal) || "Slowly Lose Weight".equals(healthGoal))) {
            new AlertDialog.Builder(getContext())
                    .setTitle("BMI Warning")
                    .setMessage("Your BMI is below the recommended range. Losing weight is not recommended. Do you want to reselect your health goal?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            healthGoalsSpinner.setSelection(0);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }



}
