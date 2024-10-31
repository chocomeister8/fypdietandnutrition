package com.fyp.dietandnutritionapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class UserDiaryFragment extends Fragment {

    private Button addDiaryEntryButton;
    private String username;
    private List<UserDiary> diaryEntries;
    private LinearLayout diaryEntriesContainer;
    private TextView titleTextView, descriptionTextView, dateTextView, timestampTextView, selectedTagsTextView, tagsTextView;
    private Spinner mealRecordSpinner;
    private EditText descriptionEditText;
    private Button addTagsButton, confirmTagsButton, cancelTagsButton, sortButton, clearFilterButton ;
    private boolean ascendingOrder = true;
    private EditText etDatePicker;
    private UserDiaryController userDiaryController;
    private ImageView moreOptionsIcon;
    private UserMealRecordController userMealRecordController;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_diary, container, false);

        userDiaryController = new UserDiaryController();
        userMealRecordController = new UserMealRecordController();
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

    private void fetchMealRecordsForDate(String selectedDateStr, String selectedMealRecordID) {
        userDiaryController.fetchAllMealsLogged(username, selectedDateStr, new MealRecord.OnMealsFetchedListener() {
            @Override
            public void onMealsFetched(List<MealRecord> mealRecords) {
                if (mealRecords != null && !mealRecords.isEmpty()) {
                    List<String> mealRecordStrings = new ArrayList<>();
                    final List<String> mealRecordIds = new ArrayList<>();

                    for (MealRecord mealRecord : mealRecords) {
                        String mealRecordString = mealRecord.getMealName() + " - " + mealRecord.getMealType() + " - " +
                                mealRecord.getCalories() + " Cal";
                        mealRecordStrings.add(mealRecordString);
                        mealRecordIds.add(mealRecord.getMealRecordID());
                    }

                    Log.d("MealLogFragment", "Size of mealRecordStrings: " + mealRecordStrings.size());


                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, mealRecordStrings);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mealRecordSpinner.setAdapter(adapter);
                    mealRecordSpinner.setTag(mealRecordIds);
                } else {
                    Log.w("MealLogFragment", "No meal records found for the selected date.");
                    mealRecordSpinner.setAdapter(null); // Clear the spinner if no records are found
                }
            }
        });
    }

    private void filterDiaryEntriesByDate(String selectedDate) {
        userDiaryController.fetchDiaryEntries(username, new UserDiary.OnDiaryEntriesFetchedListener() {
            @Override
            public void onDiaryEntriesFetched(List<UserDiary> diaryEntries) {
                diaryEntriesContainer.removeAllViews();
                List<UserDiary> filteredEntries = new ArrayList<>();
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                Log.d("FilterDebug", "Selected Date: " + selectedDate);
                Log.d("FilterDebug", "Fetched diary entries count: " + diaryEntries.size());
                for (UserDiary entry : diaryEntries) {
                    if (entry.getEntryDateTime() != null) {
                        String entryDate = dateFormat.format(entry.getEntryDateTime());
                        Log.d("FilterDebug", "Entry Date: " + entryDate);
                        if (entryDate.equals(selectedDate)) {
                            Log.d("FilterDebug", "Date matched: " + entryDate);
                            filteredEntries.add(entry);
                        } else {
                            Log.d("FilterDebug", "Date did not match: " + entryDate + " != " + selectedDate);
                        }
                    }
                }

                if (filteredEntries.isEmpty()) {
                    Log.d("FilterDebug", "filteredEntries.isEmpty()");
                    diaryEntriesContainer.removeAllViews();
                    TextView noEntriesTextView = new TextView(getContext()); // 'context' should be your activity or fragment context
                    noEntriesTextView.setText("No entries found for the selected date. Click 'Clear Filter' to view all.");
                    noEntriesTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    diaryEntriesContainer.addView(noEntriesTextView);
                } else {
                    Log.d("FilterDebug", "else");
                    for (UserDiary entry : filteredEntries) {
                        Log.d("FilterDebug", " for (UserDiary entry : filteredEntries)");
                        addDiaryEntryView(entry);
                    }
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

        moreOptionsIcon = entryView.findViewById(R.id.moreOptionsIcon);

        String mealRecordString = entry.getMealRecordString();
        titleTextView.setText(mealRecordString);

        descriptionTextView.setText(entry.getThoughts());

        String tags = entry.getTags();

        if (tags != null && !tags.isEmpty()) {
            tagsTextView.setText(tags.replace(",", ", "));
        }


        if (entry.getEntryDateTime() != null) {
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));

            // Format the date using the correct time zone
            String formattedDate = dateFormat.format(entry.getEntryDateTime());
            timestampTextView.setText(formattedDate);
        }

        moreOptionsIcon.setOnClickListener(v -> {
            // Create a PopupMenu
            PopupMenu popup = new PopupMenu(getContext(), moreOptionsIcon);
            // Inflate the popup menu from a menu resource
            popup.getMenuInflater().inflate(R.menu.user_diary_menu, popup.getMenu());

            // Set a click listener for menu item clicks
            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.edit_diary_entry) {
                    // Edit diary entry logic
                    String diaryID = entry.getDiaryID();
                    showEditDiaryEntryDialog(diaryID, entry, success -> {
                        if (success) {
                            Toast.makeText(getContext(), "Entry updated successfully", Toast.LENGTH_SHORT).show();
                            fetchDiaryEntries();
                        } else {
                            Toast.makeText(getContext(), "Failed to update entry", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return true;
                } else if (itemId == R.id.delete_diary_entry) {
                    // Delete diary entry logic
                    String diaryID = entry.getDiaryID();
                    confirmDeleteDiaryEntry(diaryID, success -> {
                        if (success) {
                            Toast.makeText(getContext(), "Entry deleted successfully", Toast.LENGTH_SHORT).show();
                            fetchDiaryEntries();
                        } else {
                            Toast.makeText(getContext(), "Failed to delete entry", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return true;
                }
                return false;
            });

            // Show the popup menu
            popup.show();
        });

        diaryEntriesContainer.addView(entryView);
    }

    private String formatDate(int day, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return dateFormat.format(calendar.getTime());
    }

    private void showAddDiaryEntryDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_add_diary_entry, null);

        dateTextView = dialogView.findViewById(R.id.dateTextView);
        mealRecordSpinner = dialogView.findViewById(R.id.mealRecordSpinner);
        descriptionEditText = dialogView.findViewById(R.id.descriptionEditText);
        addTagsButton = dialogView.findViewById(R.id.addTagsButton);
        selectedTagsTextView = dialogView.findViewById(R.id.selectedTagsTextView);

        mealRecordSpinner.setEnabled(true);

        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        addTagsButton.setOnClickListener(v -> {
            showTagSelectionDialog(tags -> selectedTagsTextView.setText(tags));
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView)
                .setTitle("Add Diary Entry")
                .setPositiveButton("Save", (dialog, which) -> {

                    String selectedDate = dateTextView.getText().toString();
                    String description = descriptionEditText.getText().toString();
                    String selectedTags = selectedTagsTextView.getText().toString();
                    String mealRecordString = mealRecordSpinner.getSelectedItem().toString(); // Ensure it's a string

                    if (selectedDate.equals("Select Date")) {
                        Toast.makeText(getContext(), "Please select a date.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (description.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter a description.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<String> mealRecordIds = (List<String>) mealRecordSpinner.getTag();
                    int selectedIndex = mealRecordSpinner.getSelectedItemPosition();
                    String selectedMealRecordID = mealRecordIds != null && selectedIndex >= 0 ? mealRecordIds.get(selectedIndex) : null;

                    // Validate the selected mealRecordID
                    if (selectedMealRecordID == null) {
                        Toast.makeText(getContext(), "Invalid meal record selection.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String[] dateParts = selectedDate.split("-");
                    int year = Integer.parseInt(dateParts[0]);
                    int month = Integer.parseInt(dateParts[1]) - 1;
                    int day = Integer.parseInt(dateParts[2]);

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, day);
                    Timestamp entryDateTime = new Timestamp(calendar.getTimeInMillis());

                    userDiaryController.handleDiaryEntry(entryDateTime, selectedMealRecordID, description, selectedTags, username, mealRecordString);
                    fetchDiaryEntries();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();

        userDiaryController.fetchAllMealsLogged(username, dateTextView.getText().toString(), mealRecords -> {
            List<String> mealRecordStrings = new ArrayList<>();
            List<String> mealRecordIds = new ArrayList<>();

            for (MealRecord mealRecord : mealRecords) {
                String mealRecordString = mealRecord.getMealName() + " - " + mealRecord.getMealType() + " - " + mealRecord.getCalories() + " Cal";
                mealRecordStrings.add(mealRecordString);
                mealRecordIds.add(mealRecord.getMealRecordID()); // Store ID for retrieval on save
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, mealRecordStrings);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mealRecordSpinner.setAdapter(adapter);
            mealRecordSpinner.setTag(mealRecordIds); // Store the mealRecordIds in the spinner's tag
        });
    }

    private void searchshowDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
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
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, dayOfMonth);


                    String selectedDateStr = dateFormat.format(calendar.getTime());

                    // Set the formatted date to the TextView and call fetchMealRecordsForDate
                    dateTextView.setText(selectedDateStr);
                    fetchMealRecordsForDate(selectedDateStr, null); // Pass in correctly formatted date
                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
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

    private void confirmDeleteDiaryEntry(String diaryID, OnEntryDeletedListener listener) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Entry")
                .setMessage("Are you sure you want to delete this diary entry?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    userDiaryController.deleteDiaryEntry(diaryID, listener);
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    public interface OnEntryDeletedListener {
        void onEntryDeleted(boolean success);
    }

    public interface OnEntryUpdatedListener {
        void onEntryUpdated(boolean success);
    }

    private void showEditDiaryEntryDialog(String diaryID, UserDiary entry, OnEntryUpdatedListener listener) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_add_diary_entry, null);

        // Initialize views
        dateTextView = dialogView.findViewById(R.id.dateTextView);
        mealRecordSpinner = dialogView.findViewById(R.id.mealRecordSpinner);
        descriptionEditText = dialogView.findViewById(R.id.descriptionEditText);
        addTagsButton = dialogView.findViewById(R.id.addTagsButton);
        selectedTagsTextView = dialogView.findViewById(R.id.selectedTagsTextView);

        mealRecordSpinner.setEnabled(true);

        // Set existing data in the dialog
        if (entry.getEntryDateTime() != null) {
            String formattedDate = dateFormat.format(entry.getEntryDateTime());
            dateTextView.setText(formattedDate);
        }

        List<String> mealRecordList = new ArrayList<>();
        mealRecordList.add(entry.getMealRecordString()); // Add only the selected meal record

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, mealRecordList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealRecordSpinner.setAdapter(adapter);
        mealRecordSpinner.setSelection(0);

        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        descriptionEditText.setText(entry.getThoughts());
        selectedTagsTextView.setText(entry.getTags());

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView)
                .setTitle("Edit Diary Entry")
                .setPositiveButton("Save", (dialog, which) -> {
                    String selectedDate = dateTextView.getText().toString();
                    String description = descriptionEditText.getText().toString();
                    String selectedTags = selectedTagsTextView.getText().toString();
                    String mealRecordString = (String) mealRecordSpinner.getSelectedItem();  // Get the selected text

                    if (selectedDate.equals("Select Date") || selectedDate.isEmpty()) {
                        Toast.makeText(getContext(), "Please select a valid date.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (description.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter a description.", Toast.LENGTH_SHORT).show();
                        return; // Prevent further processing if invalid
                    }

                    // Parse the date
                    String[] dateParts = selectedDate.split("/");
                    if (dateParts.length != 3) {
                        Toast.makeText(getContext(), "Invalid date format.", Toast.LENGTH_SHORT).show();
                        return; // Exit if format is incorrect
                    }

                    try {
                        int day = Integer.parseInt(dateParts[0]);
                        int month = Integer.parseInt(dateParts[1]) - 1; // Month is zero-based
                        int year = Integer.parseInt(dateParts[2]);

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day);
                        Timestamp entryDateTime = new Timestamp(calendar.getTimeInMillis());

                        List<String> mealRecordIds = (List<String>) mealRecordSpinner.getTag();
                        int selectedIndex = mealRecordSpinner.getSelectedItemPosition();
                        String selectedMealRecordID = mealRecordIds != null && selectedIndex >= 0 ? mealRecordIds.get(selectedIndex) : null;

                        entry.setEntryDateTime(entryDateTime);
                        entry.setMealRecordID(selectedMealRecordID);
                        entry.setThoughts(description);
                        entry.setTags(selectedTags);
                        Log.d("Debug", "Username: " + username);
                        entry.setUsername(username);
                        entry.setMealRecordString(mealRecordString);

                        // Notify the controller to update the entry in the database
                        userDiaryController.updateDiaryEntry(diaryID, entry, success -> {
                            if (listener != null) {
                                listener.onEntryUpdated(success); // Notify listener about the update
                            }
                        });
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Error parsing date.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show(); // Show the dialog
    }

    public interface TagSelectionListener {
        void onTagsSelected(String selectedTags);
    }

    private void clearFilter() {
        fetchDiaryEntries();
        etDatePicker.setText("");
    }


}