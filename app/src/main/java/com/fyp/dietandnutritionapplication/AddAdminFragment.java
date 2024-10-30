package com.fyp.dietandnutritionapplication;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddAdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddAdminFragment extends Fragment {

    private EditText userNameEditText, passwordEditText, confirmpasswordEditText, phoneEditText,firstNameEditText,lastNameEditText,emailEditText,dobEditText;
    private Button addAdmin;
    private RadioGroup radioGroupGender;
    private RadioButton selectedRadioButton;

    ProgressDialog pd;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddAdminFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddAdminFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddAdminFragment newInstance(String param1, String param2) {
        AddAdminFragment fragment = new AddAdminFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.addadminpage, container, false);
        MainActivity mainActivity = (MainActivity) getActivity();
        userNameEditText = view.findViewById(R.id.usernameEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        confirmpasswordEditText = view.findViewById(R.id.cfmpasswordEditText);
        phoneEditText = view.findViewById(R.id.phoneEditText);
        firstNameEditText = view.findViewById(R.id.firstNameAdmin);
        lastNameEditText = view.findViewById(R.id.lastNameAdmin);
        emailEditText = view.findViewById(R.id.email);
        dobEditText = view.findViewById(R.id.dob);
        radioGroupGender = view.findViewById(R.id.radioGroupGender);
        addAdmin = view.findViewById(R.id.addButton);
        Button backButton = view.findViewById(R.id.backButton);

        pd = new ProgressDialog(getActivity());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        dobEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        backButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        addAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get values from the EditTexts
                String userName = userNameEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmpasswordEditText.getText().toString();
                String firstName = firstNameEditText.getText().toString();
                String lastName = lastNameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String dob = dobEditText.getText().toString();
                String date = LocalDateTime.now(ZoneId.of("Asia/Singapore"))
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String role = "admin";

                int selectedId = radioGroupGender.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    Toast.makeText(getActivity(), "Please select a gender.", Toast.LENGTH_SHORT).show();
                    return;
                }

                RadioButton selectedRadioButton = view.findViewById(selectedId);
                String selectedGender = selectedRadioButton.getText().toString();

                // Check for empty fields
                if (firstName.isEmpty() || lastName.isEmpty() || userName.isEmpty() ||
                        email.isEmpty() || phone.isEmpty() || password.isEmpty() || dob.isEmpty()) {
                    Toast.makeText(getActivity(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate password match
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(getActivity(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate email format
                if (!isValidEmail(email)) {
                    Toast.makeText(getActivity(), "Please enter a valid email.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate phone number (length must be 8 and start with 8 or 9)
                if (!isValidPhoneNumber(phone)) {
                    Toast.makeText(getActivity(), "Please enter a valid phone number.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if email and username exist in the database (asynchronous checks)
                checkIfEmailExists(email, new EmailCheckCallback() {
                    @Override
                    public void onResult(boolean emailExists) {
                        if (emailExists) {
                            Toast.makeText(getActivity(), "Email already exists.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Check if the username exists after email is validated
                            checkIfUsernameExists(userName, new UsernameCheckCallback() {
                                @Override
                                public void onResult(boolean usernameExists) {
                                    if (usernameExists) {
                                        Toast.makeText(getActivity(), "Username already exists.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Proceed with registration if both email and username do not exist
                                        AddAdminController addAdminController = new AddAdminController();
                                        addAdminController.checkRegisterAdmin(firstName, lastName, userName, dob, email, phone, selectedGender, password, date, getActivity());

                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        return view;
    }

    public interface EmailCheckCallback {
        void onResult(boolean exists);
    }

    public void checkIfEmailExists(String email, EmailCheckCallback callback) {
        // Example with Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            // Email exists
                            callback.onResult(true);
                        } else {
                            // Email does not exist
                            callback.onResult(false);
                        }
                    }
                });
    }

    public interface UsernameCheckCallback {
        void onResult(boolean exists);
    }

    public void checkIfUsernameExists(String username, UsernameCheckCallback callback) {
        // Get the instance of Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query the "Users" collection where the username matches the provided username
        db.collection("Users").whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                // Username exists
                                callback.onResult(true);
                            } else {
                                // Username does not exist
                                callback.onResult(false);
                            }
                        } else {
                            // Handle the error
                            Log.e("FirestoreError", "Error checking username: ", task.getException());
                            callback.onResult(false); // Or handle the error appropriately
                        }
                    }
                });
    }


    public boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    public boolean isValidPhoneNumber(String phone) {
        return phone.length() == 8 && (phone.startsWith("8") || phone.startsWith("9"));
    }

    private void showDatePickerDialog() {
        // Create a Calendar instance to get the current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a DatePickerDialog using requireActivity() to get the context
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                // Set the selected date to the EditText
                dobEditText.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
            }
        }, year, month, day);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }

    private void redirectToViewAllAccounts() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, new viewAccountsFragment()); // Use your container ID
        fragmentTransaction.addToBackStack(null); // Optional: Add to back stack
        fragmentTransaction.commit();
    }

}