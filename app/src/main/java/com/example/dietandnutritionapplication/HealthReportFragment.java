package com.example.dietandnutritionapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView; // Ensure you import TextView
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HealthReportFragment extends Fragment {

    private FirebaseFirestore db;
    private Button dailyButton, weeklyButton, monthlyButton, yearlyButton;
    private PieChart pieChart;
    private TextView adviceTextView; // TextView for nutritional advice
    private TextView averageCalories;
    private TextView averageProteins;// TextView for average calorie intake

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.health_report, container, false);

        db = FirebaseFirestore.getInstance();
        pieChart = view.findViewById(R.id.pie_chart);
        adviceTextView = view.findViewById(R.id.health_advice_text); // Initialize TextView for advice
        averageCalories = view.findViewById(R.id.averageCalories); // Initialize TextView for average calories
        averageProteins = view.findViewById(R.id.averageProtein); // Initialize TextView for average calories

        dailyButton = view.findViewById(R.id.button_daily);
        weeklyButton = view.findViewById(R.id.button_weekly);
        monthlyButton = view.findViewById(R.id.button_monthly);
        yearlyButton = view.findViewById(R.id.button_yearly);

        // Set onClickListeners for each button to fetch data and display pie chart
        dailyButton.setOnClickListener(v -> fetchNutritionalData("daily"));
        weeklyButton.setOnClickListener(v -> fetchNutritionalData("weekly"));
        monthlyButton.setOnClickListener(v -> fetchNutritionalData("monthly"));
        yearlyButton.setOnClickListener(v -> fetchNutritionalData("yearly"));

        return view;
    }

    private void fetchNutritionalData(String period) {
        // Fetch nutritional data based on period (daily, weekly, etc.)
        db.collection("MealRecords") // replace with your Firestore collection
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> mealLogs = queryDocumentSnapshots.getDocuments();
                    Map<String, Float> breakdown = calculateBreakdown(mealLogs, period);
                    storeAverageInFirestore(period, breakdown);
                    displayPieChart(breakdown);
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show());
    }

    private Map<String, Float> calculateBreakdown(List<DocumentSnapshot> mealLogs, String period) {
        // Nutrient totals
        float totalProtein = 0f;
        float totalCarbs = 0f;
        float totalFat = 0f;
        float totalCalories = 0f; // New total for calories
        int count = 0;

        // Get the current time to filter by date
        long currentTime = System.currentTimeMillis();

        // Define the period time window in milliseconds (1 day, 1 week, etc.)
        long timeWindow = 0;
        switch (period) {
            case "daily":
                timeWindow = 24 * 60 * 60 * 1000; // 1 day in milliseconds
                break;
            case "weekly":
                timeWindow = 7 * 24 * 60 * 60 * 1000; // 1 week in milliseconds
                break;
            case "monthly":
                timeWindow = 30L * 24 * 60 * 60 * 1000; // 1 month in milliseconds (approx.)
                break;
            case "yearly":
                timeWindow = 365L * 24 * 60 * 60 * 1000; // 1 year in milliseconds
                break;
        }

        // Filter meal logs based on the selected time window
        for (DocumentSnapshot log : mealLogs) {
            // Assuming mealLogs contains a timestamp field for when the meal was logged
            if (log.contains("createdDate")) { // Check if createdDate exists
                long mealTime = log.getTimestamp("createdDate").toDate().getTime();
                if (currentTime - mealTime <= timeWindow) {
                    // Fetch macronutrient data (assuming fields named 'proteins', 'fats', 'carbs', and 'calories')
                    Double protein = log.getDouble("proteins");
                    Double carbs = log.getDouble("carbs");
                    Double fat = log.getDouble("fats");
                    Double calories = log.getDouble("calories"); // Fetch calories

                    // Add values to totals if they are not null
                    if (protein != null) totalProtein += protein.floatValue();
                    if (carbs != null) totalCarbs += carbs.floatValue();
                    if (fat != null) totalFat += fat.floatValue();
                    if (calories != null) totalCalories += calories.floatValue(); // Add calories

                    count++;
                }
            }
        }

        // Avoid division by zero
        if (count == 0) {
            // Set the average calories TextView to zero if no records are found
            averageCalories.setText("0");
            return Map.of("Protein", 0f, "Carbs", 0f, "Fat", 0f); // No data in the selected period
        }

        // Calculate average breakdown
        float avgProtein = totalProtein / count;
        float avgCarbs = totalCarbs / count;
        float avgFat = totalFat / count;
        float avgCalories = totalCalories / count; // Calculate average calories

        // Update the average calories TextView
        averageCalories.setText(String.valueOf(Math.round(avgCalories)));
        averageProteins.setText(String.valueOf(Math.round(avgProtein)));// Round and display average calories

        // Generate nutritional advice based on averages
        generateNutritionalAdvice(avgProtein, avgCarbs, avgFat);

        // Return macronutrient breakdown as a Map
        return Map.of(
                "Protein", avgProtein,
                "Carbs", avgCarbs,
                "Fat", avgFat
        );
    }

    private void storeAverageInFirestore(String period, Map<String, Float> breakdown) {
        // Store calculated average back into Firestore for the specified period (daily, weekly, monthly, yearly)
        db.collection("healthReport").document(period)  // period is either "daily", "weekly", "monthly", or "yearly"
                .set(breakdown)  // 'breakdown' is the map containing nutritional data
                .addOnSuccessListener(aVoid -> {
                    // Success callback - show a message indicating the data was successfully stored
                    Toast.makeText(getContext(), "Stored " + period + " average successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Failure callback - show a message indicating an error occurred
                    Toast.makeText(getContext(), "Failed to store " + period + " average", Toast.LENGTH_SHORT).show();
                });
    }

    private void displayPieChart(Map<String, Float> breakdown) {
        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Float> entry : breakdown.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Nutritional Breakdown");

        // Define colors for different nutritional categories
        List<Integer> colors = new ArrayList<>();
        colors.add(Color.BLUE); // Color for Protein
        colors.add(Color.GREEN); // Color for Carbs
        colors.add(Color.RED);   // Color for Fat

        // Set the colors to the dataset
        dataSet.setColors(colors);

        PieData pieData = new PieData(dataSet);

        pieChart.setData(pieData);
        Description description = new Description();
        description.setText("Nutritional Breakdown");
        pieChart.setDescription(description);
        pieChart.invalidate(); // Refresh chart
    }

    private void generateNutritionalAdvice(float protein, float carbohydrates, float fats) {
        String advice = "";

        // Example thresholds for generating advice
        if (carbohydrates > 60) {
            advice = "Your carbohydrate intake is high. Consider reducing refined carbs like white bread and sugary snacks, " +
                    "and opt for whole grains, fruits, and vegetables instead.";
        } else if (protein < 15) {
            advice = "Your protein intake is low. Add more protein-rich foods such as lean meats, fish, eggs, " +
                    "and legumes to support muscle health and satiety.";
        } else if (fats > 30) {
            advice = "Your fat intake is high. Limit saturated and trans fats found in processed foods, " +
                    "and choose healthier fats from sources like avocados, nuts, and olive oil.";
        } else {
            advice = "Good job! Your macronutrient intake is well-balanced. Keep focusing on whole foods!";
        }

        // Display advice in the TextView
        adviceTextView.setText(advice);
    }

}
