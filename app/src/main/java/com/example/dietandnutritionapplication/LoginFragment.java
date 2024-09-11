package com.example.dietandnutritionapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    ArrayList<Profile> accountArray = new ArrayList<>();
    private TextView regiesterText;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
                // Get the text from the EditTexts
                String enteredUsername = usernameEditText.getText().toString();
                String enteredPassword = passwordEditText.getText().toString();


                User user1 = new User();
                user1.setUsername("zaw");
                user1.setPassword("123");
                user1.setRole("user");
                accountArray.add(user1);
                Admin admin1 = new Admin();
                admin1.setUsername("admin");
                admin1.setPassword("admin123");
                admin1.setRole("admin");
                accountArray.add(admin1);
                Nutritionist nutritionist1 = new Nutritionist();
                nutritionist1.setUsername("sim");
                nutritionist1.setPassword("123");
                nutritionist1.setRole("nutritionist");
                accountArray.add(nutritionist1);

                for(Profile account:accountArray){
                    if (enteredUsername.equals(account.getUsername()) && enteredPassword.equals(account.getPassword()) && account.getRole().equals("user")) {

                        Toast.makeText(getActivity(), "Login Successful", Toast.LENGTH_SHORT).show();
                        ((MainActivity) getActivity()).replaceFragment(new RecipeFragment());
                        break;

                    } else if (enteredUsername.equals(account.getUsername()) && enteredPassword.equals(account.getPassword()) && account.getRole().equals("admin")) {
                        Toast.makeText(getActivity(), "Login Successful", Toast.LENGTH_SHORT).show();
                        ((MainActivity) getActivity()).switchToAdminMode();
                        break;
                    }else if (enteredUsername.equals(account.getUsername()) && enteredPassword.equals(account.getPassword()) && account.getRole().equals("nutritionist")) {
                        Toast.makeText(getActivity(), "Login Successful", Toast.LENGTH_SHORT).show();
                        ((MainActivity) getActivity()).replaceFragment(new RecipeFragment());
                        break;
                    }
                    else {

                        Toast.makeText(getActivity(), "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }

                }


            }
        });
        return view;
    }
}