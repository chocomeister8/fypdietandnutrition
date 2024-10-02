package com.example.dietandnutritionapplication;

import android.os.Bundle;
import android.text.TextUtils;
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

import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordFragment extends Fragment {

    private EditText emailInput;
    private Button resetPasswordButton;
    private TextView messageTextView;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forgetpassword, container, false);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Find views by ID
        emailInput = view.findViewById(R.id.emailInput);
        resetPasswordButton = view.findViewById(R.id.resetPasswordButton);
        messageTextView = view.findViewById(R.id.messageTextView);

        // Set onClickListener for the button
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getContext(), "Enter your email!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Send password reset email
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                                if (task.isSuccessful()) {
                                    messageTextView.setText("Password reset email sent!");
                                } else {
                                    messageTextView.setText("Failed to send reset email.");
                                }
                            }
                        });
            }
        });

        return view;  // Return the inflated layout
    }
}
