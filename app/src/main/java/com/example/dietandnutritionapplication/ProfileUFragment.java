package com.example.dietandnutritionapplication;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

public class ProfileUFragment extends Fragment {

    private EditText fullNameData, dateOfBirthData, phoneNumberData, emailAddressData, healthGoalsData, dailyCalorieLimitData, currentWeightData, currentHeightData;
    private Spinner genderSpinner, dietaryPreferencesSpinner, allergiesSpinner, activityLevelSpinner;
    private Button saveButton;

    private Profile userProfile;  // Profile object to hold user data

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_uprofile, container, false);

        // Initialize UI elements
        fullNameData = view.findViewById(R.id.full_name_data);
        dateOfBirthData = view.findViewById(R.id.date_of_birth_data);
        phoneNumberData = view.findViewById(R.id.phone_number_data);
        emailAddressData = view.findViewById(R.id.email_address_data);
        healthGoalsData = view.findViewById(R.id.health_goals_data);
        dailyCalorieLimitData = view.findViewById(R.id.daily_calorie_limit_data);
        currentWeightData = view.findViewById(R.id.current_weight_data);
        currentHeightData = view.findViewById(R.id.current_height_data);

        // Initialize Spinners
        genderSpinner = view.findViewById(R.id.gender_spinner);
        dietaryPreferencesSpinner = view.findViewById(R.id.dietary_preferences_spinner);
        allergiesSpinner = view.findViewById(R.id.allergies_spinner);
        activityLevelSpinner = view.findViewById(R.id.activity_level_spinner);

        // Set up Spinner options
        setUpSpinners();

        // Load profile data (assume you have a user profile)
        userProfile = new Profile("John", "Doe", "john_doe", "123456789", "password", "john.doe@example.com", "Male", "User", "2022-01-01");
        loadProfileData(userProfile);

        // Initialize Save Button
        saveButton = view.findViewById(R.id.save_button);
        saveButton.setOnClickListener(v -> saveProfileData());

        // Date Picker for Date of Birth
        dateOfBirthData.setOnClickListener(v -> showDatePickerDialog());

        return view;
    }

    private void setUpSpinners() {
        // Gender spinner setup
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        // Dietary Preferences spinner setup
        ArrayAdapter<CharSequence> dietaryAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.dietary_preferences_array, android.R.layout.simple_spinner_item);
        dietaryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dietaryPreferencesSpinner.setAdapter(dietaryAdapter);

        // Allergies spinner setup
        ArrayAdapter<CharSequence> allergiesAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.allergies_array, android.R.layout.simple_spinner_item);
        allergiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        allergiesSpinner.setAdapter(allergiesAdapter);

        // Activity Level spinner setup
        ArrayAdapter<CharSequence> activityLevelAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.activity_level_array, android.R.layout.simple_spinner_item);
        activityLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityLevelSpinner.setAdapter(activityLevelAdapter);
    }

    private void loadProfileData(Profile profile) {
        // Load data into UI elements
        fullNameData.setText(profile.getFirstName() + " " + profile.getLastName());
        dateOfBirthData.setText(profile.getDob());
        phoneNumberData.setText(profile.getPhoneNumber());
        emailAddressData.setText(profile.getEmail());
        healthGoalsData.setText("Lose Weight");
        dailyCalorieLimitData.setText("2000");
        currentWeightData.setText("70 kg");
        currentHeightData.setText("175 cm");

        // Set Spinner selections
        setSpinnerSelection(genderSpinner, profile.getGender());
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        for (int position = 0; position < adapter.getCount(); position++) {
            if (adapter.getItem(position).toString().equals(value)) {
                spinner.setSelection(position);
                break;
            }
        }
    }

    private void showDatePickerDialog() {
        // Date picker dialog
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
            dateOfBirthData.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
        }, year, month, day);
        datePickerDialog.show();
    }

    private void saveProfileData() {
        // Save user input to Profile object
        String[] name = fullNameData.getText().toString().split(" ");
        userProfile.setFirstName(name[0]);
        userProfile.setLastName(name.length > 1 ? name[1] : "");
        userProfile.setPhoneNumber(phoneNumberData.getText().toString());
        userProfile.setEmail(emailAddressData.getText().toString());
        userProfile.setDob(dateOfBirthData.getText().toString());
        userProfile.setGender(genderSpinner.getSelectedItem().toString());

        // Save userProfile to database or other storage
        // For now, we just print the profile data
        System.out.println(userProfile.toString());
    }
}
