package com.example.dietandnutritionapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class AdminHomeFragment extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adminhomepage);

        // Find the buttons in the layout
        Button viewAccountsButton = findViewById(R.id.viewAccountsButton);
        Button addFAQButton = findViewById(R.id.addFAQbutton);

        // Set an OnClickListener on the viewAccountsButton
        viewAccountsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to AccountsActivity
                Intent intent = new Intent(AdminHomeFragment.this, AccountsFragment.class);
                startActivity(intent);
            }
        });

        // Set an OnClickListener on the addFAQButton
        addFAQButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to FAQActivity
                Intent intent = new Intent(AdminHomeFragment.this, FAQFragment.class);
                startActivity(intent);
            }
        });
    }

   /* public AdminHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.adminhomepage, container, false);

        // Initialize ImageView and set click listeners
        Button viewAccountsButton = view.findViewById(R.id.viewAccountsButton);
        viewAccountsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AccountsFragment.class);
            startActivity(intent);
        });

        Button addFAQButton = view.findViewById(R.id.addFAQbutton);
        addFAQButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FAQFragment.class);
            startActivity(intent);
        });
        return view;
    }*/
}//