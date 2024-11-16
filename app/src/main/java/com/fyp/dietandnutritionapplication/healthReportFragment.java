package com.fyp.dietandnutritionapplication;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.Timestamp;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import android.app.AlertDialog;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.Legend;

public class healthReportFragment extends Fragment {  // Class name should be capitalized

    private Button buttonDaily, buttonMonthly;
    private TextView selectedDateTextView, adviceTextView, averageCalories, averageProteins, averageFats, averageCarbs , averageFiber;
    private PieChart pieChart;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String userId;
    private String username;
    private NotificationUController notificationUController;
    private TextView notificationBadgeTextView;
    private TextView calorieLimitTextView;
    private BarChart barChart;
    private static final int DAYS_TO_SHOW = 7;
    private Calendar currentStartDate;

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
            averageCarbs = view.findViewById(R.id.averageCarbs);
            averageFiber = view.findViewById(R.id.averageFiber);
            pieChart = view.findViewById(R.id.pie_chart);
            calorieLimitTextView = view.findViewById(R.id.calorieLimit);
            barChart = view.findViewById(R.id.bar_chart);

            currentStartDate = Calendar.getInstance();
            currentStartDate.setTimeZone(TimeZone.getTimeZone("GMT+8"));

            notificationBadgeTextView = view.findViewById(R.id.notificationBadgeTextView);
            notificationUController = new NotificationUController();
            notificationUController.fetchNotifications(userId, new Notification.OnNotificationsFetchedListener() {
                @Override
                public void onNotificationsFetched(List<Notification> notifications) {
                    notificationUController.countNotifications(userId, new Notification.OnNotificationCountFetchedListener() {
                        @Override
                        public void onCountFetched(int count) {
                            notificationBadgeTextView.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
                            notificationBadgeTextView.setText(count > 0 ? String.valueOf(count) : "");
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

            // Fetch username from Firestore to get health advice
            db.collection("Users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String username = documentSnapshot.getString("username"); // Ensure "username" field exists
                    if (username != null && !username.isEmpty()) {
                        Log.d("FetchHealthAdvice", "Username: " + username);
                        fetchHealthAdviceByUsername(username);
                    } else {
                        Toast.makeText(getContext(), "Username is missing for this user.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "User profile not found.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Log.e("FetchHealthAdvice", "Error fetching username", e);
                Toast.makeText(getContext(), "Failed to retrieve username.", Toast.LENGTH_SHORT).show();
            });

            // Set button listeners
            buttonDaily.setOnClickListener(v -> {
                if (isUserAuthenticated()) {
                    showDatePickerDialog();
                    fetchWeeklyCalorieData();
                } else {
                    Toast.makeText(getContext(), "Please log in to access your health report.", Toast.LENGTH_SHORT).show();
                }
            });

            buttonMonthly.setOnClickListener(v -> {
                if (isUserAuthenticated()) {
                    showMonthPickerDialog();
                } else {
                    Toast.makeText(getContext(), "Please log in to access your health report.", Toast.LENGTH_SHORT).show();
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
        return auth.getCurrentUser() != null;
    }


    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
            String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
            selectedDateTextView.setText(selectedDate);

            // Fetch nutritional data for the selected date
            fetchNutritionalData(selectedDate);

            // Update current start date and fetch weekly calorie data
            currentStartDate.set(selectedYear, selectedMonth, selectedDay);
            fetchWeeklyCalorieData(); // Update data based on selected date
        }, year, month, day);

        datePickerDialog.show();
    }


    private void showMonthPickerDialog() {
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};

        // Fetch current year
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select a Month");

        builder.setItems(months, (dialog, which) -> {
            String selectedMonth = months[which];
            int selectedMonthIndex = which + 1; // Months are 1-indexed
            String selectedMonthYear = selectedMonth + " " + year;
            selectedDateTextView.setText(selectedMonthYear);

            // Fetch data for the selected month and year
            fetchMonthlyData(selectedMonth, year);
            fetchMonthlyCalorieData(selectedMonthIndex);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

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
                            double totalFiber = 0;
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
                                            // Increment totals based on nutritional values
                                            totalCalories += (Double) data.get("calories");
                                            totalCarbs += (Double) data.get("carbs");
                                            totalProteins += (Double) data.get("proteins");
                                            totalFats += (Double) data.get("fats");
                                            totalFiber += (Double) data.get("fiber");
                                            hasRecordsForSelectedDate = true; // Records found for this date
                                        }
                                    }
                                }
                            }

                            // Check if any records were found for the selected date
                            if (hasRecordsForSelectedDate) {
                                // Calculate averages or total values as needed
                                averageCalories.setText(String.format("%.2f", totalCalories)); // Display total calories
                                averageProteins.setText(String.format("%.2f", totalProteins)); // Display total proteins
                                averageFats.setText(String.format("%.2f", totalFats)); // Display total fats
                                averageCarbs.setText(String.format("%.2f", totalCarbs));// Display total carbs
                                averageFiber.setText(String.format("%.2f", totalFiber));// Display total carbs

                                // Prepare data for Pie Chart
                                List<PieEntry> pieEntries = new ArrayList<>();
                                //pieEntries.add(new PieEntry((float) totalCalories, "Calories"));
                                pieEntries.add(new PieEntry((float) totalCarbs, "Carbs"));
                                pieEntries.add(new PieEntry((float) totalProteins, "Proteins"));
                                pieEntries.add(new PieEntry((float) totalFats, "Fats"));
                                pieEntries.add(new PieEntry((float) totalFats, "Fiber"));

                                // Define soft pastel colors (RGBA values for softer colors)
                                List<Integer> softColors = new ArrayList<>();
                                //softColors.add(Color.rgb(255, 182, 193));  // Light Pink for Calories
                                softColors.add(Color.rgb(176, 224, 230));  // Powder Blue for Carbs
                                softColors.add(Color.rgb(152, 251, 152));  // Pale Green for Proteins
                                softColors.add(Color.rgb(255, 228, 181));  // Light Goldenrod for Fats
                                softColors.add(Color.rgb(255, 182, 193));  // Papaya Whip for Fiber

                                PieDataSet pieDataSet = new PieDataSet(pieEntries, "Nutritional Values");
                                // Set custom colors to the dataset
                                pieDataSet.setColors(softColors);
                                PieData pieData = new PieData(pieDataSet);
                                pieChart.setData(pieData);
                                pieChart.invalidate(); // Refresh the chart

                                // Fetch the calorie limit
                                fetchCalorieLimit();

                                fetchHealthAdviceByUsername(username);

                            } else {
                                // Clear chart and text when no records found
                                resetDisplay();
                                pieChart.invalidate();
                                fetchHealthAdviceByUsername(username);

                                Toast.makeText(getContext(), "No records found for the selected date.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("NutritionalData", "Error getting documents: ", task.getException());
                            Toast.makeText(getContext(), "Error retrieving data. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            Log.e("NutritionalData", "Error parsing date: " + e.getMessage(), e);
        }
    }


    private void fetchMonthlyData(String month, int year) {
        // Convert month name to number
        int monthNumber = getMonthNumber(month);
        Log.d("FetchMonthlyData", "Month Number: " + monthNumber);

        // Ensure the month number is valid
        if (monthNumber < 1 || monthNumber > 12) {
            Toast.makeText(getContext(), "Invalid month selected.", Toast.LENGTH_SHORT).show();
            return; // Early exit if month is invalid
        }

        db.collection("MealRecords")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        double totalCalories = 0;
                        double totalCarbs = 0;
                        double totalProteins = 0;
                        double totalFats = 0;
                        double totalFiber = 0;
                        double calorieLimit =0;
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

                                    Log.d("FetchMonthlyData", "Processing Document: Month " + documentMonth + ", Year " + documentYear);

                                    // Check if the month and year match the selected values
                                    if (documentMonth == monthNumber && documentYear == year) {
                                        // Safely handle potential null values
                                        totalCalories += (data.get("calories") != null) ? (Double) data.get("calories") : 0.0;
                                        totalCarbs += (data.get("carbs") != null) ? (Double) data.get("carbs") : 0.0;
                                        totalProteins += (data.get("proteins") != null) ? (Double) data.get("proteins") : 0.0;
                                        totalFats += (data.get("fats") != null) ? (Double) data.get("fats") : 0.0;
                                        totalFiber += (data.get("fiber") != null) ? (Double) data.get("fiber") : 0.0;
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
                            averageFiber.setText(String.format("%.2f", totalFiber));// Display total carbs

                            // Update Pie Chart with total values
                            updatePieChart(totalCalories,totalCarbs, totalProteins, totalFats, totalFiber);  // Update to include fiber

                            // Fetch the calorie limit
                            calculateMonthlyCalorieLimit();

                            fetchHealthAdviceByUsername(username);
                            } else {
                            // Display message for no records found
                            resetDisplay();
                            pieChart.invalidate();

                            fetchHealthAdviceByUsername(username);

                            Toast.makeText(getContext(), "No records found for " + month + " " + year, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("FirestoreError", "Error fetching monthly data", task.getException());
                        Toast.makeText(getContext(), "Error fetching monthly data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchCalorieLimit() {
        db.collection("Users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Get the calorie limit and set it to the TextView
                            Long calorieLimit = document.getLong("calorieLimit");
                            calorieLimitTextView.setText(String.valueOf(calorieLimit));
                        } else {
                            calorieLimitTextView.setText(" ");
                        }
                    } else {
                        calorieLimitTextView.setText("Failed to fetch data");
                    }
                });
    }

    // Calculate monthly calorie limit based on daily limit
    private void calculateMonthlyCalorieLimit() {
        fetchCalorieLimit(); // First, fetch the daily limit

        // Use an OnSuccessListener to ensure we calculate after fetching
        db.collection("Users").document(userId).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        Long dailyCalorieLimit = document.getLong("calorieLimit");


                        if (dailyCalorieLimit != null) {
                            int daysInMonth = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
                            long monthlyCalorieLimit = dailyCalorieLimit * daysInMonth;
                            calorieLimitTextView.setText(String.valueOf(monthlyCalorieLimit));
                        }
                    }
                })
                .addOnFailureListener(e -> calorieLimitTextView.setText("Failed to calculate monthly limit"));
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

    public void updatePieChart(double totalCalories, double totalFats, double totalCarbs, double totalProteins, double totalFiber) {
        List<PieEntry> pieEntries = new ArrayList<>();

        // Add entries for calories, carbs, proteins, fats, and fiber
        //pieEntries.add(new PieEntry((float) totalCalories, "Calories"));
        pieEntries.add(new PieEntry((float) totalCarbs, "Carbs"));
        pieEntries.add(new PieEntry((float) totalProteins, "Proteins"));
        pieEntries.add(new PieEntry((float) totalFats, "Fats"));
        pieEntries.add(new PieEntry((float) totalFiber, "Fiber"));  // Add fiber entry

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Nutrients");

        // Define soft pastel colors (RGBA values for softer colors)
        List<Integer> softColors = new ArrayList<>();
        //softColors.add(Color.rgb(255, 182, 193));  // Light Pink for Calories
        softColors.add(Color.rgb(176, 224, 230));  // Powder Blue for Carbs
        softColors.add(Color.rgb(152, 251, 152));  // Pale Green for Proteins
        softColors.add(Color.rgb(255, 228, 181));  // Light Goldenrod for Fats
        softColors.add(Color.rgb(255, 182, 193));  // Papaya Whip for Fiber

        // Set custom colors to the dataset
        pieDataSet.setColors(softColors);

        // Customize other properties of the pie chart (optional)
        pieDataSet.setValueTextColor(Color.DKGRAY);  // Dark Gray for text
        pieDataSet.setValueTextSize(12f);  // Text size

        // Set data and refresh the chart
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();  // Refresh the chart to display updated data
    }




    private void fetchHealthAdviceByUsername(String username) {
        // Log the username to debug
        Log.d("FetchHealthAdvice", "Username passed: " + username);

        if (username == null || username.isEmpty()) {
            Toast.makeText(getContext(), " ", Toast.LENGTH_SHORT).show();
            return;
        }


        // Reference the Firestore document for the unique user based on username
        DocumentReference documentRef = db.collection("healthAdvice").document(username);

        // Fetch the document data
        documentRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Map Firestore document to HealthAdvice class
                        UserAccountDetailsToRecommendFragment.HealthAdvice healthAdvice = documentSnapshot.toObject(UserAccountDetailsToRecommendFragment.HealthAdvice.class);
                        if (healthAdvice != null && healthAdvice.getComment() != null) {
                            // Display the health advice comment if view is available
                            View rootView = getView();
                            if (rootView != null) {
                                TextView adviceTextView = rootView.findViewById(R.id.health_advice_text);
                                adviceTextView.setText(healthAdvice.getComment());
                            } else {
                                Log.e("UserAccountFragment", "Root view is null. Cannot find TextView.");
                            }
                        } else {
                            Toast.makeText(getContext(), "No health advice found for this user.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "No health advice exists for this user.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("UserAccountFragment", "Error fetching health advice", e);
                    Toast.makeText(getContext(), "Error fetching health advice.", Toast.LENGTH_SHORT).show();
                });
    }



    private void resetDisplay() {
        averageCalories.setText(" ");
        averageProteins.setText(" ");
        averageCarbs.setText(" ");
        averageFats.setText(" ");
        averageFiber.setText(" ");
        calorieLimitTextView.setText(" ");
        pieChart.clear();
        adviceTextView.setText("No data available for this date.");
    }

    private void fetchWeeklyCalorieData() {
        // Calculate end date and start date based on current start date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        Date endDate = currentStartDate.getTime();
        Calendar startDateCalendar = (Calendar) currentStartDate.clone();
        startDateCalendar.add(Calendar.DAY_OF_MONTH, -DAYS_TO_SHOW);
        Date startDate = startDateCalendar.getTime();

        db.collection("MealRecords")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Float> breakfastData = new HashMap<>();
                        Map<String, Float> lunchData = new HashMap<>();
                        Map<String, Float> dinnerData = new HashMap<>();
                        Map<String, Float> snacksData = new HashMap<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Timestamp timestamp = document.getTimestamp("createdDate");
                            String mealType = document.getString("mealType");
                            Double calories = document.getDouble("calories");

                            if (timestamp != null && timestamp.toDate().after(startDate) &&
                                    timestamp.toDate().compareTo(endDate) <= 0 && // Change this line
                                    mealType != null && calories != null) {

                                Calendar mealDate = Calendar.getInstance();
                                mealDate.setTime(timestamp.toDate());
                                // Change date format here
                                String dateKey = new SimpleDateFormat("d MMM", Locale.getDefault()).format(mealDate.getTime());

                                Map<String, Float> targetMap;
                                switch (mealType.toLowerCase()) {
                                    case "breakfast":
                                        targetMap = breakfastData;
                                        break;
                                    case "lunch":
                                        targetMap = lunchData;
                                        break;
                                    case "dinner":
                                        targetMap = dinnerData;
                                        break;
                                    default:
                                        targetMap = snacksData;
                                        break;
                                }
                                targetMap.merge(dateKey, calories.floatValue(), Float::sum);
                            }
                        }
                        updateCalorieChart(breakfastData, lunchData, dinnerData, snacksData);
                    } else {
                        Log.e("FetchCalorieData", "Error fetching data: ", task.getException());
                    }
                });
    }

    private void updateCalorieChart(Map<String, Float> breakfastData,
                                    Map<String, Float> lunchData,
                                    Map<String, Float> dinnerData,
                                    Map<String, Float> snacksData) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        Calendar calendar = (Calendar) currentStartDate.clone();
        calendar.add(Calendar.DAY_OF_MONTH, -6);  // Start from 5 days before the selected date
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM", Locale.getDefault());


        float maxCalories = 0f;  // Variable to hold the maximum calorie intake

        for (int i = 0; i < 7; i++) {  // Loop for 7 days (5 days prior + selected day + 1 day after)
            String dateKey = sdf.format(calendar.getTime());
            labels.add(dateKey);

            float breakfast = breakfastData.getOrDefault(dateKey, 0f);
            float lunch = lunchData.getOrDefault(dateKey, 0f);
            float dinner = dinnerData.getOrDefault(dateKey, 0f);
            float snacks = snacksData.getOrDefault(dateKey, 0f);

            float totalCalories = breakfast + lunch + dinner + snacks;  // Calculate total calories for the day
            entries.add(new BarEntry(i, new float[]{breakfast, lunch, dinner, snacks}));

            // Update maxCalories if totalCalories is greater
            if (totalCalories > maxCalories) {
                maxCalories = totalCalories;
            }

            calendar.add(Calendar.DAY_OF_MONTH, 1);  // Move to the next day
        }

        BarDataSet dataSet = new BarDataSet(entries, "Daily Calories");
        dataSet.setColors(
                Color.rgb(176, 224, 230),
                Color.rgb(152, 251, 152),
                Color.rgb(255, 228, 181),
                Color.rgb(255, 182, 193)
        );
        dataSet.setStackLabels(new String[]{"Breakfast", "Lunch", "Dinner", "Snacks"});

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setAxisMinimum(0f);

        barChart.getDescription().setEnabled(false);
        Legend legend = barChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBorders(false);
        barChart.setBackgroundColor(Color.WHITE);

        // Fetch calorie goal and then add the limit line
        float finalMaxCalories = maxCalories;
        db.collection("Users").document(userId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists() && document.contains("calorieLimit")) {
                        Long calorieGoal = document.getLong("calorieLimit");
                        Log.d("CalorieGoal", "Calorie goal: " + calorieGoal);  // Check if calorie goal is fetched

                        if (calorieGoal != null) {
                            // Create limit line
                            com.github.mikephil.charting.components.LimitLine goalLine =
                                    new com.github.mikephil.charting.components.LimitLine(calorieGoal, "Daily Goal");
                            goalLine.setLineWidth(2f);  // Make it thicker for testing
                            goalLine.setLineColor(Color.RED);  // Change to red for visibility
                            goalLine.enableDashedLine(10f, 10f, 0f);
                            goalLine.setLabelPosition(com.github.mikephil.charting.components.LimitLine.LimitLabelPosition.RIGHT_TOP);

                            // Add to chart
                            barChart.getAxisLeft().removeAllLimitLines();
                            barChart.getAxisLeft().addLimitLine(goalLine);
                            Log.d("LimitLine", "Added limit line with calorie goal: " + calorieGoal);

                            // Set Y-axis max to be greater than the maximum calorie intake
                            barChart.getAxisLeft().setAxisMaximum(Math.max(finalMaxCalories, calorieGoal) + 100); // Adding buffer to max

                            // Invalidate the chart after adding the line
                            barChart.invalidate();
                        }
                    }
                });

        // Refresh the chart
        barChart.invalidate();
    }



    private void fetchMonthlyCalorieData(int month) {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.MONTH, month - 1);
        startCalendar.set(Calendar.DAY_OF_MONTH, 1);
        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        Date startDate = startCalendar.getTime();

        Calendar endCalendar = (Calendar) startCalendar.clone();
        endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        endCalendar.set(Calendar.HOUR_OF_DAY, 23);
        endCalendar.set(Calendar.MINUTE, 59);
        endCalendar.set(Calendar.SECOND, 59);
        Date endDate = endCalendar.getTime();

        db.collection("MealRecords")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Float> monthlyData = new HashMap<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Timestamp timestamp = document.getTimestamp("createdDate");
                            String mealType = document.getString("mealType");
                            Double calories = document.getDouble("calories");

                            if (timestamp != null && timestamp.toDate().after(startDate) &&
                                    timestamp.toDate().compareTo(endDate) <= 0 &&
                                    mealType != null && calories != null) {
                                monthlyData.merge(mealType, calories.floatValue(), Float::sum);
                            }
                        }
                        updateMonthlyCalorieChart(monthlyData);
                    } else {
                        Log.e("FetchMonthlyData", "Error fetching data: ", task.getException());
                    }
                });
    }

    private void updateMonthlyCalorieChart(Map<String, Float> monthlyData) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>(monthlyData.keySet());

        int index = 0;
        float maxCalories = 0; // Variable to track the max calories
        for (Map.Entry<String, Float> entry : monthlyData.entrySet()) {
            entries.add(new BarEntry(index++, entry.getValue()));
            // Update maxCalories if the current entry's value is greater
            if (entry.getValue() > maxCalories) {
                maxCalories = entry.getValue();
            }
        }

        BarDataSet dataSet = new BarDataSet(entries, "Monthly Calories");

        // Set custom colors
        int[] customColors = {
                Color.rgb(176, 224, 230), // Light Blue
                Color.rgb(152, 251, 152), // Light Green
                Color.rgb(255, 228, 181), // Light Peach
                Color.rgb(255, 182, 193)  // Light Pink
        };

        dataSet.setColors(customColors); // Set the custom colors

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setAxisMinimum(0f);

        // Set Y-axis maximum value, adding a buffer of 10% to the maximum calories
        barChart.getAxisLeft().setAxisMaximum(maxCalories * 1.1f);


        barChart.invalidate(); // Refresh the chart
    }

}

