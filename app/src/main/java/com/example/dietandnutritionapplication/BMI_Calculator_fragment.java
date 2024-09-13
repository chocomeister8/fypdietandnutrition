package com.example.dietandnutritionapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BMI_Calculator_fragment extends AppCompatActivity {

    private EditText heightInput, weightInput, ageInput;
    private TextView bmiResultValue, bmrValue, calorieValue;
    private Button pastRecordButton;  // Declare the button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculate_bmi);

        // Initialize the input fields
        heightInput = findViewById(R.id.height_input);
        weightInput = findViewById(R.id.weight_input);
        ageInput = findViewById(R.id.age_input);
        Button calculateButton = findViewById(R.id.calculate_button);
        bmiResultValue = findViewById(R.id.bmi_result_value);
        bmrValue = findViewById(R.id.bmr_value);
        calorieValue = findViewById(R.id.Calorie_Value);

        // Initialize the PastRecord button
        pastRecordButton = findViewById(R.id.PastRecord_button);

        // Calculate Button Logic
        calculateButton.setOnClickListener(v -> calculateBMIAndBMR());

        // Set up click listener for the "Past Record" button
        pastRecordButton.setOnClickListener(v -> {
            // Handle the button click, show a message, or navigate to another screen
            Toast.makeText(this, "Viewing past records...", Toast.LENGTH_SHORT).show();

            // Optionally, navigate to another activity
            Intent intent = new Intent(BMI_Calculator_fragment.this, bmi_pastRecord_fragment.class);
            startActivity(intent);
        });
    }

    private void calculateBMIAndBMR() {
        // Extract height, weight, and age values
        String heightText = heightInput.getText().toString();
        String weightText = weightInput.getText().toString(); // Corrected line
        String ageText = ageInput.getText().toString();

        if (heightText.isEmpty() || weightText.isEmpty() || ageText.isEmpty()) {
            // Handle empty inputs by showing a Toast message
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
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
