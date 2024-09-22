package com.example.dietandnutritionapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginFragment extends Fragment {
    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private TextView regiesterText;
    private FirebaseFirestore db;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance(); // Initialize Firestore
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.login, container, false);
        usernameEditText = view.findViewById(R.id.firstName);
        passwordEditText = view.findViewById(R.id.editTextTextPassword);
        loginButton = view.findViewById(R.id.loginbutton);

        TextView myTextView = view.findViewById(R.id.noregis);
        myTextView.setClickable(true);

        myTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).replaceFragment(new URegisterFragment());
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredUsername = usernameEditText.getText().toString();
                String enteredPassword = passwordEditText.getText().toString();


                db.collection("Users")
                        .whereEqualTo("username", enteredUsername)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                if (!querySnapshot.isEmpty()) {
                                    QueryDocumentSnapshot document = (QueryDocumentSnapshot) querySnapshot.getDocuments().get(0);
                                    String dbPassword = document.getString("password");
                                    String role = document.getString("role");
                                    String username = document.getString("username");

                                    if (enteredPassword.equals(dbPassword)) {

                                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("loggedInUserName", username);  // Save the user email
                                        editor.apply();

                                        Toast.makeText(getActivity(), "Login Successful", Toast.LENGTH_SHORT).show();
                                        if ("user".equals(role)) {
                                            ((MainActivity) getActivity()).switchToUserMode();
                                        } else if ("admin".equals(role)) {
                                            ((MainActivity) getActivity()).switchToAdminMode();
                                        } else if ("nutritionist".equals(role)) {
                                            ((MainActivity) getActivity()).switchToNutriMode();
                                        }
                                    } else {
                                        Toast.makeText(getActivity(), "Invalid password", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "User not found", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Exception e = task.getException();
                                Log.e("LoginError", "Error checking login", e);
                                Toast.makeText(getActivity(), "Error checking login: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        return view;
    }
}