package com.fyp.dietandnutritionapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ProfileAdapter extends ArrayAdapter<Profile> {
    private Context context;
    private ArrayList<Profile> profiles;


    public ProfileAdapter(Context context, ArrayList<Profile> profiles) {
        super(context, 0, profiles);
        this.context = context;
        this.profiles = profiles;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.account_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.usernameTextView = convertView.findViewById(R.id.username);
            viewHolder.fullnameTextView = convertView.findViewById(R.id.fullname);
            viewHolder.roleTextView = convertView.findViewById(R.id.role);
            viewHolder.dateJoinedTextView = convertView.findViewById(R.id.dateJoined);
            viewHolder.statusTextView = convertView.findViewById(R.id.status);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Profile currentProfile = profiles.get(position);

        viewHolder.usernameTextView.setText(currentProfile.getUsername());
        String fullName = currentProfile.getFirstName() + " " + currentProfile.getLastName();
        viewHolder.fullnameTextView.setText(fullName);
        viewHolder.roleTextView.setText(currentProfile.getRole());
        viewHolder.dateJoinedTextView.setText(currentProfile.getDateJoined());
        viewHolder.statusTextView.setText(currentProfile.getStatus());

        if ("deactivated".equalsIgnoreCase(currentProfile.getStatus())) {
            convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.suspendedAccountRed));
        } else {
            convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.activeAccountGreen));
        }

        return convertView;
    }

    static class ViewHolder {
        TextView usernameTextView;
        TextView roleTextView;
        TextView fullnameTextView;
        TextView dateJoinedTextView;
        TextView statusTextView;
    }
}