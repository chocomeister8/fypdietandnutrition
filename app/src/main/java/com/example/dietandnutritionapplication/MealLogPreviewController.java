package com.example.dietandnutritionapplication;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MealLogPreviewController extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView progressText;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_guest_meallogpreview);

    }
}

