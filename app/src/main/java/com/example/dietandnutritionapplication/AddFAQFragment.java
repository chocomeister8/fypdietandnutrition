package com.example.dietandnutritionapplication;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddFAQFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFAQFragment extends Fragment {

    private EditText titleEditText, questionEditText, answerEditText;
    private Button addFAQ;
    ProgressDialog pd;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddFAQFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddFAQFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFAQFragment newInstance(String param1, String param2) {
        AddFAQFragment fragment = new AddFAQFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.addfaqpage, container, false);
        MainActivity mainActivity = (MainActivity) getActivity();
        titleEditText = view.findViewById(R.id.title);
        questionEditText = view.findViewById(R.id.question);
        answerEditText = view.findViewById(R.id.answer);
        addFAQ = view.findViewById(R.id.addFAQ);

        pd = new ProgressDialog(getActivity());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        addFAQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = titleEditText.getText().toString();
                String question = questionEditText.getText().toString();
                String answer = answerEditText.getText().toString();

                ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Singapore"));
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                String date = now.format(formatter);

//                insertFAQ(title, question, answer, date);
                AddFAQController addFAQController = new AddFAQController();
                addFAQController.checkFAQ(title,question,answer,date,pd,getActivity());
                redirectToViewAllFAQs();
            }

        });
        return view;
    }

    private void redirectToViewAllFAQs() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, new FAQFragment()); // Use your container ID
        fragmentTransaction.addToBackStack(null); // Optional: Add to back stack
        fragmentTransaction.commit();
    }
}