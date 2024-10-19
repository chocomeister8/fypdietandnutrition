package com.fyp.dietandnutritionapplication;

import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import com.google.firebase.Timestamp;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
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
    private String userId;
    private TextView averageCarbs; // Add this line
    private NotificationUController notificationUController;
    private TextView notificationBadgeTextView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.health_report, container, false);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            userId = user.getUid();

            // Initialize views
            buttonDaily = view.findViewById(R.id.button_daily);
            buttonMonthly = view.findViewById(R.id.button_monthly);
            selectedDateTextView = view.findViewById(R.id.selected_date);
            adviceTextView = view.findViewById(R.id.health_advice_text);
            averageCalories = view.findViewById(R.id.averageCalories);
            averageProteins = view.findViewById(R.id.averageProtein);
            averageFats = view.findViewById(R.id.averageFats);
            pieChart = view.findViewById(R.id.pie_chart);
            averageCarbs = view.findViewById(R.id.averageCarbs); // Add this line

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

            ImageView notiImage = view.findViewById(R.id.noti_icon);
            notiImage.setOnClickListener(v -> {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, new NotificationUFragment())
                        .addToBackStack(null)
                        .commit();

            });

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
            fetchTodaysReport();


        } else {
            // User is not logged in
            Toast.makeText(getContext(), "User is not logged in.", Toast.LENGTH_SHORT).show();
        }


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
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
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

    private void fetchTodaysReport() {
        // Get today's date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String todayDate = sdf.format(new Date()); // Get today's date as a string

        // Update the selected date TextView
        selectedDateTextView.setText(todayDate);

        // Fetch nutritional data for today
        fetchNutritionalData(todayDate); // Call your existing method to fetch data
    }

    private void fetchNutritionalData(String selectedDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            Date parsedSelectedDate = sdf.parse(selectedDate);

            // Convert the parsed Date to a Timestamp
            Timestamp selectedDateTimestamp = new Timestamp(parsedSelectedDate);

            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.setTime(parsedSelectedDate);

            Log.d("NutritionalData", "selectedDateTimestamp: " + selectedCalendar);

            // Get the current user and their username
            FirebaseUser user = auth.getCurrentUser();
            String userId = user != null ? user.getUid() : null;

            Log.d("NutritionalData", "User ID: " + userId);

            // Fetch data for the day using a range, filtering by username
            db.collection("MealRecords")
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            double totalCalories = 0;  // Added variable for total calories
                            double totalCarbs = 0;
                            double totalProteins = 0;
                            double totalFats = 0;
                            boolean hasRecordsForSelectedDate = false; // Flag to check for records

                            Log.d("NutritionalData", "Query successful, processing results...");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                HashMap<String, Object> data = (HashMap<String, Object>) document.getData();
                                if (data != null) { // Check if data is not null
                                    // Get createdDate from the document
                                    Timestamp createdDate = document.getTimestamp("createdDate");
                                    if (createdDate != null) {
                                        // Create a calendar instance from the createdDate
                                        Calendar createdCalendar = Calendar.getInstance();
                                        createdCalendar.setTime(createdDate.toDate());

                                        // Compare only the year, month, and day
                                        if (selectedCalendar.get(Calendar.YEAR) == createdCalendar.get(Calendar.YEAR) &&
                                                selectedCalendar.get(Calendar.MONTH) == createdCalendar.get(Calendar.MONTH) &&
                                                selectedCalendar.get(Calendar.DAY_OF_MONTH) == createdCalendar.get(Calendar.DAY_OF_MONTH)) {
                                            totalCalories += (double) data.get("calories");  // Fetch calories from database
                                            totalCarbs += (double) data.get("carbs");
                                            totalProteins += (double) data.get("proteins");
                                            totalFats += (double) data.get("fats");
                                            hasRecordsForSelectedDate = true; // Set flag to true if records are found

                                            Log.d("NutritionalData", "Total Calories: " + totalCalories);
                                            Log.d("NutritionalData", "Total Carbs: " + totalCarbs);
                                            Log.d("NutritionalData", "Total Proteins: " + totalProteins);
                                            Log.d("NutritionalData", "Total Fats: " + totalFats);
                                        }
                                    }
                                }
                            }

                            // Check if there are records found
                            if (hasRecordsForSelectedDate) {
                                // Display totals
                                averageCalories.setText(String.format("%.2f", totalCalories));
                                averageCarbs.setText(String.format("%.2f", totalCarbs));// Display calories
                                averageProteins.setText(String.format("%.2f", totalProteins));
                                averageFats.setText(String.format("%.2f", totalFats));

                                // Update Pie Chart with total values
                                updatePieChart(totalCarbs, totalProteins, totalFats);  // Update PieChart with carbs, proteins, and fats

                                // Generate nutritional advice based on the totals
                                generateNutritionalAdvice((float) totalProteins, (float) totalCarbs, (float) totalFats);
                            } else {
                                // Reset display and show no record message
                                resetDisplay();
                                adviceTextView.setText("No record for today.");
                            }
                        } else {
                            Log.e("FirestoreError", "Error fetching nutritional data", task.getException());
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
        Log.d("FetchMonthlyData", "Month Number: " + monthNumber);

        db.collection("MealRecords")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        double totalCalories = 0;
                        double totalCarbs = 0;
                        double totalProteins = 0;
                        double totalFats = 0;
                        boolean recordsFound = false; // Flag to check if any records are found

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            HashMap<String, Object> data = (HashMap<String, Object>) document.getData();
                            if (data != null) {
                                Timestamp createdDate = (Timestamp) data.get("createdDate");
                                if (createdDate != null) {
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(createdDate.toDate());
                                    int documentMonth = calendar.get(Calendar.MONTH) + 1; // Month is 0-based
                                    int documentYear = calendar.get(Calendar.YEAR);

                                    // Check if the month and year match the selected values
                                    if (documentMonth == monthNumber && documentYear == year) {
                                        totalCalories += (double) data.get("calories");
                                        totalCarbs += (double) data.get("carbs");
                                        totalProteins += (double) data.get("proteins");
                                        totalFats += (double) data.get("fats");
                                        recordsFound = true; // Set flag to true if a record is found
                                    }
                                }
                            }
                        }
                        Log.d("FetchMonthlyData", "Total Calories: " + totalCalories);
                        Log.d("FetchMonthlyData", "Total Carbs: " + totalCarbs);
                        Log.d("FetchMonthlyData", "Total Proteins: " + totalProteins);
                        Log.d("FetchMonthlyData", "Total Fats: " + totalFats);

                        // Check if any records were found
                        if (recordsFound) {
                            // Display totals
                            averageCalories.setText(String.format("%.2f", totalCalories));
                            averageCarbs.setText(String.format("%.2f", totalCarbs));
                            averageProteins.setText(String.format("%.2f", totalProteins));
                            averageFats.setText(String.format("%.2f", totalFats));

                            // Update Pie Chart with total values
                            updatePieChart(totalCarbs, totalProteins, totalFats);

                            // Generate nutritional advice based on the totals
                            generateNutritionalAdvice((float) totalProteins, (float) totalCarbs, (float) totalFats);
                        } else {
                            // Display message for no records found
                            resetDisplay();
                            Toast.makeText(getContext(), "No records found for " + month + " " + year, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("FirestoreError", "Error fetching monthly data", task.getException());
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
        averageCarbs.setText("N/A");
        averageFats.setText("N/A");
        pieChart.clear();
        adviceTextView.setText("No data available for this date.");
    }
}
