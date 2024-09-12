package com.example.dietandnutritionapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileUFragment extends Fragment {

    private TextView fullNameData, dateOfBirthData, genderData, phoneNumberData, emailAddressData;
    private TextView dietaryPreferencesData, allergiesData, healthGoalsData;
    private TextView dailyCalorieLimitData, currentWeightData, currentHeightData, activityLevelData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_uprofile, container, false);

        // Initialize Views
        fullNameData = view.findViewById(R.id.full_name_data);
        dateOfBirthData = view.findViewById(R.id.date_of_birth_data);
        genderData = view.findViewById(R.id.gender_data);
        phoneNumberData = view.findViewById(R.id.phone_number_data);
        emailAddressData = view.findViewById(R.id.email_address_data);

        dietaryPreferencesData = view.findViewById(R.id.dietary_preferences_data);
        allergiesData = view.findViewById(R.id.allergies_data);
        healthGoalsData = view.findViewById(R.id.health_goals_data);

        dailyCalorieLimitData = view.findViewById(R.id.daily_calorie_limit_data);
        currentWeightData = view.findViewById(R.id.current_weight_data);
        currentHeightData = view.findViewById(R.id.current_height_data);
        activityLevelData = view.findViewById(R.id.activity_level_data);

        // Load profile data
        loadProfileData();

        return view;
    }

    private void loadProfileData() {
        // Fetch data from database or set dummy data
        // For demonstration, use dummy data

        fullNameData.setText("John Doe");
        dateOfBirthData.setText("01/01/1990");
        genderData.setText("Male");
        phoneNumberData.setText("123-456-7890");
        emailAddressData.setText("john.doe@example.com");

        dietaryPreferencesData.setText("Vegetarian");
        allergiesData.setText("Peanuts");
        healthGoalsData.setText("Lose Weight");

        dailyCalorieLimitData.setText("2000");
        currentWeightData.setText("70 kg");
        currentHeightData.setText("175 cm");
        activityLevelData.setText("Moderate");
    }
}
