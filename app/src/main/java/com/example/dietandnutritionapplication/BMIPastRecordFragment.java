package com.example.dietandnutritionapplication;

import android.os.Bundle;
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
import com.google.firebase.firestore.FirebaseFirestore;
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

    // Constructor to accept local BMI list
    public BMIPastRecordFragment(List<BMIDetail> localBmiList) {
        this.bmiList = localBmiList != null ? localBmiList : new ArrayList<>();
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bmi_report, container, false);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.bmiRecord_recycler_view);
        adapter = new BMIRecordAdapter(bmiList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Fetch BMI records from Firestore
        fetchBMIRecords();

        // Hide buttons
        if (getActivity() != null) {
            getActivity().findViewById(R.id.calculate_button).setVisibility(View.GONE);
            getActivity().findViewById(R.id.PastRecord_button).setVisibility(View.GONE);
            getActivity().findViewById(R.id.advice_column).setVisibility(View.GONE);
        }

        return view;
    }

    private void fetchBMIRecords() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("BMIRecord")
                .whereEqualTo("user_id", userId) // Fetch records for the current user
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        bmiList.clear(); // Clear existing records
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            double bmi = document.getDouble("bmi");
                            long timestamp = document.getLong("timestamp");
                            // Add to the local list
                            bmiList.add(new BMIDetail(bmi, timestamp, userId));
                        }
                        adapter.notifyDataSetChanged(); // Notify the adapter to refresh the view
                    } else {
                        Toast.makeText(getContext(), "Error fetching records: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
