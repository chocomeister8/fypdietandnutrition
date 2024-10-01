package com.example.dietandnutritionapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.appcompat.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Calendar;
import java.util.Locale;

public class UserDiaryFragment extends Fragment {

    private Button addDiaryEntryButton;
    private String username;
    private List<UserDiary> diaryEntries;
    private LinearLayout diaryEntriesContainer;
    private TextView titleTextView, descriptionTextView, dateTextView, timestampTextView, selectedTagsTextView, tagsTextView;
    private Spinner mealTypeSpinner;
    private EditText descriptionEditText;
    private Button addTagsButton, confirmTagsButton, cancelTagsButton, sortButton, clearFilterButton ;
    private boolean ascendingOrder = true;
    private EditText etDatePicker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_diary, container, false);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("loggedInUserName", null);

        addDiaryEntryButton = view.findViewById(R.id.addDiaryEntryButton);
        etDatePicker = view.findViewById(R.id.et_date_picker);
        diaryEntriesContainer = view.findViewById(R.id.diaryEntriesContainer);
        clearFilterButton = view.findViewById(R.id.clearFilterButton);

        diaryEntries = new ArrayList<>();

        addDiaryEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDiaryEntryDialog();
            }
        });

        fetchDiaryEntries();

        etDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchshowDatePickerDialog();
            }
        });

        clearFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFilter();
            }
        });

        return view;
    }

    private void fetchDiaryEntries() {
        UserDiaryController userDiaryController = new UserDiaryController();
        userDiaryController.fetchDiaryEntries(username, new UserDiary.OnDiaryEntriesFetchedListener() {
            @Override
            public void onDiaryEntriesFetched(List<UserDiary> diaryEntries) {
                diaryEntriesContainer.removeAllViews();

                Collections.sort(diaryEntries, new Comparator<UserDiary>() {
                    @Override
                    public int compare(UserDiary entry1, UserDiary entry2) {
                        // Assuming getEntryDateTime() returns a Timestamp
                        return entry2.getEntryDateTime().compareTo(entry1.getEntryDateTime());
                    }
                });

                for (UserDiary entry : diaryEntries) {
                    addDiaryEntryView(entry);
                }
            }
        });
    }

    private void filterDiaryEntriesByDate(String selectedDate) {
        UserDiaryController userDiaryController = new UserDiaryController();
        userDiaryController.fetchDiaryEntries(username, new UserDiary.OnDiaryEntriesFetchedListener() {
            @Override
            public void onDiaryEntriesFetched(List<UserDiary> diaryEntries) {
                diaryEntriesContainer.removeAllViews();
                List<UserDiary> filteredEntries = new ArrayList<>();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
                Log.d("FilterDebug", "Selected Date: " + selectedDate);

                for (UserDiary entry : diaryEntries) {
                    if (entry.getEntryDateTime() != null) {
                        String entryDate = sdf.format(entry.getEntryDateTime());
                        Log.d("FilterDebug", "Entry Date: " + entryDate);
                        if (entryDate.equals(selectedDate)) {
                            Log.d("FilterDebug", "Date matched: " + entryDate);
                            filteredEntries.add(entry);
                        } else {
                            Log.d("FilterDebug", "Date did not match: " + entryDate + " != " + selectedDate);
                        }
                    }
                }

                diaryEntriesContainer.removeAllViews();
                for (UserDiary entry : filteredEntries) {
                    addDiaryEntryView(entry);
                }
            }
        });

    }

    private void addDiaryEntryView(UserDiary entry) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View entryView = inflater.inflate(R.layout.user_diary_input, diaryEntriesContainer, false);

        titleTextView = entryView.findViewById(R.id.diaryTitle);
        descriptionTextView = entryView.findViewById(R.id.diaryDescription);
        timestampTextView = entryView.findViewById(R.id.diaryTimestamp);
        tagsTextView = entryView.findViewById(R.id.cardDiaryTags);

        titleTextView.setText(entry.getMealType());
        descriptionTextView.setText(entry.getThoughts());

        String tags = entry.getTags();

        if (tags != null && !tags.isEmpty()) {
            tagsTextView.setText(tags.replace(",", ", ")); // Add formatting if needed
        }

        if (entry.getEntryDateTime() != null) {
            String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(entry.getEntryDateTime());
            timestampTextView.setText(formattedDate);
        }

        diaryEntriesContainer.addView(entryView);
        fetchDiaryEntries();
    }

    private String formatDate(int day, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    private void showAddDiaryEntryDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_add_diary_entry, null);

        dateTextView = dialogView.findViewById(R.id.dateTextView);
        mealTypeSpinner = dialogView.findViewById(R.id.mealTypeSpinner);
        descriptionEditText = dialogView.findViewById(R.id.descriptionEditText);
        addTagsButton = dialogView.findViewById(R.id.addTagsButton);
        selectedTagsTextView = dialogView.findViewById(R.id.selectedTagsTextView);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.meal_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealTypeSpinner.setAdapter(adapter);

        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        addTagsButton.setOnClickListener(v -> {
            showTagSelectionDialog(tags -> selectedTagsTextView.setText(tags));
        });

        UserDiaryController userDiaryController = new UserDiaryController();

        // Build the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView)
                .setTitle("Add Diary Entry")
                .setPositiveButton("Save", (dialog, which) -> {

                    String selectedDate = dateTextView.getText().toString();
                    String selectedMealType = mealTypeSpinner.getSelectedItem().toString();
                    String description = descriptionEditText.getText().toString();
                    String selectedTags = selectedTagsTextView.getText().toString();

                        String[] dateParts = selectedDate.split("/");
                        int day = Integer.parseInt(dateParts[0]);
                        int month = Integer.parseInt(dateParts[1]) - 1;
                        int year = Integer.parseInt(dateParts[2]);

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day);
                        Timestamp entryDateTime = new Timestamp(calendar.getTimeInMillis());

                        userDiaryController.handleDiaryEntry(entryDateTime, selectedMealType, description, selectedTags, username);

                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void showTagSelectionDialog(TagSelectionListener listener) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View tagDialogView = inflater.inflate(R.layout.dialog_select_tags, null);

        RadioGroup question1 = tagDialogView.findViewById(R.id.question1);
        RadioGroup question2 = tagDialogView.findViewById(R.id.question2);
        RadioGroup question3 = tagDialogView.findViewById(R.id.question3);
        RadioGroup question4 = tagDialogView.findViewById(R.id.question4);

        confirmTagsButton = tagDialogView.findViewById(R.id.confirmTagsButton);
        cancelTagsButton = tagDialogView.findViewById(R.id.cancelTagsButton);
        // Create the dialog
        AlertDialog.Builder tagDialogBuilder = new AlertDialog.Builder(getContext());
        tagDialogBuilder.setView(tagDialogView).setTitle("Select Tags");

        AlertDialog tagDialog = tagDialogBuilder.create();

        confirmTagsButton.setOnClickListener(v -> {
            StringBuilder selectedTagsBuilder = new StringBuilder();

            if (question1.getCheckedRadioButtonId() != -1) {
                selectedTagsBuilder.append(((RadioButton) tagDialogView.findViewById(question1.getCheckedRadioButtonId())).getText()).append(", ");
            }
            if (question2.getCheckedRadioButtonId() != -1) {
                selectedTagsBuilder.append(((RadioButton) tagDialogView.findViewById(question2.getCheckedRadioButtonId())).getText()).append(", ");
            }
            if (question3.getCheckedRadioButtonId() != -1) {
                selectedTagsBuilder.append(((RadioButton) tagDialogView.findViewById(question3.getCheckedRadioButtonId())).getText()).append(", ");
            }
            if (question4.getCheckedRadioButtonId() != -1) {
                selectedTagsBuilder.append(((RadioButton) tagDialogView.findViewById(question4.getCheckedRadioButtonId())).getText()).append(", ");
            }

            if (selectedTagsBuilder.length() > 0) {
                selectedTagsBuilder.setLength(selectedTagsBuilder.length() - 2); // Remove trailing comma and space
            }

            listener.onTagsSelected(selectedTagsBuilder.toString());

            tagDialog.dismiss();
        });
        cancelTagsButton.setOnClickListener(v -> {
            tagDialog.dismiss();
        });

        tagDialog.show();
    }

    private void searchshowDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String selectedDate = formatDate(dayOfMonth, month, year);
                etDatePicker.setText(selectedDate);
                filterDiaryEntriesByDate(selectedDate);
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Set selected date to the TextView
                String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                dateTextView.setText(selectedDate);
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    public interface TagSelectionListener {
        void onTagsSelected(String selectedTags);
    }

    private void clearFilter() {
        fetchDiaryEntries();
        etDatePicker.setText("");
    }

}