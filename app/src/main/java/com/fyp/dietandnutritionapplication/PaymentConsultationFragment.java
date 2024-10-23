package com.fyp.dietandnutritionapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PaymentConsultationFragment extends Fragment {

    private static final String ARG_CONSULTATION_ID = "consultationId";

    public static PaymentConsultationFragment newInstance(String consultationId) {
        PaymentConsultationFragment fragment = new PaymentConsultationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CONSULTATION_ID, consultationId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.payment_consultation, container, false);
    }
}
