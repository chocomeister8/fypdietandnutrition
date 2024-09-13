package com.example.dietandnutritionapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Test_BMI_fragment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculate_bmi);  // Assuming this is the correct layout

        Button calculateButton = findViewById(R.id.calculate_button);
        Button pastRecordButton = findViewById(R.id.PastRecord_button);

        // Set up the intent for the "Calculate BMI" button
        calculateButton.setOnClickListener(v -> {
            Intent intent = new Intent(Test_BMI_fragment.this, BMI_Calculator_fragment.class);
            startActivity(intent);
        });

        // Set up the intent for the "Past Record" button
        pastRecordButton.setOnClickListener(v -> {
            Intent intent = new Intent(Test_BMI_fragment.this, bmi_pastRecord_fragment.class);  // Change to the correct activity for past records
            startActivity(intent);
        });
    }
}
