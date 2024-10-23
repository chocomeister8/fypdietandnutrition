package com.fyp.dietandnutritionapplication;

import android.content.Context;
import android.widget.Toast;

public class NRegisterController {
    public NRegisterController(){

    }

    public void checkRegister(String firstName, String lastName, String userName, String email, String phone, String gender, String password, String specialization, String experience, String datejoined, Context context) {
        UserAccountEntity userAccountEntity = new UserAccountEntity();
        userAccountEntity.registerNutri(firstName, lastName, userName, email, phone, gender, password, specialization, experience, datejoined, context,
                new UserAccountEntity.RegisterCallback() {
                    @Override
                    public void onSuccess(String message) {

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
