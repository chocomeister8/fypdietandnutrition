package com.example.dietandnutritionapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;



public class FAQFragment extends Fragment{
    ListView FAQListView;
    ArrayList<String> items;
    FAQAdapter adapter;
    ArrayList<FAQ> faqs = new ArrayList<>();

    private Spinner filterFAQspinner;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FAQFragment() {
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
    public static FAQFragment newInstance(String param1, String param2) {
        FAQFragment fragment = new FAQFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        items = new ArrayList<>();
        MainActivity mainActivity = (MainActivity) getActivity();
        faqs = mainActivity.getFAQArray();
        for(FAQ faq:faqs){
            displayFAQ faq1= new displayFAQ(faq.getTitle(), faq.getQuestion(), faq.getAnswer(), faq.getDateCreated());
            items.add(faq1.toString());
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.viewallfaqs, container, false);
        FAQListView = view.findViewById(R.id.faqListView);


        // Set the adapter to the ListView
        //FAQAdapter adapter = new FAQAdapter(getContext(), faqs);
        adapter = new FAQAdapter(getContext(), faqs);
        FAQListView.setAdapter(adapter);


        FAQListView = view.findViewById(R.id.faqListView);
        filterFAQspinner = view.findViewById(R.id.filterFAQSpinner);

        //filterFAQspinner = view.findViewById(R.id.filterFAQSpinner);
        List<String> sortFAQ = new ArrayList<>();
        sortFAQ.add("Latest to oldest");
        sortFAQ.add("Oldest to latest");
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, sortFAQ);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterFAQspinner.setAdapter(sortAdapter);

        filterFAQspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Determine whether to sort latest to oldest or oldest to latest
                boolean latestToOldest = position == 0; // Assuming position 0 is "Latest to oldest"
                sortFAQByDate(latestToOldest);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing if no item is selected
            }
        });

        return view;
    }
    private void sortFAQByDate(final boolean latestToOldest) {
        faqs.sort(new Comparator<FAQ>() {
            @Override
            public int compare(FAQ r1, FAQ r2) {
                // Sort based on the latestToOldest flag
                return latestToOldest
                        ? r2.getDateCreated().compareTo(r1.getDateCreated())
                        : r1.getDateCreated().compareTo(r2.getDateCreated());
            }
        });
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
