package com.example.dietandnutritionapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BMIPastRecordFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private BMIRecordAdapter adapter;
    private List<BMIDetail> bmiList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bmi_report, container, false);

        // Hide buttons
        if (getActivity() != null) {
            getActivity().findViewById(R.id.calculate_button).setVisibility(View.GONE);
            getActivity().findViewById(R.id.PastRecord_button).setVisibility(View.GONE);
            getActivity().findViewById(R.id.advice_column).setVisibility(View.GONE);
        }

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.bmiRecord_recycler_view);
        bmiList = new ArrayList<>();
        adapter = new BMIRecordAdapter(bmiList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        fetchPastBMIRecords();

        return view;
    }

    private void fetchPastBMIRecords() {
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (userId == null) {
            Toast.makeText(getActivity(), "User is not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("BMIPastRecordFragment", "Fetching records for user ID: " + userId);

        db.collection("BMIRecord")
                .whereEqualTo("user_id", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("BMIPastRecordFragment", "Error fetching records", error);
                        Toast.makeText(getActivity(), "Error fetching records: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        bmiList.clear();
                        for (DocumentSnapshot document : value.getDocuments()) {
                            if (document.exists()) {
                                Log.d("BMIPastRecordFragment", "Fetched document: " + document.getData());

                                Double bmiValue = document.getDouble("bmi");
                                Long timestampValue = document.getLong("timestamp");

                                if (bmiValue != null && timestampValue != null) {
                                    bmiList.add(new BMIDetail(bmiValue, timestampValue, document.getId()));
                                } else {
                                    Log.w("BMIPastRecordFragment", "BMI or timestamp is null for document: " + document.getId());
                                }
                            } else {
                                Log.w("BMIPastRecordFragment", "Document does not exist: " + document.getId());
                            }
                        }
                        adapter.notifyDataSetChanged(); // Notify the adapter of the changes
                    } else {
                        Log.w("BMIPastRecordFragment", "No documents found for user: " + userId);
                    }
                });
    }


}
