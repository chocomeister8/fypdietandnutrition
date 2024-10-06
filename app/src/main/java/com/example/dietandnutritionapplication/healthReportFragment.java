package com.example.dietandnutritionapplication;

import java.util.TimeZone;
import com.google.firebase.Timestamp;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import android.app.AlertDialog;

public class healthReportFragment extends Fragment {

    private Button buttonDaily, buttonMonthly;
    private TextView selectedDateTextView, adviceTextView, averageCalories, averageProteins, averageFats;
    private PieChart pieChart;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.health_report, container, false);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize views
        buttonDaily = view.findViewById(R.id.button_daily);
        buttonMonthly = view.findViewById(R.id.button_monthly);
        selectedDateTextView = view.findViewById(R.id.selected_date);
        adviceTextView = view.findViewById(R.id.health_advice_text);
        averageCalories = view.findViewById(R.id.averageCalories);
        averageProteins = view.findViewById(R.id.averageProtein);
        averageFats = view.findViewById(R.id.averageFats);
        pieChart = view.findViewById(R.id.pie_chart);

        // Set button listeners
        buttonDaily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserAuthenticated()) {
                    showDatePickerDialog();
                } else {
                    Toast.makeText(getContext(), "Please log in to access your health report.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonMonthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserAuthenticated()) {
                    showMonthPickerDialog();
                } else {
                    Toast.makeText(getContext(), "Please log in to access your health report.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    // Check if the user is authenticated
    private boolean isUserAuthenticated() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null;
    }

    // Method to show DatePickerDialog for daily selection
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                selectedDateTextView.setText(selectedDate);
                fetchNutritionalData(selectedDate); // Fetch data for the selected date
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    // Method to display the selected month and year using an AlertDialog
    private void showMonthPickerDialog() {
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};

        // Fetch current year
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select a Month");

        builder.setItems(months, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedMonth = months[which];
                String selectedMonthYear = selectedMonth + " " + year;
                selectedDateTextView.setText(selectedMonthYear);
                fetchMonthlyData(selectedMonth, year);  // Fetch data for the selected month and year
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void fetchNutritionalData(String selectedDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
            inputFormat.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
            Date date = inputFormat.parse(selectedDate);

            // Set the start timestamp for the day (00:00:00 UTC)
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(date);
            startCalendar.set(Calendar.HOUR_OF_DAY, 0);
            startCalendar.set(Calendar.MINUTE, 0);
            startCalendar.set(Calendar.SECOND, 0);
            startCalendar.set(Calendar.MILLISECOND, 0);
            Timestamp startTimestamp = new Timestamp(startCalendar.getTime());

            // Set the end timestamp for the day (23:59:59.999 UTC)
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(date);
            endCalendar.set(Calendar.HOUR_OF_DAY, 23);
            endCalendar.set(Calendar.MINUTE, 59);
            endCalendar.set(Calendar.SECOND, 59);
            endCalendar.set(Calendar.MILLISECOND, 999);
            Timestamp endTimestamp = new Timestamp(endCalendar.getTime());

            // Get the current user and their username
            FirebaseUser user = auth.getCurrentUser();
            String username = user != null ? user.getDisplayName() : null; // Assuming username is stored in displayName

            // Fetch data for the day using a range, filtering by username
            db.collection("MealRecords")
                    .whereGreaterThanOrEqualTo("createdDate", startTimestamp)
                    .whereLessThan("createdDate", endTimestamp)
                    //.whereEqualTo("username", username) // Filter by username
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            double totalCalories = 0;  // Added variable for total calories
                            double totalCarbs = 0;
                            double totalProteins = 0;
                            double totalFats = 0;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                HashMap<String, Object> data = (HashMap<String, Object>) document.getData();
                                if (data != null) { // Check if data is not null
                                    totalCalories += (double) data.get("calories");  // Fetch calories from database
                                    totalCarbs += (double) data.get("carbs");
                                    totalProteins += (double) data.get("proteins");
                                    totalFats += (double) data.get("fats");
                                }
                            }

                            // Check if there are records found
                            if (task.getResult().size() > 0) {
                                // Display totals
                                averageCalories.setText(String.format("%.2f", totalCalories));  // Display calories
                                averageProteins.setText(String.format("%.2f", totalProteins));
                                averageFats.setText(String.format("%.2f", totalFats));

                                // Update Pie Chart with total values
                                updatePieChart(totalCarbs, totalProteins, totalFats);  // Update PieChart with carbs, proteins, and fats

                                // Generate nutritional advice based on the totals
                                generateNutritionalAdvice((float) totalProteins, (float) totalCarbs, (float) totalFats);
                            } else {
                                // Reset display when no data found
                                resetDisplay();
                            }
                        } else {
                            Toast.makeText(getContext(), "Error fetching nutritional data.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error parsing date.", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchMonthlyData(String month, int year) {
        // Convert month name to number
        int monthNumber = getMonthNumber(month);
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(year, monthNumber - 1, 1, 0, 0, 0); // Set to the first day of the month
        Timestamp startTimestamp = new Timestamp(startCalendar.getTime());

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(year, monthNumber, 1, 0, 0, 0); // Set to the first day of the next month
        Timestamp endTimestamp = new Timestamp(endCalendar.getTime());

        FirebaseUser user = auth.getCurrentUser();
        String username = user != null ? user.getDisplayName() : null; // Assuming username is stored in displayName

        db.collection("MealRecords")
                .whereGreaterThanOrEqualTo("createdDate", startTimestamp)
                .whereLessThan("createdDate", endTimestamp)
                //.whereEqualTo("username", username) // Filter by username
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        double totalCalories = 0; // Added variable for total calories
                        double totalCarbs = 0;
                        double totalProteins = 0;
                        double totalFats = 0;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            HashMap<String, Object> data = (HashMap<String, Object>) document.getData();
                            if (data != null) { // Check if data is not null
                                totalCalories += (double) data.get("calories"); // Fetch calories from database
                                totalCarbs += (double) data.get("carbs");
                                totalProteins += (double) data.get("proteins");
                                totalFats += (double) data.get("fats");
                            }
                        }

                        // Check if there are records found
                        if (task.getResult().size() > 0) {
                            // Display totals
                            averageCalories.setText(String.format("%.2f", totalCalories)); // Display calories
                            averageProteins.setText(String.format("%.2f", totalProteins));
                            averageFats.setText(String.format("%.2f", totalFats));

                            // Update Pie Chart with total values
                            updatePieChart(totalCarbs, totalProteins, totalFats); // Update PieChart with carbs, proteins, and fats

                            // Generate nutritional advice based on the totals
                            generateNutritionalAdvice((float) totalProteins, (float) totalCarbs, (float) totalFats);
                        } else {
                            // Reset display when no data found
                            resetDisplay();
                        }
                    } else {
                        Toast.makeText(getContext(), "Error fetching monthly data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private int getMonthNumber(String month) {
        switch (month) {
            case "January": return 1;
            case "February": return 2;
            case "March": return 3;
            case "April": return 4;
            case "May": return 5;
            case "June": return 6;
            case "July": return 7;
            case "August": return 8;
            case "September": return 9;
            case "October": return 10;
            case "November": return 11;
            case "December": return 12;
            default: return -1; // Invalid month
        }
    }

    // Method to update the PieChart
    private void updatePieChart(double totalCarbs, double totalProteins, double totalFats) {
        // Calculate total grams of macronutrients
        double totalMacronutrients = totalCarbs + totalProteins + totalFats;

        // Prepare pie chart entries based on grams
        ArrayList<PieEntry> entries = new ArrayList<>();
        if (totalMacronutrients > 0) { // Ensure no division by zero
            entries.add(new PieEntry((float) (totalCarbs / totalMacronutrients * 100), "Carbs"));
            entries.add(new PieEntry((float) (totalProteins / totalMacronutrients * 100), "Proteins"));
            entries.add(new PieEntry((float) (totalFats / totalMacronutrients * 100), "Fats"));
        } else {
            // Handle the case where there are no macronutrients to avoid empty pie chart
            entries.add(new PieEntry(0, "No Data"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Nutritional Breakdown");
        dataSet.setColors(new int[]{
                Color.parseColor("#A8DAB5"), // Soft Green
                Color.parseColor("#A2C2E6"), // Soft Blue
                Color.parseColor("#F4A3A3")  // Soft Red
        });
        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.invalidate(); // Refresh the chart
    }



    // Method to generate nutritional advice based on totals
    private void generateNutritionalAdvice(float proteins, float carbs, float fats) {
        StringBuilder advice = new StringBuilder();

        // Advising on protein intake
        if (proteins < 50) {
            advice.append("Increase protein intake for muscle growth. Include lean meats, fish, eggs, dairy, legumes, and nuts. ");
        } else if (proteins <= 100) {
            advice.append("Your protein intake is adequate. Keep including protein sources for overall health. ");
        } else {
            advice.append("Your protein intake is high. Consider moderating your protein sources to avoid health issues. ");
        }

        // Advising on carbohydrate intake
        if (carbs < 130) {
            advice.append("Add more carbohydrates for energy, focusing on whole grains, fruits, and vegetables. ");
        } else if (carbs <= 300) {
            advice.append("Your carbohydrate intake is healthy. Choose complex carbs for sustained energy. ");
        } else {
            advice.append("Your carbohydrate intake may be too high. Limit processed sugars and refined carbs. ");
        }

        // Advising on fat intake
        if (fats > 70) {
            advice.append("Reduce fat intake for heart health. Choose healthy fats like avocados and nuts while limiting saturated and trans fats. ");
        } else if (fats <= 70) {
            advice.append("Your fat intake is balanced. Include healthy fats in moderation for brain function. ");
        } else {
            advice.append("Your fat intake is low. Incorporate healthy fats into your diet for overall health. ");
        }

        adviceTextView.setText(advice.toString());
    }


    private void resetDisplay() {
        averageCalories.setText("N/A");
        averageProteins.setText("N/A");
        averageFats.setText("N/A");
        pieChart.clear();
        adviceTextView.setText("No data available for this date.");
    }
}
