package com.appname.ebaysearch.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appname.ebaysearch.API.APICalls;
import com.appname.ebaysearch.API.InterfaceAPI;
import com.appname.ebaysearch.Adapters.PhotosAdapter;
import com.appname.ebaysearch.Items.PhotoItem;
import com.appname.ebaysearch.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PhotosFragment extends Fragment {

    private ArrayList<String> images;
    private RecyclerView recyclerView;
    private PhotosAdapter adapter;
    private String title;
    private LinearLayout photos_progress;

    public static PhotosFragment newInstance(String title) {
        PhotosFragment fragment = new PhotosFragment();
        Bundle args = new Bundle();
        args.putString("title",title);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos_details, container, false);

        title = getArguments().getString("title");
        recyclerView = view.findViewById(R.id.recycler_view);
        photos_progress = view.findViewById(R.id.photos_progress);

        photos_progress.setVisibility(View.VISIBLE);
        getPhotos(title);



        return view;
    }
    private void getPhotos(String title){

        APICalls.getInstance(getContext()).getphotos(title, new InterfaceAPI() {
            @Override
            public void onSuccess(JSONObject response) {


                parsePhotoItems(response);
            }

            @Override
            public void onSuccess(JSONArray response) {

            }

            @Override
            public void onError(Exception error) {

            }
        });
    }
    private void parsePhotoItems(JSONObject response) {
        images = new ArrayList<>();

        // Check if the response contains "items"
        if (response.has("items")) {
            JSONArray items = null;
            try {
                items = response.getJSONArray("items");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            // Iterate through each item in the array
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = null;
                try {
                    item = items.getJSONObject(i);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                // Extract the link and add it to the list
                String link = item.optString("link", null);
                if (link != null) {
                    images.add(link);
                }
            }
        }

         // assuming you have a RecyclerView in your fragment_photos_details.xml
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        photos_progress.setVisibility(View.GONE);
        // Set up the adapter
        adapter = new PhotosAdapter(images);
        recyclerView.setAdapter(adapter);

    }
}
