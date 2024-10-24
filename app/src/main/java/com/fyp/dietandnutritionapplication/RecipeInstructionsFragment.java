package com.fyp.dietandnutritionapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewFragment;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class RecipeInstructionsFragment extends Fragment {

    private static final String ARG_URL = "url";
    private String instructionsUrl;

    public static RecipeInstructionsFragment newInstance(String url) {
        RecipeInstructionsFragment fragment = new RecipeInstructionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            instructionsUrl = getArguments().getString(ARG_URL);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_instructions, container, false);
        WebView webView = view.findViewById(R.id.web_view);
        Button backButton = view.findViewById(R.id.back_button);

        if (getArguments() != null) {
            String url = getArguments().getString(ARG_URL);
            webView.setWebViewClient(new WebViewClient()); // Ensures links open within the WebView
            webView.loadUrl(url); // Load the instructions URL
        }

        view.findViewById(R.id.back_button).setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack(); // Go back to the previous fragment
        });

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Load the URL
        if (instructionsUrl != null) {
            webView.loadUrl(instructionsUrl);
        } else {
            Toast.makeText(getActivity(), "No URL provided", Toast.LENGTH_SHORT).show();
        }

        return view;
    }
}
