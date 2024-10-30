package com.fyp.dietandnutritionapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;




public class AllergyOptionsFragment extends Fragment{
    ListView allergyOptionsListView;
    ArrayList<String> items;
    AllergyOptionsAdapter adapter;
    ArrayList<AllergyOptions> allergyoptions = new ArrayList<>();
    ArrayList<AllergyOptions> originalallergyOptions = new ArrayList<>(); // Keep the unfiltered original list
    private EditText allergyOptionsEditText;

    private String savedSearchText = "";
    private int savedFilterPosition = 0;
    private int savedScrollPosition = 0;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AllergyOptionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment viewAccountsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AllergyOptionsFragment newInstance(String param1, String param2) {
        AllergyOptionsFragment fragment = new AllergyOptionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.viewallallergyoptions, container, false);
        allergyOptionsListView = view.findViewById(R.id.allergyOptionListView);
        allergyOptionsEditText = view.findViewById(R.id.allergyOptionEditText);

        // Set the adapter to the ListView
        //FAQAdapter adapter = new FAQAdapter(getContext(), faqs);
        adapter = new AllergyOptionsAdapter(getContext(), allergyoptions);
        allergyOptionsListView.setAdapter(adapter);

        allergyOptionsEditText.setText(savedSearchText);

        // Scroll to the saved scroll position
        allergyOptionsListView.post(() -> allergyOptionsListView.setSelection(savedScrollPosition));

        AllergyOptionsController allergyOptionsController= new AllergyOptionsController(); // Ensure you have a member variable for ViewFAQController
        allergyOptionsController.getAllAllergyOptions(new AllergyOptionsEntity.DataCallback() {
            @Override
            public void onSuccess(ArrayList<AllergyOptions> allergyOptions) {
                originalallergyOptions.clear(); // Clear the original profiles list
                originalallergyOptions.addAll(allergyOptions); // Store fetched profiles in the original list
                allergyoptions.clear(); // Clear the filtering list
                allergyoptions.addAll(allergyOptions); // Store profiles for filtering

                adapter.notifyDataSetChanged(); // Notify adapter of data changes
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to load Allergy Options.", Toast.LENGTH_SHORT).show();
            }
        });

        allergyOptionsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        allergyOptionsListView.setOnItemClickListener((parent, view1, position, id) -> {

            savedSearchText = allergyOptionsEditText.getText().toString();
            savedScrollPosition = allergyOptionsListView.getFirstVisiblePosition();

        });

        allergyOptionsListView.setOnItemLongClickListener((parent, view1, position, id) -> {
            // Retrieve the specialization to delete
            AllergyOptions selectedAllergyOption = allergyoptions.get(position);

            // Show a confirmation dialog
            new AlertDialog.Builder(getContext())
                    .setTitle("Delete Allergy Option")
                    .setMessage("Are you sure you want to delete this allergy option?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Call the delete function if the user confirms
                        deleteAllergyOptionFromDatabase(selectedAllergyOption);

                        // Remove the specialization from the list and notify the adapter
                        allergyoptions.remove(position);
                        adapter.notifyDataSetChanged();
                    })
                    .setNegativeButton("No", null)
                    .show();

            return true;
        });

        return view;
    }

    private void deleteAllergyOptionFromDatabase(AllergyOptions allergyOptions) {
        ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Deleting specialization...");
        pd.show();

        // Get the ID of the specialization to delete
        String allergyOptionId = allergyOptions.getAllergyOptionId();

        AllergyOptionsController controller = new AllergyOptionsController();
        controller.deleteAllergyOption(allergyOptionId, new AllergyOptionsEntity.DeleteCallback() {
            @Override
            public void onSuccess() {
                pd.dismiss();
                Toast.makeText(getContext(), "Allergy Option deleted successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                pd.dismiss();
                Toast.makeText(getContext(), "Failed to delete allergy option: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
