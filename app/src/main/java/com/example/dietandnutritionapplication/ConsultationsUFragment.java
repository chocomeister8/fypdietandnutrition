package com.example.dietandnutritionapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;

public class ConsultationsUFragment extends Fragment {

    private ListView nutritionistListView;
    private List<Nutritionist> nutritionistList;
//    private UserConsultationsController adapter;
    private FirebaseFirestore db;
    ArrayList<Profile> nutriAccounts = new ArrayList<>();
    private ProfileAdapter adapter;
    private NutriProfileAdapter nutriProfileAdapter;
    private NutriAdapterTest nutriAdapterTest;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_consultations_u, container, false);

        // Initialize the ListView
        nutritionistListView = view.findViewById(R.id.nutritionist_list_view);

        // Initialize the list of nutritionists
        nutritionistList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();


        nutriProfileAdapter = new NutriProfileAdapter(getContext(),nutriAccounts);
        nutritionistListView.setAdapter(nutriProfileAdapter);

        ConsultationController consultationController = new ConsultationController();
        consultationController.retrieveNutri(new UserAccountEntity.DataCallback() {
            @Override
            public void onSuccess(ArrayList<Profile> accounts) {
                nutriAccounts.clear(); // Clear the current list
                nutriAccounts.addAll(accounts); // Add the fetched accounts

                nutriProfileAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed to load accounts.", Toast.LENGTH_SHORT).show();
            }
        });
        // Fetch nutritionists from Firestore
//        loadNutritionistsFromFirestore();

//        Bitmap janeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profile);
//        Bitmap johnBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profile);
//        // Add dummy data (replace with real data source)

        // Handle item clicks
//        nutritionistListView.setOnItemClickListener((parent, view1, position, id) -> {
//            Nutritionist selectedNutritionist = nutritionistList.get(position);
//            // Create an instance of NutriViewProfileFragment
//            NutriViewProfileFragment profileFragment = new NutriViewProfileFragment();
//
//            // Create a bundle to pass the email
//            Bundle args = new Bundle();
//            args.putString("email", selectedNutritionist.getEmail());
//            profileFragment.setArguments(args);
//
//            // Replace the current fragment with NutriViewProfileFragment
//            requireActivity().getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.frame_layout, profileFragment)
//                    .addToBackStack(null)
//                    .commit();
//        });

        return view;
    }

}
