package com.fyp.dietandnutritionapplication;

import java.util.ArrayList;

public class ViewAccountsController {
    ArrayList<Profile> profiles = new ArrayList<>();
    public ViewAccountsController(){

    }
    public void retrieveAccounts(final UserAccountEntity.DataCallback callback) {
        UserAccountEntity userAccountEntity = new UserAccountEntity();
        userAccountEntity.fetchAccounts(new UserAccountEntity.DataCallback() {
            @Override
            public void onSuccess(ArrayList<Profile> accounts) {
                profiles.clear();
                profiles.addAll(accounts);
                callback.onSuccess(profiles);
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                callback.onFailure(e);
            }
        });
    }

    public void retrievePendingNutritionist(final UserAccountEntity.DataCallback callback) {
        UserAccountEntity userAccountEntity = new UserAccountEntity();
        userAccountEntity.retrievePendingNutritionists(new UserAccountEntity.DataCallback() {
            @Override
            public void onSuccess(ArrayList<Profile> accounts) {
                profiles.clear();
                profiles.addAll(accounts);
                callback.onSuccess(profiles);
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                callback.onFailure(e);
            }
        });
    }
}
