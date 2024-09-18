package com.example.dietandnutritionapplication;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddAdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddAdminFragment extends Fragment {

    private EditText userNameEditText, passwordEditText, phoneEditText,firstNameEditText,lastNameEditText,emailEditText,dobEditText;
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
        phoneEditText = view.findViewById(R.id.phoneEditText);
        firstNameEditText = view.findViewById(R.id.firstNameAdmin);
        lastNameEditText = view.findViewById(R.id.lastNameAdmin);
        emailEditText = view.findViewById(R.id.email);
        dobEditText = view.findViewById(R.id.dob);
        radioGroupGender = view.findViewById(R.id.radioGroupGender);
        addAdmin = view.findViewById(R.id.addButton);

        pd = new ProgressDialog(getActivity());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        addAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get values from the EditTexts
                String userName = userNameEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String firstName = firstNameEditText.getText().toString();
                String lastName = lastNameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String dob = dobEditText.getText().toString();
                String role = "admin";
                int selectedId = radioGroupGender.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    // No RadioButton selected
                    Toast.makeText(getActivity(), "Please select a gender.", Toast.LENGTH_SHORT).show();
                    return;
                }

                RadioButton selectedRadioButton = view.findViewById(selectedId);
                String selectedGender = selectedRadioButton.getText().toString();

                if (firstName.isEmpty() || lastName.isEmpty() || userName.isEmpty() ||
                        email.isEmpty() || phone.isEmpty() || password.isEmpty() || dob.isEmpty()) {
                    // Display error message
                    Toast.makeText(getActivity(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                    return; // Exit the method early
                }

                if (!isValidEmail(email)) {
                    Toast.makeText(getActivity(), "Please enter a valid email.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate phone number (length must be 8 and start with 8 or 9)
                if (!isValidPhoneNumber(phone)) {
                    Toast.makeText(getActivity(), "Please enter a valid phone number.", Toast.LENGTH_SHORT).show();
                    return;
                }
                UserAccountEntity userAccountEntity = new UserAccountEntity();
                userAccountEntity.addAdmin(firstName, userName, dob, email, phone, selectedGender, password, getActivity(),
                        new UserAccountEntity.RegisterCallback() {
                            @Override
                            public void onSuccess() {

                                Toast.makeText(getActivity(), "Registration successful", Toast.LENGTH_SHORT).show();
                                ((MainActivity) getActivity()).replaceFragment(new LandingFragment());
                            }

                            @Override
                            public void onFailure(String errorMessage) {

                                Toast.makeText(getActivity(), "Registration failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                );

//                insertAdmin(firstName, lastName, userName, password, phone, dob, email, selectedGender, role);
//                redirectToViewAllAccounts();
            }

        });
        return view;
    }

    private void insertAdmin(String firstName, String lastName, String userName, String phone, String dob, String email, String password, String gender, String role){

        DocumentReference newAdmin = db.collection("Users").document(); // Auto-generated ID

        Map<String, Object> admin = new HashMap<>();

        String id = UUID.randomUUID().toString();
        admin.put("id",newAdmin);
        admin.put("firstName", firstName);
        admin.put("lastName", lastName);
        admin.put("username", userName);
        admin.put("dob", dob);
        admin.put("phoneNumber", phone);
        admin.put("email", email);
        admin.put("password", password);
        admin.put("gender", gender);
        admin.put("role", role);



        db.collection("Users").document(id).set(admin).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.dismiss();
                Toast.makeText(getActivity(), "New Admin Added", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Failed to add", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isValidPhoneNumber(String phone) {
        // Regular expression for phone numbers starting with 8 or 9 and exactly 8 digits long
        return phone.matches("^[89][0-9]{7}$");
    }

    private void redirectToViewAllAccounts() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, new viewAccountsFragment()); // Use your container ID
        fragmentTransaction.addToBackStack(null); // Optional: Add to back stack
        fragmentTransaction.commit();
    }
}