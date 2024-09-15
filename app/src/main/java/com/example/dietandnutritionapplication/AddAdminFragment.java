package com.example.dietandnutritionapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddAdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddAdminFragment extends Fragment {

    private EditText userNameEditText, passwordEditText, phoneEditText,firstNameAdmin,lastNameAdmin,email,dob;
    private Button addButton;
    private RadioGroup radioGroupGender;
    private RadioButton selectedRadioButton;

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
        firstNameAdmin = view.findViewById(R.id.firstNameAdmin);
        lastNameAdmin = view.findViewById(R.id.lastNameAdmin);
        email = view.findViewById(R.id.email);
        dob = view.findViewById(R.id.dob);
        radioGroupGender = view.findViewById(R.id.radioGroupGender);
        addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get values from the EditTexts
                String userName = userNameEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String role = "Admin";
                String firstName = firstNameAdmin.getText().toString();
                String lastName = lastNameAdmin.getText().toString();
                String emailText = email.getText().toString();
                String dobText = dob.getText().toString();
                int selectedId = radioGroupGender.getCheckedRadioButtonId();
                String selectedGender;
                if (selectedId != -1) {  // Ensure a RadioButton is selected
                    selectedRadioButton = view.findViewById(selectedId);
                    selectedGender = selectedRadioButton.getText().toString();
                } else {
                    // Handle the case where no RadioButton is selected
                    selectedGender = "Not selected";
                }

                mainActivity.createAdminAccount(firstName, lastName, userName, password,phone,dobText,emailText,selectedGender,role);
                mainActivity.replaceFragment(new AdminHomeFragment());
            }

        });
        return view;
    }
}