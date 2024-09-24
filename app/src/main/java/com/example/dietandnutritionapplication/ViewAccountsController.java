package com.example.dietandnutritionapplication;

import android.widget.Toast;

import java.util.ArrayList;

public class ViewAccountsController {
    ArrayList<Profile> profiles = new ArrayList<>();
    public ViewAccountsController(){

    }
    public ArrayList<Profile> retrieveAccounts(){
        UserAccountEntity userAccountEntity = new UserAccountEntity();
        userAccountEntity.fetchAccounts(new UserAccountEntity.DataCallback() {
            @Override
            public void onSuccess(ArrayList<Profile> accounts) {
                profiles.clear(); // Clear the filtering list
                profiles.addAll(accounts);
                // Store profiles for filtering
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
//                Toast.makeText(getContext(), "Failed to load accounts.", Toast.LENGTH_SHORT).show();
            }
        });
        return profiles;
    }
}
