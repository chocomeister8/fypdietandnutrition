package com.example.dietandnutritionapplication;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;

public class AdminHomeFragment extends Fragment {
    private BarChart barChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.adminhomepage, container, false);

        // Initialize BarChart by finding it in the view
        barChart = view.findViewById(R.id.accountBarchart);  // Ensure this matches the ID in your XML

        // Check if barChart is not null before using it
        if (barChart != null) {
            ArrayList<BarEntry> userEntries = new ArrayList<>();
            userEntries.add(new BarEntry(1f, 3));  // Registered Users

            ArrayList<BarEntry> nutritionistEntries = new ArrayList<>();
            nutritionistEntries.add(new BarEntry(2f, 1));  // Nutritionists

            ArrayList<BarEntry> adminEntries = new ArrayList<>();
            adminEntries.add(new BarEntry(3f, 1));  // Admins

            // Create data sets for each category
            BarDataSet userDataSet = new BarDataSet(userEntries, "Users");
            userDataSet.setColor(Color.rgb(144, 238, 144));  // Set color for users

            BarDataSet nutritionistDataSet = new BarDataSet(nutritionistEntries, "Nutritionists");
            nutritionistDataSet.setColor(Color.rgb(224, 255, 255));  // Set color for nutritionists

            BarDataSet adminDataSet = new BarDataSet(adminEntries, "Admins");
            adminDataSet.setColor(Color.rgb(211, 211, 211));  // Set color for admins

            // Combine the data sets into one BarData object
            BarData barData = new BarData(userDataSet, nutritionistDataSet, adminDataSet);

            // Set the data to the bar chart
            barChart.setData(barData);

            // Customize the bar width to ensure proper spacing
            barData.setBarWidth(0.9f);  // Adjust bar width as needed

            // Refresh the chart
            barChart.invalidate();

            barChart.getDescription().setEnabled(false);

            // Remove grid lines
            barChart.getXAxis().setDrawGridLines(false);
            barChart.getAxisLeft().setDrawGridLines(false);
            barChart.getAxisRight().setDrawGridLines(false);

            XAxis xAxis = barChart.getXAxis();
            xAxis.setDrawLabels(false);  // Hide all X-axis labels
            barChart.getData().setDrawValues(false);

            YAxis leftAxis = barChart.getAxisLeft();
            leftAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.valueOf((int) value);  // Format Y-axis values as integers
                }
            });

            leftAxis.setGranularity(1f);  // Set the granularity to 1 to show one label per integer
            leftAxis.setGranularityEnabled(true);  // Enable granularity to ensure it is used

            YAxis rightAxis = barChart.getAxisRight();
            rightAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.valueOf((int) value);  // Format Y-axis values as integers
                }
            });
            rightAxis.setGranularity(1f);  // Set the granularity to 1 to show one label per integer
            rightAxis.setGranularityEnabled(true);  // Enable granularity to ensure it is used


            // Enable the legend and customize it
            Legend legend = barChart.getLegend();
            legend.setEnabled(true);  // Ensure the legend is enabled
            legend.setTextSize(14f);  // Adjust the text size of the legend
            legend.setForm(Legend.LegendForm.CIRCLE);  // Customize the legend shape (optional)

            barData.setValueTextSize(12f);  // Set text size for the values
            barData.setValueFormatter(new ValueFormatter() {
                @Override
                public String getBarLabel(BarEntry barEntry) {
                    return String.valueOf((int) barEntry.getY());  // Return integer values
                }
            });

            // Handle click events on bars
            barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, Highlight h) {
                    if (e == null) return;
                    Toast.makeText(getContext(), "Value: " + e.getY(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected() {
                    // Do nothing
                }
            });

            ImageView logoutImage = view.findViewById(R.id.right_icon);
            logoutImage.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();
                // Switch to guest mode (for nutritionists as guests)
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).switchToGuestMode();
                    ((MainActivity) getActivity()).replaceFragment(new LandingFragment());
                }
            });

            // Find the buttons in the layout
            Button viewAccountsButton = view.findViewById(R.id.viewAccountsButton);
            Button addAdminButton = view.findViewById(R.id.addAdminButton);
            Button viewFAQsButton = view.findViewById(R.id.viewFAQbutton);
            Button addFAQButton = view.findViewById(R.id.addFAQbutton);

            // Set an OnClickListener on the viewAccountsButton
            viewAccountsButton.setOnClickListener(v -> {
                // Replace the current fragment with AccountsFragment
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, new viewAccountsFragment()); // Ensure R.id.frame_layout is the container in your activity
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            });

            addAdminButton.setOnClickListener(v -> {
                // Replace the current fragment with AccountsFragment
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, new AddAdminFragment()); // Ensure R.id.frame_layout is the container in your activity
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            });

            viewFAQsButton.setOnClickListener(v -> {
                // Replace the current fragment with AccountsFragment
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, new FAQFragment()); // Ensure R.id.frame_layout is the container in your activity
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            });

            // Set an OnClickListener on the addFAQButton
            addFAQButton.setOnClickListener(v -> {
                // Replace the current fragment with FAQFragment
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, new AddFAQFragment()); // Ensure R.id.frame_layout is the container in your activity
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            });
        }
        return view;
    }
}