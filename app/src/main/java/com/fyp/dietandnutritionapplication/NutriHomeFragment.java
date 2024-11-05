package com.fyp.dietandnutritionapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NutriHomeFragment extends Fragment {

    private ImageView logoutImageView;
    private RecyclerView bookingsRecyclerView;
    private Button addBookingButton;
    private Spinner filter_button;
    private List<String> bookingsList;
    private FirebaseFirestore firestore;
    private BookingsAdapter adapter;
    private TextView totalConsultationsText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nutri_homepage, container, false);

        // Initialize UI elements
        logoutImageView = view.findViewById(R.id.right_icon);
        Button button_recipes = view.findViewById(R.id.button_recipes);
        Button button_consultation = view.findViewById(R.id.consultation);
        Button button_profile = view.findViewById(R.id.profile);
        Button button_recommendation = view.findViewById(R.id.button_recommendRecipes);
        filter_button = view.findViewById(R.id.filter_button);
        bookingsRecyclerView = view.findViewById(R.id.bookings_recycler_view);
        addBookingButton = view.findViewById(R.id.add_booking_button);
        totalConsultationsText = view.findViewById(R.id.carbohydrates_value);

        firestore = FirebaseFirestore.getInstance();

        // Hardcoded bookings list for prototype
        bookingsList = new ArrayList<>();
        adapter = new BookingsAdapter(bookingsList);

//        bookingsList.add("Booking 1: John Doe - 10/10/2024");
//        bookingsList.add("Booking 2: Jane Smith - 12/10/2024");
//        bookingsList.add("Booking 3: Mark Johnson - 14/10/2024");
        // Set up Spinner for star ratings
        List<String> sortOptions = new ArrayList<>();
        sortOptions.add("▼ Nearest Date");
        sortOptions.add("▼ Most Recent Added");
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, sortOptions);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filter_button.setAdapter(sortAdapter);

        sortBookingsByDate(true);

        // Set up Spinner item selected listener
        filter_button.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        sortBookingsByNearestTime();
                        break;
                    case 1:
                        // Most recent to oldest
                        sortBookingsByDate(true);
                        break;
                }
                // Update ListView with sorted reviews
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case where no selection is made if needed
            }
        });


        // Set up RecyclerView with hardcoded data
        bookingsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        BookingsAdapter adapter = new BookingsAdapter(bookingsList);
        bookingsRecyclerView.setAdapter(adapter);

        fetchBookingsFromFirestore();

        // Add booking button (prototype action)
        addBookingButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new ConsultationNFragment())
                    .addToBackStack(null)
                    .commit();
            });

        // Logout action
        logoutImageView.setOnClickListener(v -> {
            // Create an AlertDialog to confirm logout
            new AlertDialog.Builder(getContext())
                    .setTitle("Confirm Logout")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Log out", (dialog, which) -> {
                        // User confirmed to log out
                        Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).switchToGuestMode();
                            ((MainActivity) getActivity()).replaceFragment(new LoginFragment());
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        // User cancelled the logout action
                        dialog.dismiss();
                    })
                    .show();
        });


        button_recipes.setOnClickListener(v -> {
            // Replace current fragment with NavAllRecipesFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new NutriAllRecipesFragment())
                    .addToBackStack(null)  // Add to back stack to enable back navigation
                    .commit();
        });

        button_consultation.setOnClickListener(v -> {
            // Replace current fragment with NavConsultationFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new ConsultationNFragment())
                    .addToBackStack(null)
                    .commit();
        });

        button_profile.setOnClickListener(v -> {
            // Replace current fragment with NavProfileFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new ProfileNFragment())
                    .addToBackStack(null)
                    .commit();
        });

        button_recommendation.setOnClickListener(v -> {
            // Replace current fragment with NavProfileFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new ViewAllUserToRecommendFragment())
                    .addToBackStack(null)
                    .commit();
        });


        return view;
    }

    private void sortBookingsByDate(boolean mostRecentFirst) {
        Collections.sort(bookingsList, (b1, b2) -> {
            // Assuming the date format in bookingsList is "dd/MM/yyyy"
            String date1 = extractDate(b1);
            String date2 = extractDate(b2);
            Date parsedDate1 = parseDate(date1);
            Date parsedDate2 = parseDate(date2);

            if (parsedDate1 == null || parsedDate2 == null) return 0;

            return mostRecentFirst ? parsedDate2.compareTo(parsedDate1) : parsedDate1.compareTo(parsedDate2);
        });
    }
    private void sortBookingsByNearestTime() {
        Collections.sort(bookingsList, (b1, b2) -> {
            String date1 = extractDate(b1);
            String time1 = extractTime(b1);
            String date2 = extractDate(b2);
            String time2 = extractTime(b2);

            // Parse combined date and time for comparison
            Date parsedDateTime1 = parseDateAndTime(date1, time1);
            Date parsedDateTime2 = parseDateAndTime(date2, time2);

            // Ensure neither date is null before comparing
            if (parsedDateTime1 == null) return 1; // Treat null as later
            if (parsedDateTime2 == null) return -1; // Treat null as later
            return parsedDateTime1.compareTo(parsedDateTime2);
        });
    }

    // Helper method to extract the date part from the booking string
    private String extractDate(String booking) {
        // Assuming date is formatted as "Date: dd/MM/yyyy" in booking string
        String[] lines = booking.split("\n");
        for (String line : lines) {
            if (line.startsWith("Date: ")) {
                return line.replace("Date: ", "").trim();
            }
        }
        return null;
    }

    private String extractTime(String booking) {
        // Assuming time is formatted as "Time: HH:mm" in booking string
        String[] lines = booking.split("\n");
        for (String line : lines) {
            if (line.startsWith("Time: ")) {
                return line.replace("Time: ", "").trim();
            }
        }
        return null;
    }

    // Helper method to parse date strings
    private Date parseDateAndTime(String dateStr, String timeStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        try {
            return dateFormat.parse(dateStr + " " + timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Return null if parsing fails
        }
    }

    private Date parseDate(String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void fetchBookingsFromFirestore() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            firestore.collection("Users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String loggedInNutritionistName = documentSnapshot.getString("username");

                            // Query consultation slots where `nutritionistName` matches the logged-in user
                            firestore.collection("Consultation_slots")
                                    .whereEqualTo("nutritionistName", loggedInNutritionistName)
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                        bookingsList.clear();

                                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                            Slot slot = document.toObject(Slot.class);

                                            // Check if the booking status is not 'completed'
                                            if (!"completed".equalsIgnoreCase(slot.getStatus())) {
                                                // Create a string to display in the RecyclerView
                                                String bookingInfo = "Nutritionist: " + slot.getNutritionistName() +
                                                        "\nDate: " + slot.getDate() +
                                                        "\nTime: " + slot.getTime() +
                                                        "\nStatus: " + slot.getStatus() +
                                                        "\nZoom Link: " + slot.getZoomLink() + "\n";

                                                // Add the formatted string to the bookings list
                                                bookingsList.add(bookingInfo);
                                            }
                                        }

                                        // Update counts
                                        totalConsultationsText.setText(" " + bookingsList.size());

                                        adapter.notifyDataSetChanged();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Failed to fetch bookings", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to fetch user details", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isUpcoming(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            // Parse the provided date string into a Date object
            Date parsedDate = dateFormat.parse(date);
            Date currentDate = new Date();

            // Compare the parsed date with the current date
            return parsedDate != null && parsedDate.after(currentDate);
        } catch (ParseException e) {
            // Handle the parsing exception
            e.printStackTrace();
            return false; // Return false if there's an error in parsing
        }
    }


    // RecyclerView Adapter for hardcoded bookings
    private static class BookingsAdapter extends RecyclerView.Adapter<BookingsAdapter.BookingViewHolder> {
        private final List<String> bookings;

        public BookingsAdapter(List<String> bookings) {
            this.bookings = bookings;
        }

        @NonNull
        @Override
        public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new BookingViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
            String booking = bookings.get(position);
            holder.bookingTextView.setText(booking);

            Linkify.addLinks(holder.bookingTextView, Linkify.WEB_URLS);
        }

        @Override
        public int getItemCount() {
            return bookings.size();
        }

        static class BookingViewHolder extends RecyclerView.ViewHolder {
            TextView bookingTextView;

            public BookingViewHolder(@NonNull View itemView) {
                super(itemView);
                bookingTextView = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}
