package com.example.dietandnutritionapplication;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

public class LoginController {
    public LoginController(){

    }

    public void checkLogin(String enteredUsername, String enteredPassword, Context context) {
        UserAccountEntity userAccountEntity = new UserAccountEntity();

        // Check if the context is an instance of MainActivity before casting
        if (context instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) context;
            userAccountEntity.login(enteredUsername, enteredPassword, context, mainActivity);
        } else {
            throw new IllegalArgumentException("Context must be an instance of MainActivity");
        }
    }
}
