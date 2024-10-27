package com.fyp.dietandnutritionapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PaymentConsultationFragment extends Fragment {

    // Define argument keys
    private static final String ARG_CONSULTATION_ID = "consultationId";

    // Variables to hold arguments
    private String consultationId;

    // Static consultation price
    private static final double CONSULTATION_PRICE = 150.00;

    // Firestore instance
    private FirebaseFirestore db;

    // UI elements
    private TextView discountTextView;
    private TextView estimatedTotalTextView;
    private EditText promoCodeEditText;
    private Button applyPromoButton;
    private Button btnPayPal;

    // Factory method to create a new instance of the fragment with arguments
    public static PaymentConsultationFragment newInstance(String consultationId) {
        PaymentConsultationFragment fragment = new PaymentConsultationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CONSULTATION_ID, consultationId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.payment_consultation, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Retrieve arguments from the bundle
        if (getArguments() != null) {
            consultationId = getArguments().getString(ARG_CONSULTATION_ID);
        }

        // Set the static consultation price in the corresponding TextView
        TextView priceTextView = view.findViewById(R.id.tvSubtotalValue);
        priceTextView.setText("$" + String.format("%.2f", CONSULTATION_PRICE));

        // Initialize UI elements
        discountTextView = view.findViewById(R.id.discountValue);
        estimatedTotalTextView = view.findViewById(R.id.tvEstimatedTotalValue);
        promoCodeEditText = view.findViewById(R.id.promoCode);
        applyPromoButton = view.findViewById(R.id.applyPromoButton);
        btnPayPal = view.findViewById(R.id.btnPayPal);

        // Set button click listener to apply promo code
        applyPromoButton.setOnClickListener(v -> {
            String promoCode = promoCodeEditText.getText().toString().trim();
            validatePromoCode(promoCode);
        });

        // Set button click listener for PayPal button
        btnPayPal.setOnClickListener(v -> {
            processPayment(); // Call the payment process method here
        });
        // Initialize the estimated total with the original price
        estimatedTotalTextView.setText("$" + String.format("%.2f", CONSULTATION_PRICE));

        return view;
    }

    private void processPayment() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String clientName = currentUser.getDisplayName(); // Get the current user's display name
            Log.d("PaymentConsultationFragment", "Current user: " + clientName); // Log user info
            updateConsultationWithClientName(clientName); // Update Firestore
        } else {
            Log.e("PaymentConsultationFragment", "No user is currently logged in");
            Toast.makeText(getContext(), "You must be logged in to make a payment.", Toast.LENGTH_SHORT).show();
        }

        // This assumes payment processing is successful
        showBookingSuccessMessage();
        navigateToConsultationsFragment();
    }

    private void updateConsultationWithClientName(String clientName) {
        if (consultationId != null && !consultationId.isEmpty()) {
            db.collection("Consultation_slots").document(consultationId)
                    .update("clientName", "hi")
                    .addOnSuccessListener(aVoid -> Log.d("FirebaseUpdate", "Client name added successfully"))
                    .addOnFailureListener(e -> {
                        Log.e("FirebaseUpdate", "Error adding client name", e);
                        Toast.makeText(getContext(), "Failed to update client name. Please try again.", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Log.e("FirebaseUpdate", "Consultation ID is null or empty");
        }
    }


    // Method to show success message
    private void showBookingSuccessMessage() {
        Toast.makeText(getContext(), "Your booking was successful! Thank you for your payment.", Toast.LENGTH_SHORT).show();
    }

    // Method to navigate to the ConsultationsFragment
    private void navigateToConsultationsFragment() {
        try {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, new ConsultationsFragment());
            transaction.addToBackStack(null);  // Adds to back stack for proper back navigation
            transaction.commit();

        } catch (Exception e) {
            Log.e("NavigationError", "Error navigating to ConsultationsFragment", e);
            Toast.makeText(getContext(), "Navigation failed, please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to validate promo code from Firestore
    private void validatePromoCode(String promoCode) {
        db.collection("promoCode")
                .whereEqualTo("promocode", promoCode)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        showInvalidPromoCodeError();
                    } else {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            Double discountValue = document.getDouble("discountValue");
                            if (discountValue != null) {
                                applyDiscount(discountValue);
                            } else {
                                Toast.makeText(getContext(), "Discount value not found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("PaymentConsultationFragment", "Error validating promo code", e);
                    Toast.makeText(getContext(), "Error checking promo code", Toast.LENGTH_SHORT).show();
                });
    }

    // Method to show error when promo code is invalid
    private void showInvalidPromoCodeError() {
        Toast.makeText(getContext(), "Promo code invalid", Toast.LENGTH_SHORT).show();
    }

    // Method to apply discount when promo code is valid
    private void applyDiscount(Double discountValue) {
        discountTextView.setText(String.format("-$%.2f", discountValue));
        double estimatedTotal = CONSULTATION_PRICE - discountValue;
        estimatedTotalTextView.setText("$" + String.format("%.2f", estimatedTotal));
    }
}
