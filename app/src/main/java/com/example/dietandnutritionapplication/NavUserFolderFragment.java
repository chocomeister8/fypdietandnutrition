package com.example.dietandnutritionapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class NavUserFolderFragment extends Fragment {

    private String folderName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_folder, container, false);

        // Retrieve folder name from arguments
        if (getArguments() != null) {
            folderName = getArguments().getString("folder_name");
        }

        // Display the folder name or content based on the folder
        TextView folderTitle = ((View) view).findViewById(R.id.folder_title);
        folderTitle.setText(folderName);

        // TODO: Load the relevant content for the folder based on folderName

        return view;
    }
}
