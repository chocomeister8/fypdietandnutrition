package com.fyp.dietandnutritionapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class NutriHomeFragment extends Fragment {

    private ImageView logoutImageView;
    private RecyclerView bookingsRecyclerView;
    private Button addBookingButton;
    private List<String> bookingsList;
    private FirebaseFirestore firestore;
    private BookingsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nutri_homepage, container, false);

        // Initialize UI elements
        logoutImageView = view.findViewById(R.id.right_icon);
        Button button_recipes = view.findViewById(R.id.button_recipes);
        Button button_consultation = view.findViewById(R.id.consultation);
        Button button_profile = view.findViewById(R.id.profile);
        bookingsRecyclerView = view.findViewById(R.id.bookings_recycler_view);
        addBookingButton = view.findViewById(R.id.add_booking_button);

        firestore = FirebaseFirestore.getInstance();

        // Hardcoded bookings list for prototype
        bookingsList = new ArrayList<>();
        adapter = new BookingsAdapter(bookingsList);

//        bookingsList.add("Booking 1: John Doe - 10/10/2024");
//        bookingsList.add("Booking 2: Jane Smith - 12/10/2024");
//        bookingsList.add("Booking 3: Mark Johnson - 14/10/2024");

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
                            ((MainActivity) getActivity()).replaceFragment(new LandingFragment());
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

        return view;
    }

    private void fetchBookingsFromFirestore() {
        firestore.collection("Consultation_slots").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    bookingsList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Slot slot = document.toObject(Slot.class);

                        // Create a string to display in the RecyclerView
                        String bookingInfo = "Nutritionist: " + slot.getNutritionistName() +
                                "\nDate: " + slot.getDate() +
                                "\nTime: " + slot.getTime() +
                                "\nStatus: " + slot.getStatus();

                        // Add the formatted string to the bookings list
                        bookingsList.add(bookingInfo);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to fetch bookings", Toast.LENGTH_SHORT).show();
                });
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
