package com.fyp.dietandnutritionapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText; // Import EditText for promo code input
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PaymentConsultationFragment extends Fragment {

    // Define argument keys
    private static final String ARG_CONSULTATION_ID = "consultationId";

    // Variables to hold arguments
    private String consultationId;

    // Static consultation price
    private static final double CONSULTATION_PRICE = 150.00;  // Static value for consultation price

    // Firestore instance
    private FirebaseFirestore db;

    // UI elements
    private TextView discountTextView;  // The TextView to show discount
    private TextView estimatedTotalTextView; // New TextView for estimated total
    private EditText promoCodeEditText; // EditText for user to enter promo code
    private Button applyPromoButton;      // Button to apply promo code

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
        TextView priceTextView = view.findViewById(R.id.tvSubtotalValue); // Assuming you have a TextView with this ID
        priceTextView.setText("$" + String.format("%.2f", CONSULTATION_PRICE)); // Static price

        // Initialize UI elements
        discountTextView = view.findViewById(R.id.discountValue); // Assuming this is the correct ID
        estimatedTotalTextView = view.findViewById(R.id.tvEstimatedTotalValue); // New TextView for estimated total
        promoCodeEditText = view.findViewById(R.id.promoCode); // Add this line to initialize the EditText
        applyPromoButton = view.findViewById(R.id.applyPromoButton);  // Assuming you have an Apply button in your XML

        // Set button click listener to apply promo code
        applyPromoButton.setOnClickListener(v -> {
            String promoCode = promoCodeEditText.getText().toString().trim(); // Get the promo code from the EditText
            validatePromoCode(promoCode);
        });

        // Initialize the estimated total with the original price
        estimatedTotalTextView.setText("$" + String.format("%.2f", CONSULTATION_PRICE));

        return view;
    }

    // Method to validate promo code from Firestore
    private void validatePromoCode(String promoCode) {
        // Query the Firestore to check if the promo code exists
        db.collection("promoCode")
                .whereEqualTo("promocode", promoCode)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Check if any documents returned
                    if (queryDocumentSnapshots.isEmpty()) {
                        // Promo code not found
                        showInvalidPromoCodeError();
                    } else {
                        // Promo code exists; get the discount value
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            // Assuming your promo code document has a field named "discountValue"
                            Double discountValue = document.getDouble("discountValue"); // Ensure this is the correct type

                            if (discountValue != null) {
                                // Apply the discount and update UI
                                applyDiscount(discountValue);
                            } else {
                                // Handle case where discountValue is null
                                Toast.makeText(getContext(), "Discount value not found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle errors
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
        discountTextView.setText(String.format("-$%.2f", discountValue));  // Display the discount value

        // Calculate the new estimated total after applying the discount
        double estimatedTotal = CONSULTATION_PRICE - discountValue;

        // Update the estimated total TextView
        estimatedTotalTextView.setText("$" + String.format("%.2f", estimatedTotal));
    }
}
