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
        averageCarbs = view.findViewById(R.id.averageCarbs); // Changed to averageCarbs
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

        adviceTextView = view.findViewById(R.id.health_advice_text); // Initialize TextView for advice

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

    // Method to display the selected month using an AlertDialog
    private void showMonthPickerDialog() {
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select a Month");

        builder.setItems(months, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedMonth = months[which];
                selectedDateTextView.setText(selectedMonth);
                // Fetch current year
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                fetchMonthlyData(selectedMonth, year); // Fetch data for the selected month
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

    // Method to fetch nutritional data for the selected date
    private void fetchNutritionalData(String selectedDate) {
        try {
            // Convert selectedDate from "dd/MM/yyyy" to Date
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = inputFormat.parse(selectedDate);
            Timestamp timestamp = new Timestamp(date);

            // Fetch data where createdDate equals the timestamp
            db.collection("MealRecords")
                    .whereEqualTo("createdDate", timestamp) // Using Timestamp
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            double totalCarbs = 0; // Changed to totalCarbs
                            double totalProteins = 0;
                            double totalFats = 0;
                            int count = 0;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                HashMap<String, Object> data = (HashMap<String, Object>) document.getData();
                                totalCarbs += (double) data.get("carbs"); // Changed to carbs
                                totalProteins += (double) data.get("proteins");
                                totalFats += (double) data.get("fats");
                                count++;
                            }

                            if (count > 0) {
                                double avgCarbs = totalCarbs / count; // Changed to avgCarbs
                                double avgProteins = totalProteins / count;
                                double avgFats = totalFats / count;

                                averageCarbs.setText(String.format("%.2f", avgCarbs)); // Changed to averageCarbs
                                averageProteins.setText(String.format("%.2f", avgProteins));
                                averageFats.setText(String.format("%.2f", avgFats));
                                updatePieChart(avgCarbs, avgProteins, avgFats); // Changed to avgCarbs

                                // Generate nutritional advice based on averages
                                generateNutritionalAdvice((float) avgProteins, (float) avgCarbs, (float) avgFats); // Changed to include avgCarbs
                            } else {
                                Toast.makeText(getContext(), "No records found for the selected date.", Toast.LENGTH_SHORT).show();
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

    // Method to fetch nutritional data for the selected month
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
                    .whereGreaterThanOrEqualTo("createdDate", startTimestamp) // Using Timestamp
                    .whereLessThanOrEqualTo("createdDate", endTimestamp) // Using Timestamp
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            double totalCarbs = 0; // Changed to totalCarbs
                            double totalProteins = 0;
                            double totalFats = 0;
                            int count = 0;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                HashMap<String, Object> data = (HashMap<String, Object>) document.getData();
                                totalCarbs += (double) data.get("carbs"); // Changed to carbs
                                totalProteins += (double) data.get("proteins");
                                totalFats += (double) data.get("fats");
                                count++;
                            }

                            if (count > 0) {
                                double avgCarbs = totalCarbs / count; // Changed to avgCarbs
                                double avgProteins = totalProteins / count;
                                double avgFats = totalFats / count;

                                averageCarbs.setText(String.format("%.2f", avgCarbs)); // Changed to averageCarbs
                                averageProteins.setText(String.format("%.2f", avgProteins));
                                averageFats.setText(String.format("%.2f", avgFats));
                                updatePieChart(avgCarbs, avgProteins, avgFats); // Changed to avgCarbs

                                // Generate nutritional advice based on averages
                                generateNutritionalAdvice((float) avgProteins, (float) avgCarbs, (float) avgFats); // Changed to include avgCarbs
                            } else {
                                Toast.makeText(getContext(), "No records found for the selected month.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Error fetching data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error fetching monthly data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
                return 0;
        }
    }

    // Method to update the PieChart
    private void updatePieChart(double avgCarbs, double avgProteins, double avgFats) {
        PieDataSet dataSet = new PieDataSet(getPieEntries(avgCarbs, avgProteins, avgFats), "Nutritional Breakdown");
        dataSet.setColors(new int[]{
                Color.parseColor("#A8DAB5"), // Soft Green
                Color.parseColor("#A2C2E6"), // Soft Blue
                Color.parseColor("#F4A3A3")  // Soft Red
        });

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    // Method to get PieEntries for PieChart
    private ArrayList<PieEntry> getPieEntries(double avgCarbs, double avgProteins, double avgFats) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) avgCarbs, "Carbs")); // Changed to avgCarbs
        entries.add(new PieEntry((float) avgProteins, "Proteins"));
        entries.add(new PieEntry((float) avgFats, "Fats"));
        return entries;
    }

    // Method to generate nutritional advice based on averages
    private void generateNutritionalAdvice(float avgProteins, float avgCarbs, float avgFats) {
        String advice = "";
        if (avgCarbs < 200) {
            advice += "Increase your carb intake for balanced energy. ";
        } else {
            advice += "Your carb intake is good. ";
        }

        if (avgProteins < 50) {
            advice += "Consider adding more protein to your diet. ";
        } else {
            advice += "Your protein intake is sufficient. ";
        }

        if (avgFats > 70) {
            advice += "Try to reduce your fat intake for better health. ";
        } else {
            advice += "Your fat intake is within a healthy range. ";
        }

        adviceTextView.setText(advice);
    }
}

