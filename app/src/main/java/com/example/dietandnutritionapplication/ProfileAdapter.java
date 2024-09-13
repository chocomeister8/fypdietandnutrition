package com.example.dietandnutritionapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ProfileAdapter extends ArrayAdapter<Profile> {
    private Context context;
    private ArrayList<Profile> profiles;

    public ProfileAdapter(Context context, ArrayList<Profile> profiles) {
        super(context, 0, profiles);
        this.context = context;
        this.profiles = profiles;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.account_item, parent, false);
        }

        Profile currentProfile = profiles.get(position);

        TextView usernameTextView = convertView.findViewById(R.id.username);
        TextView roleTextView = convertView.findViewById(R.id.role);

        usernameTextView.setText(currentProfile.getUsername());
        roleTextView.setText(currentProfile.getRole());

        return convertView;
    }
}
