package com.example.dietandnutritionapplication;

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
    private TextView selectedDateTextView, adviceTextView, averageCarbs, averageProteins, averageFats;
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
        averageCarbs = view.findViewById(R.id.averageCarbs);
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
            // Convert selectedDate from "dd/MM/yyyy" to Date
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = inputFormat.parse(selectedDate);
            Timestamp timestamp = new Timestamp(date);

            // Fetch data where createdDate equals the timestamp
            db.collection("MealRecords")
                    .whereEqualTo("createdDate", timestamp)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            double totalCarbs = 0;
                            double totalProteins = 0;
                            double totalFats = 0;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                HashMap<String, Object> data = (HashMap<String, Object>) document.getData();
                                totalCarbs += (double) data.get("carbs");
                                totalProteins += (double) data.get("proteins");
                                totalFats += (double) data.get("fats");
                            }

                            // Check if there are records found
                            if (task.getResult().size() > 0) {
                                // Display totals
                                averageCarbs.setText(String.format("%.2f", totalCarbs));
                                averageProteins.setText(String.format("%.2f", totalProteins));
                                averageFats.setText(String.format("%.2f", totalFats));

                                // Update Pie Chart with total values
                                updatePieChart(totalCarbs, totalProteins, totalFats);

                                // Generate nutritional advice based on the totals
                                generateNutritionalAdvice((float) totalProteins, (float) totalCarbs, (float) totalFats);
                            } else {
                                // Reset display when no data found
                                resetDisplay();
                            }
                        } else {
                            Toast.makeText(getContext(), "Error fetching data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error parsing date: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void fetchMonthlyData(String selectedMonth, int year) {
        try {
            // Set the start and end dates for the month
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.set(year, getMonthNumber(selectedMonth) - 1, 1, 0, 0, 0);
            Timestamp startTimestamp = new Timestamp(startCalendar.getTime());

            Calendar endCalendar = Calendar.getInstance();
            endCalendar.set(year, getMonthNumber(selectedMonth) - 1, endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
            Timestamp endTimestamp = new Timestamp(endCalendar.getTime());

            db.collection("MealRecords")
                    .whereGreaterThanOrEqualTo("createdDate", startTimestamp)
                    .whereLessThanOrEqualTo("createdDate", endTimestamp)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            double totalCarbs = 0;
                            double totalProteins = 0;
                            double totalFats = 0;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                HashMap<String, Object> data = (HashMap<String, Object>) document.getData();
                                totalCarbs += (double) data.get("carbs");
                                totalProteins += (double) data.get("proteins");
                                totalFats += (double) data.get("fats");
                            }

                            // Check if there are records found
                            if (task.getResult().size() > 0) {
                                // Display totals
                                averageCarbs.setText(String.format("%.2f", totalCarbs));
                                averageProteins.setText(String.format("%.2f", totalProteins));
                                averageFats.setText(String.format("%.2f", totalFats));

                                // Update Pie Chart with total values
                                updatePieChart(totalCarbs, totalProteins, totalFats);

                                // Generate nutritional advice based on the totals
                                generateNutritionalAdvice((float) totalProteins, (float) totalCarbs, (float) totalFats);
                            } else {
                                // Reset display when no data found
                                resetDisplay();
                            }
                        } else {
                            Toast.makeText(getContext(), "Error fetching data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error parsing month: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Method to reset the display when no data is found
    private void resetDisplay() {
        // Reset text fields
        averageCarbs.setText("0.00");
        averageProteins.setText("0.00");
        averageFats.setText("0.00");

        // Clear the pie chart
        pieChart.clear();
        pieChart.invalidate();

        // Clear nutritional advice
        adviceTextView.setText("");
    }



    // Helper method to get month number from month name
    private int getMonthNumber(String month) {
        switch (month) {
            case "January":
                return 1;
            case "February":
                return 2;
            case "March":
                return 3;
            case "April":
                return 4;
            case "May":
                return 5;
            case "June":
                return 6;
            case "July":
                return 7;
            case "August":
                return 8;
            case "September":
                return 9;
            case "October":
                return 10;
            case "November":
                return 11;
            case "December":
                return 12;
            default:
                return 0; // Invalid month
        }
    }

    // Method to update the pie chart
    private void updatePieChart(double carbs, double proteins, double fats) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) carbs, "Carbohydrates"));
        entries.add(new PieEntry((float) proteins, "Proteins"));
        entries.add(new PieEntry((float) fats, "Fats"));

        PieDataSet dataSet = new PieDataSet(entries, "Nutritional Composition");
        dataSet.setColors(new int[]{
                Color.parseColor("#A8DAB5"), // Soft Green
                Color.parseColor("#A2C2E6"), // Soft Blue
                Color.parseColor("#F4A3A3")  // Soft Red
        });

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.invalidate(); // refresh the chart
    }

    // Method to generate nutritional advice based on the averages of carbs, proteins, and fats
    private void generateNutritionalAdvice(float totalProteins, float totalCarbs, float totalFats) {
        String advice = "";
        if (totalCarbs < 200) {
            advice += "Increase your carb intake for balanced energy. ";
        } else {
            advice += "Your carb intake is good. ";
        }

        if (totalProteins < 50) {
            advice += "Consider adding more protein to your diet. ";
        } else {
            advice += "Your protein intake is sufficient. ";
        }

        if (totalFats > 70) {
            advice += "Try to reduce your fat intake for better health. ";
        } else {
            advice += "Your fat intake is within a healthy range. ";
        }

        adviceTextView.setText(advice);
    }
}

