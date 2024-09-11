package com.example.dietandnutritionapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class AccountsController extends ArrayAdapter<Object> {
    public AccountsController(Context context, List<Object> accounts) {
        super(context, 0, accounts);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.account_item, parent, false);
        }
        Object account = getItem(position);

        TextView firstnameTextView = convertView.findViewById(R.id.firstName);
        TextView lastnameTextView = convertView.findViewById(R.id.lastName);
        TextView usernameTextView = convertView.findViewById(R.id.username);
        TextView phoneNumberTextView = convertView.findViewById(R.id.phone);
        TextView emailTextView = convertView.findViewById(R.id.email);
        TextView genderTextView = convertView.findViewById(R.id.gender);
        TextView accountTypeTextView = convertView.findViewById(R.id.accountType); // For Nutritionist

        if (account instanceof Admin) {
            Admin admin = (Admin) account;
            firstnameTextView.setText(admin.getFirstName());
            lastnameTextView.setText(admin.getLastName());
            usernameTextView.setText(admin.getUsername());
            genderTextView.setText(admin.getGender());
            phoneNumberTextView.setText(admin.getPhoneNumber());
            emailTextView.setText(admin.getEmail());
            accountTypeTextView.setText(admin.getRole());

            accountTypeTextView.setVisibility(View.GONE); // Not needed for Admin
        } /*else if (account instanceof User) {
            User user = (User) account;
            nameTextView.setText(user.getName());
            emailTextView.setText(user.getEmail());
            specializationTextView.setVisibility(View.GONE); // Not needed for User
        } */

        return convertView;
    }
}
