package com.example.dietandnutritionapplication;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;

public class ProfileUFragment extends Fragment {

    private TextView userNameData, fullNameData, dateOfBirthData, phoneNumberData, emailAddressData, healthGoalsData, dailyCalorieLimitData, currentWeightData, currentHeightData;
    private TextView genderData, dietaryPreferencesData, allergiesData, activityLevelData;
    private Button editButton;
    private UserAccountEntity userAccountEntity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_uprofile, container, false);

        initializeUI(view);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("loggedInUserEmail", null);

        userAccountEntity = new UserAccountEntity();

        if (userEmail != null) {
            fetchUserProfile(userEmail);
        } else {
            Toast.makeText(getContext(), "No user logged in", Toast.LENGTH_SHORT).show();
        }

        ImageView logoutImage = view.findViewById(R.id.logout_icon);
        logoutImage.setOnClickListener(v -> {
            // Switch to guest mode (for nutritionists as guests)
            if (getActivity() instanceof MainActivity) {
                Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).switchToGuestMode();
                ((MainActivity) getActivity()).replaceFragment(new LandingFragment());
            }
        });

        return view;
    }

    private void initializeUI(View view) {
        userNameData = view.findViewById(R.id.username_data);
        fullNameData = view.findViewById(R.id.full_name_data);
        dateOfBirthData = view.findViewById(R.id.date_of_birth_data);
        phoneNumberData = view.findViewById(R.id.phone_number_data);
        emailAddressData = view.findViewById(R.id.email_address_data);
        healthGoalsData = view.findViewById(R.id.health_goals_data);
        dailyCalorieLimitData = view.findViewById(R.id.daily_calorie_limit_data);
        currentWeightData = view.findViewById(R.id.current_weight_data);
        currentHeightData = view.findViewById(R.id.current_height_data);
        genderData = view.findViewById(R.id.gender_data);
        dietaryPreferencesData = view.findViewById(R.id.dietary_preferences_data);
        allergiesData = view.findViewById(R.id.allergies_data);
        activityLevelData = view.findViewById(R.id.activity_level_data);
    }

    private void fetchUserProfile(String userEmail) {
        Log.d("ProfileUFragment", "Attempting to fetch user profile for email: " + userEmail);
        userAccountEntity.fetchAccounts(new UserAccountEntity.DataCallback() {
            @Override
            public void onSuccess(ArrayList<Profile> accounts) {
                Log.d("ProfileUFragment", "Successfully fetched accounts.");
                for (Profile profile : accounts) {
                    if (profile instanceof User) {
                        User user = (User) profile;
                        Log.d("ProfileUFragment", "Fetched user: " + user.getEmail());
                        if (user.getEmail().equals(userEmail)) {
                            loadProfileData(user);
                            return;
                        }
                    }
                }
                Log.d("ProfileUFragment", "User profile not found.");
                Toast.makeText(getContext(), "User profile not found", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed to fetch profile data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProfileData(User user) {
        Log.d("ProfileUFragment", "Loading user data: " + user.getEmail());
        userNameData.setText(user.getUsername());
        fullNameData.setText(user.getFirstName() + " " + user.getLastName());
        dateOfBirthData.setText(user.getDob());
        phoneNumberData.setText(user.getPhoneNumber());
        emailAddressData.setText(user.getEmail());
        healthGoalsData.setText(user.getHealthGoal());
        dailyCalorieLimitData.setText(String.valueOf(user.getCalorieLimit()));
        currentWeightData.setText(String.valueOf(user.getCurrentWeight()));
        currentHeightData.setText(String.valueOf(user.getCurrentHeight()));
        genderData.setText(user.getGender());
        dietaryPreferencesData.setText(user.getDietaryPreference());
        allergiesData.setText(user.getFoodAllergies());
        activityLevelData.setText(user.getActivityLevel());
    }


}
