package com.fyp.dietandnutritionapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BMICalculatorController extends Fragment {
    private FirebaseUser currentUser;
    private EditText heightInput, weightInput, ageInput;
    private TextView bmiResultValue, bmrValue, calorieValue, bmiAdvice;
    private Button pastRecordButton;
    private ImageButton buttonMale, buttonFemale;
    private String selectedGender = ""; // No default gender
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private List<BMIEntity> bmiList = new ArrayList<>();
    private NotificationUController notificationUController;
    private TextView notificationBadgeTextView;// Initialize the BMI list

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calculate_bmi, container, false);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        heightInput = view.findViewById(R.id.height_input);
        weightInput = view.findViewById(R.id.weight_input);
        ageInput = view.findViewById(R.id.age_input);
        Button calculateButton = view.findViewById(R.id.calculate_button);
        bmiResultValue = view.findViewById(R.id.bmi_result_value);
        bmrValue = view.findViewById(R.id.bmr_value);
        calorieValue = view.findViewById(R.id.Calorie_Value);
        pastRecordButton = view.findViewById(R.id.PastRecord_button);
        buttonMale = view.findViewById(R.id.button_male);
        buttonFemale = view.findViewById(R.id.button_female);
        bmiAdvice = view.findViewById(R.id.bmi_advice);


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

        String userId = "";


        // Set button backgrounds
        updateButtonBackground(buttonMale, false);
        updateButtonBackground(buttonFemale, false);

        buttonMale.setOnClickListener(v -> {
            selectedGender = "Male";
            updateButtonBackground(buttonMale, true);
            updateButtonBackground(buttonFemale, false);
        });

        buttonFemale.setOnClickListener(v -> {
            selectedGender = "Female";
            updateButtonBackground(buttonFemale, true);
            updateButtonBackground(buttonMale, false);
        });

        calculateButton.setOnClickListener(v -> calculateBMIAndBMR());

        pastRecordButton.setOnClickListener(v -> {
            BMIPastRecordController pastRecordFragment = new BMIPastRecordController(bmiList);
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, pastRecordFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void updateButtonBackground(ImageButton button, boolean isSelected) {
        if (isSelected) {
            button.setBackgroundColor(Color.parseColor("#FFDDDD")); // Selected color
        } else {
            button.setBackgroundColor(Color.TRANSPARENT); // Default color
        }
    }

    private void calculateBMIAndBMR() {
        String heightText = heightInput.getText().toString();
        String weightText = weightInput.getText().toString();
        String ageText = ageInput.getText().toString();

        // Check if gender is selected
        if (selectedGender.isEmpty()) {
            Toast.makeText(getActivity(), "Please select your gender", Toast.LENGTH_SHORT).show();
            return;
        }

        if (heightText.isEmpty() || weightText.isEmpty() || ageText.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Read height in cm and weight in kg
        double heightInCm = Double.parseDouble(heightText); // height in cm
        double weightInKg = Double.parseDouble(weightText); // weight in kg
        int age = Integer.parseInt(ageText);

        // Calculate BMI using height in cm
        double bmi = (weightInKg * 10000) / (heightInCm * heightInCm); // BMI calculation
        bmiResultValue.setText(String.format("%.1f", bmi));
        addNewBMIRecord(bmi, weightInKg, heightInCm, age);

        // Calculate BMR
        double bmr;
        if (selectedGender.equals("Male")) {
            bmr = 10 * weightInKg + 6.25 * heightInCm - 5 * age + 5; // height in cm
        } else { // Female
            bmr = 10 * weightInKg + 6.25 * heightInCm - 5 * age - 161; // height in cm
        }
        bmrValue.setText(String.format("%.2f kcal/day", bmr));
        double calorieMaintenance = bmr * 1.2; // Assuming sedentary
        calorieValue.setText(String.format("%.2f kcal/day", calorieMaintenance));

        // Set the advice based on the BMI value
        setBmiAdvice(bmi);
    }

    private void addNewBMIRecord(double bmi, double weight, double height, int age) {
        String userId = auth.getCurrentUser().getUid();
        Map<String, Object> bmiData = new HashMap<>();
        bmiData.put("bmi", bmi);
        bmiData.put("weight", weight);
        bmiData.put("height", height);
        bmiData.put("age", age);
        bmiData.put("timestamp", System.currentTimeMillis());
        bmiData.put("user_id", userId);
        bmiData.put("gender", selectedGender); // Add gender to the record

        db.collection("BMIRecord")
                .add(bmiData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(requireContext(), "BMI record added.", Toast.LENGTH_SHORT).show();
                    // Add the new record to the local list
                    bmiList.add(new BMIEntity(bmi, System.currentTimeMillis(), userId));
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error adding BMI record: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setBmiAdvice(double bmi) {
        String advice;
        if (bmi < 18.5) {
            advice = "Underweight. Consider consulting a healthcare provider.";
        } else if (bmi < 24.9) {
            advice = "Healthy weight. Keep it up!";
        } else if (bmi < 29.9) {
            advice = "Overweight. Consider a balanced diet and exercise.";
        } else {
            advice = "Obese. It’s advisable to consult a healthcare provider.";
        }
        bmiAdvice.setText(advice);
    }
}
