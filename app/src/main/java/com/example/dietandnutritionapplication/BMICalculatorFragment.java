package com.example.dietandnutritionapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BMICalculatorFragment extends Fragment {

    private EditText heightInput, weightInput, ageInput;
    private TextView bmiResultValue, bmrValue, calorieValue;
    private Button pastRecordButton;  // Declare the button

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.calculate_bmi, container, false);

        // Initialize the input fields
        heightInput = view.findViewById(R.id.height_input);
        weightInput = view.findViewById(R.id.weight_input);
        ageInput = view.findViewById(R.id.age_input);
        Button calculateButton = view.findViewById(R.id.calculate_button);
        bmiResultValue = view.findViewById(R.id.bmi_result_value);
        bmrValue = view.findViewById(R.id.bmr_value);
        calorieValue = view.findViewById(R.id.Calorie_Value);

        // Initialize the PastRecord button
        pastRecordButton = view.findViewById(R.id.PastRecord_button);

        // Calculate Button Logic
        calculateButton.setOnClickListener(v -> calculateBMIAndBMR());

        // Set up click listener for the "Past Record" button
        pastRecordButton.setOnClickListener(v -> {
            // Handle the button click, show a message, or navigate to another screen
            Toast.makeText(getActivity(), "Viewing past records...", Toast.LENGTH_SHORT).show();

            // Optionally, navigate to another fragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new BMIPastRecordFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void calculateBMIAndBMR() {
        // Extract height, weight, and age values
        String heightText = heightInput.getText().toString();
        String weightText = weightInput.getText().toString();
        String ageText = ageInput.getText().toString();

        if (heightText.isEmpty() || weightText.isEmpty() || ageText.isEmpty()) {
            // Handle empty inputs by showing a Toast message
            Toast.makeText(getActivity(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double heightInMeters = Double.parseDouble(heightText) / 100.0;
        double weightInKg = Double.parseDouble(weightText);
        int age = Integer.parseInt(ageText);

        // Calculate BMI
        double bmi = weightInKg / (heightInMeters * heightInMeters);
        bmiResultValue.setText(String.format("%.1f", bmi));

        // Calculate BMR (for male as an example)
        double bmr = 10 * weightInKg + 6.25 * (heightInMeters * 100) - 5 * age + 5;
        bmrValue.setText(String.format("%.2f kcal/day", bmr));

        // Assuming calorie maintenance is based on BMR * 1.2 (sedentary activity level)
        double calorieMaintenance = bmr * 1.2;
        calorieValue.setText(String.format("%.2f kcal/day", calorieMaintenance));
    }
}
