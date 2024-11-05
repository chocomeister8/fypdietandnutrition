package com.fyp.dietandnutritionapplication;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import com.fyp.dietandnutritionapplication.databinding.ActivityMainBinding;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeFragment extends Fragment {
    private BarChart barChart;
    ArrayList<Profile> profiles = new ArrayList<>();
    private ProfileAdapter adapter;
    private int numberOfUsers = 0;
    private int numberOfNutritionists = 0;
    private int numberOfAdmins = 0;
    private TextView faqCountTextView, specializationTextView, dietpreferenceTextView, allergyTextView;
    private EditText promoCodeInput, discountValuesInput; // Add references for promo code and discount
    private Button addPromoCodeButton;

    private ActivityMainBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.adminhomepage, container, false);
        faqCountTextView = view.findViewById(R.id.faqcount);
        specializationTextView = view.findViewById(R.id.specializationcount);
        dietpreferenceTextView = view.findViewById(R.id.dietpreferencecount);
        allergyTextView = view.findViewById(R.id.allergycount);
        barChart = view.findViewById(R.id.accountBarchart);
        ImageView logoutImage = view.findViewById(R.id.logout_button);

        // Find the buttons in the layout
        Button viewAccountsButton = view.findViewById(R.id.viewAccountsButton);
        Button addAdminButton = view.findViewById(R.id.addAdminButton);
        Button viewFAQsButton = view.findViewById(R.id.viewFAQbutton);
        Button addFAQButton = view.findViewById(R.id.addFAQbutton);
        Button viewPendingNutri = view.findViewById(R.id.viewPendingNutri);
        Button viewSpecialization = view.findViewById(R.id.viewSpecializationbutton);
        Button addSpecialization = view.findViewById(R.id.addSpecializationbutton);
        Button viewDietPreference = view.findViewById(R.id.viewDietPreferencebutton);
        Button addDietPreference = view.findViewById(R.id.addDietPreferencebutton);
        Button viewAllergy = view.findViewById(R.id.allergybutton);
        Button addAllergy = view.findViewById(R.id.addallergybutton);

        // Initialize promo code input and button
        promoCodeInput = view.findViewById(R.id.promoCodeInput);
        discountValuesInput = view.findViewById(R.id.discountvalues);
        addPromoCodeButton = view.findViewById(R.id.addPromoCodeButton);

        // Set OnClickListener for the add promo code button
        addPromoCodeButton.setOnClickListener(v -> addPromoCode());

        binding = ActivityMainBinding.inflate(getLayoutInflater());

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

        viewPendingNutri.setOnClickListener(v -> {
            // Replace the current fragment with AccountsFragment
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, new viewPendingNutritionistFragment()); // Ensure R.id.frame_layout is the container in your activity
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        viewSpecialization.setOnClickListener(v -> {
            // Replace the current fragment with AccountsFragment
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, new SpecializationFragment()); // Ensure R.id.frame_layout is the container in your activity
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        addSpecialization.setOnClickListener(v -> {
            // Replace the current fragment with AccountsFragment
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, new AddSpecializationFragment()); // Ensure R.id.frame_layout is the container in your activity
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

        viewDietPreference.setOnClickListener(v -> {
            // Replace the current fragment with AccountsFragment
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, new DietPreferenceFragment()); // Ensure R.id.frame_layout is the container in your activity
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        addDietPreference.setOnClickListener(v -> {
            // Replace the current fragment with AccountsFragment
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, new AddDietPreferenceFragment()); // Ensure R.id.frame_layout is the container in your activity
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        viewAllergy.setOnClickListener(v -> {
            // Replace the current fragment with AccountsFragment
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, new AllergyOptionsFragment()); // Ensure R.id.frame_layout is the container in your activity
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        addAllergy.setOnClickListener(v -> {
            // Replace the current fragment with AccountsFragment
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, new AddAllergyOptionsFragment()); // Ensure R.id.frame_layout is the container in your activity
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });


        adapter = new ProfileAdapter(getContext(), profiles);

        // Create an instance of UserAccountEntity and fetch accounts
        UserAccountEntity userAccountEntity = new UserAccountEntity();
        userAccountEntity.fetchAccounts(new UserAccountEntity.DataCallback() {
            @Override
            public void onSuccess(ArrayList<Profile> fetchedProfiles) {
                profiles.clear(); // Clear the current list
                profiles.addAll(fetchedProfiles); // Add new profiles

                // Count different types of profiles
                numberOfUsers = 0;
                numberOfNutritionists = 0;
                numberOfAdmins = 0;

                for (Profile profile : profiles) {
                    if (profile.getRole().equals("user")) {
                        numberOfUsers++;
                    } else if (profile.getRole().equals("nutritionist")) {
                        numberOfNutritionists++;
                    } else if (profile.getRole().equals("admin")) {
                        numberOfAdmins++;
                    }
                }

                // Update the chart with new data
                fetchAndUpdateProfileData();
                fetchFAQCount();
                fetchSpecializationCount();
                fetchDietPreferenceCount();
                fetchAllergyOptionCount();
                adapter.notifyDataSetChanged(); // Notify adapter of data changes
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to load accounts.", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up buttons and other UI elements
        setupUI(view);

        return view;
    }

    private void hideBottomNavigationView() {
        if (getActivity() instanceof MainActivity) {
            // Assuming there's a method in MainActivity to hide the bottom navigation
            ((MainActivity) getActivity()).hideBottomNavigationView();
        }
    }

    private void fetchAndUpdateProfileData() {
        // Check if barChart is not null before using it
        if (barChart != null) {
            ArrayList<BarEntry> userEntries = new ArrayList<>();
            userEntries.add(new BarEntry(0f, numberOfUsers));  // Registered Users

            ArrayList<BarEntry> nutritionistEntries = new ArrayList<>();
            nutritionistEntries.add(new BarEntry(1f, numberOfNutritionists));  // Nutritionists

            ArrayList<BarEntry> adminEntries = new ArrayList<>();
            adminEntries.add(new BarEntry(2f, numberOfAdmins));  // Admins

            BarDataSet barDataSet = new BarDataSet(userEntries, "Account Types");


            // Create data sets for each category
            BarDataSet userDataSet = new BarDataSet(userEntries, "Users");
            userDataSet.setColor(Color.rgb(144, 238, 144));  // Set color for users

            BarDataSet nutritionistDataSet = new BarDataSet(nutritionistEntries, "Nutritionists");
            nutritionistDataSet.setColor(Color.rgb(180, 205, 255));  // Set color for nutritionists

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
            legend.setFormSize(10f);  // Adjust the size of the legend shape
            legend.setXEntrySpace(30f);  // Adjust the space between legend entries horizontally
            legend.setYEntrySpace(5f);  // Adjust the space between legend entries vertically
            legend.setFormToTextSpace(5f);

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
        }
    }
    private void setupUI(View view) {
        ImageView logoutImage = view.findViewById(R.id.logout_button);
        logoutImage.setOnClickListener(v -> {
            // Create an AlertDialog to confirm logout
            new AlertDialog.Builder(getContext())
                    .setTitle("Confirm Logout")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Log out", (dialog, which) -> {
                        // User confirmed to log out
                        Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).replaceFragment(new LoginFragment());
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        // User cancelled the logout action
                        dialog.dismiss();
                    })
                    .show();
        });

        Button viewAccountsButton = view.findViewById(R.id.viewAccountsButton);
        Button addAdminButton = view.findViewById(R.id.addAdminButton);
        Button viewFAQsButton = view.findViewById(R.id.viewFAQbutton);
        Button addFAQButton = view.findViewById(R.id.addFAQbutton);

        viewAccountsButton.setOnClickListener(v -> replaceFragment(new viewAccountsFragment()));
        addAdminButton.setOnClickListener(v -> replaceFragment(new AddAdminFragment()));
        viewFAQsButton.setOnClickListener(v -> replaceFragment(new FAQFragment()));
        addFAQButton.setOnClickListener(v -> replaceFragment(new AddFAQFragment()));
    }

    private void fetchFAQCount() {
        FAQEntity faqEntity = new FAQEntity();
        faqEntity.retrieveFAQs(new FAQEntity.DataCallback() {
            @Override
            public void onSuccess(ArrayList<FAQ> faqs) {
                int faqCount = faqs.size(); // Get the number of FAQs
                faqCountTextView.setText("FAQs: " + faqCount); // Update the TextView
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to load FAQs.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchSpecializationCount() {
        SpecializationEntity specializationEntity = new SpecializationEntity();
        specializationEntity.fetchSpecialization(new SpecializationEntity.DataCallback() {
            @Override
            public void onSuccess(ArrayList<Specialization> specializations) {
                int specCount = specializations.size(); // Get the number of FAQs
                specializationTextView.setText("Specializations: " + specCount); // Update the TextView
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to load FAQs.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDietPreferenceCount() {
        DietPreferenceEntity dietPreferenceEntity = new DietPreferenceEntity();
        dietPreferenceEntity.fetchDietPreference(new DietPreferenceEntity.DataCallback() {
            @Override
            public void onSuccess(ArrayList<DietPreference> dietPreferences) {
                int dietpreferenceCount = dietPreferences.size(); // Get the number of FAQs
                dietpreferenceTextView.setText("Diet Preference: " + dietpreferenceCount); // Update the TextView
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to load FAQs.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAllergyOptionCount() {
        AllergyOptionsEntity allergyOptionsEntity = new AllergyOptionsEntity();
        allergyOptionsEntity.retrieveAllergyOptions(new AllergyOptionsEntity.DataCallback() {
            @Override
            public void onSuccess(ArrayList<AllergyOptions> alleryOptions) {
                int allergyoptionsCount = alleryOptions.size(); // Get the number of FAQs
                allergyTextView.setText("Allergy Options: " + allergyoptionsCount); // Update the TextView
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to load FAQs.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    // Method to check if a string is numeric
    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true; // It's numeric
        } catch (NumberFormatException e) {
            return false; // It's not numeric
        }
    }

    // Your existing addPromoCode method
    private void addPromoCode() {
        String promoCode = promoCodeInput.getText().toString().trim();
        String discountValueStr = discountValuesInput.getText().toString().trim();

        // Check if promo code is empty
        if (promoCode.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a promo code.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if discount value is a number
        if (discountValueStr.isEmpty() || !isNumeric(discountValueStr)) {
            Toast.makeText(getContext(), "Please enter a valid discount value (number).", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse discount value as a number
        double discountValue = Double.parseDouble(discountValueStr);

        // Log the values being saved
        Log.d("AdminHomeFragment", "Adding Promo Code: " + promoCode + " with discount: " + discountValue);

        // Save promo code and discount value to database
        savePromoCodeToDatabase(promoCode, discountValue);
    }

    // Save the promo code to Firestore
    private void savePromoCodeToDatabase(String promoCode, double discountValue) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        PromoCode promo = new PromoCode(promoCode, discountValue);

        Log.d("AdminHomeFragment", "Saving promo code: " + promoCode + ", Discount: " + discountValue);

        db.collection("promoCode")
                .add(promo)
                .addOnSuccessListener(documentReference -> {
                    Log.d("AdminHomeFragment", "Promo code added with ID: " + documentReference.getId());
                    Toast.makeText(getContext(), "Promo code added successfully.", Toast.LENGTH_SHORT).show();
                    promoCodeInput.setText("");
                    discountValuesInput.setText("");
                })
                .addOnFailureListener(e -> {
                    Log.e("AdminHomeFragment", "Error adding promo code: " + e);
                    Toast.makeText(getContext(), "Failed to add promo code: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}