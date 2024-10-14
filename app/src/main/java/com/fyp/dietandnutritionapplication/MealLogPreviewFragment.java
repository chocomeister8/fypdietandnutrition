package com.fyp.dietandnutritionapplication;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MealLogPreviewFragment extends Fragment {

    private TextView dateTextView;
    private TextView registerToAccessTextView;
    private TextView progressCalorieLimitTextView;
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guest_meallogpreview, container, false);

        dateTextView = view.findViewById(R.id.dateTextView);
        registerToAccessTextView = view.findViewById(R.id.register_to_access);
        progressCalorieLimitTextView = view.findViewById(R.id.progress_calorielimit);

        // Set today's date by default
        updateDateTextView(calendar);

        dateTextView.setOnClickListener(v -> showDatePickerDialog());

        registerToAccessTextView.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).replaceFragment(new URegisterFragment());
            }
        });

        progressCalorieLimitTextView.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).replaceFragment(new URegisterFragment());
            }
        });


        return view;
    }

    private void showDatePickerDialog() {
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            updateDateTextView(calendar);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void updateDateTextView(Calendar calendar) {
        String formattedDate = dateFormat.format(calendar.getTime());
        if (isToday(calendar)) {
            dateTextView.setText("Today");
        } else {
            dateTextView.setText(formattedDate);
        }
    }

    private boolean isToday(Calendar calendar) {
        Calendar today = Calendar.getInstance();
        return calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                calendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH);
    }


}

