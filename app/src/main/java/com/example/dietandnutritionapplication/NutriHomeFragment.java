package com.example.dietandnutritionapplication;

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

import java.util.ArrayList;
import java.util.List;

public class NutriHomeFragment extends Fragment {

    private ImageView logoutImageView;
    private RecyclerView bookingsRecyclerView;
    private Button addBookingButton;
    private List<String> bookingsList;

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

        // Hardcoded bookings list for prototype
        bookingsList = new ArrayList<>();
        bookingsList.add("Booking 1: John Doe - 10/10/2024");
        bookingsList.add("Booking 2: Jane Smith - 12/10/2024");
        bookingsList.add("Booking 3: Mark Johnson - 14/10/2024");

        // Set up RecyclerView with hardcoded data
        bookingsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        BookingsAdapter adapter = new BookingsAdapter(bookingsList);
        bookingsRecyclerView.setAdapter(adapter);

        // Add booking button (prototype action)
        addBookingButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Feature Coming Soon", Toast.LENGTH_SHORT).show();
        });

        // Logout action
        logoutImageView.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).replaceFragment(new LandingFragment());
            }
        });

        button_recipes.setOnClickListener(v -> {
            // Replace current fragment with NavAllRecipesFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new RecipesFolderFragment())
                    .addToBackStack(null)  // Add to back stack to enable back navigation
                    .commit();
        });

//        button_consultation.setOnClickListener(v -> {
//            // Replace current fragment with NavConsultationFragment
//            requireActivity().getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.frame_layout, new ConsultationsNFragment())
//                    .addToBackStack(null)
//                    .commit();
//        });

        button_profile.setOnClickListener(v -> {
            // Replace current fragment with NavProfileFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new ProfileNFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
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
