package com.fyp.dietandnutritionapplication;

import android.content.Context;
import android.widget.Toast;

public class URegisterController {
    public URegisterController(){

    }
    // Check here for valid info 
    public void checkRegister(String firstName, String lastName, String userName, String dob, String email, String phone, String gender, String password, String datejoined, Context context) {
        UserAccountEntity userAccountEntity = new UserAccountEntity();
        userAccountEntity.registerUser(firstName, lastName, userName, dob, email, phone, gender, password, datejoined, context,
                new UserAccountEntity.RegisterCallback() {
                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(context, "Registration successful, please verify your email!", Toast.LENGTH_SHORT).show();

                        // Ensure context is MainActivity before casting
                        if (context instanceof MainActivity) {
                            ((MainActivity) context).replaceFragment(new LoginFragment());
                        } else {
                            Toast.makeText(context, "Error: Context is not an instance of MainActivity", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        // Ensure context is valid
                        Toast.makeText(context, "Registration failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}
