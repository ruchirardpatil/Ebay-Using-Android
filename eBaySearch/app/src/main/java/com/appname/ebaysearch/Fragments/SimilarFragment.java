package com.appname.ebaysearch.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appname.ebaysearch.API.APICalls;
import com.appname.ebaysearch.API.InterfaceAPI;
import com.appname.ebaysearch.Adapters.SimilarItemsAdapter;
import com.appname.ebaysearch.Items.SimilarItem;
import com.appname.ebaysearch.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

// In SimilarFragment.java

public class SimilarFragment extends Fragment {

    private ArrayList<SimilarItem> similarItems,similarItemsOriginal;

    private String id;
    private RecyclerView recyclerView;
    private LinearLayout similar_progress;
    private Spinner spinnerSort, spinnerOrder;
    private SimilarItemsAdapter adapter;


    public static SimilarFragment newInstance(String id) {
        SimilarFragment fragment = new SimilarFragment();
        Bundle args = new Bundle();
        args.putSerializable("id", id);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_similar, container, false);

        id = getArguments().getString("id");


        recyclerView = view.findViewById(R.id.recyclerViewSimilarItems);
        similar_progress = view.findViewById(R.id.similar_progress);
        spinnerSort = view.findViewById(R.id.spinner_sort);
        spinnerOrder = view.findViewById(R.id.spinner_order);

        similar_progress.setVisibility(View.VISIBLE);

        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sort_options, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(sortAdapter);

        // Set up the adapter for spinnerOrder
        ArrayAdapter<CharSequence> orderAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.order_options, android.R.layout.simple_spinner_item);
        orderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrder.setAdapter(orderAdapter);

        if ("Default".equals(spinnerSort.getSelectedItem().toString())) {
            spinnerOrder.setEnabled(false);
        }

        getSimilarItems(id);



        return view;
    }
    private void getSimilarItems(String id){

        APICalls.getInstance(getContext()).getsimilaritems(id, new InterfaceAPI(){

            @Override
            public void onSuccess(JSONObject response) {

                similar_progress.setVisibility(View.GONE);
                parseSimilarItems(response);

            }

            @Override
            public void onSuccess(JSONArray response) {

            }

            @Override
            public void onError(Exception error) {

            }

        });
    }
    private void parseSimilarItems(JSONObject response) {
        similarItems = new ArrayList<>();

        // Check if response contains the required key
        if (response.has("getSimilarItemsResponse")) {
            JSONObject similarItemsResponse = null;
            try {
                similarItemsResponse = response.getJSONObject("getSimilarItemsResponse");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            if (similarItemsResponse.has("itemRecommendations")) {
                JSONObject itemRecommendations = null;
                try {
                    itemRecommendations = similarItemsResponse.getJSONObject("itemRecommendations");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                if (itemRecommendations.has("item")) {
                    JSONArray items = null;
                    try {
                        items = itemRecommendations.getJSONArray("item");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = null;
                        try {
                            item = items.getJSONObject(i);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        SimilarItem similarDetails = new SimilarItem();

                        // Extracting the required fields
                        similarDetails.setImage(item.optString("imageURL", ""));
                        similarDetails.setTitle(item.optString("title", ""));

                        // Extracting shipping cost
                        JSONObject shippingCostObj = item.optJSONObject("shippingCost");
                        if (shippingCostObj != null) {
                            similarDetails.setShippingCost(shippingCostObj.optString("__value__"));
                        } else {
                            similarDetails.setShippingCost("0.00");
                        }

                        // Extracting price
                        JSONObject currentPriceObj = item.optJSONObject("buyItNowPrice");
                        if (currentPriceObj != null) {
                            try {
                                similarDetails.setPrice(currentPriceObj.getString("__value__"));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            similarDetails.setPrice("0.00");
                        }

                        // Extracting days left
                        String timeLeft = item.optString("timeLeft", "");
                        similarDetails.setDaysLeft(parseTimeLeft(timeLeft));

                        similarItems.add(similarDetails);
                    }
                }
            }
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new SimilarItemsAdapter(similarItems, getContext());
        recyclerView.setAdapter(adapter);
        similarItemsOriginal = new ArrayList<>(similarItems);

        setupSpinnerListeners();

    }

private String parseTimeLeft(String timeLeft) {
    // Parse the timeLeft to return the number of days
    if (timeLeft.isEmpty()) return "0";  // Return "0" which can be parsed as 0 days

    try {
        int daysIndex = timeLeft.indexOf("D");
        if (daysIndex != -1) {
            return timeLeft.substring(1, daysIndex);  // Extract the number of days as a string
        } else {
            return "0";  // Less than a day is considered as 0 days
        }
    } catch (Exception e) {
        return "0";
    }
}

    private void setupSpinnerListeners() {

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSort = parent.getItemAtPosition(position).toString();
                if ("Default".equals(selectedSort)) {
                    spinnerOrder.setEnabled(false);
                    // If 'Descending' is selected, change to 'Ascending'
                    if (spinnerOrder.getSelectedItem().toString().equals("Descending")) {
                        spinnerOrder.setSelection(((ArrayAdapter)spinnerOrder.getAdapter()).getPosition("Ascending"));
                    }
                    restoreOriginalOrder();
                } else {
                    spinnerOrder.setEnabled(true);
                    sortList(selectedSort, spinnerOrder.getSelectedItem().toString());
                }
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinnerSort.getSelectedItem().toString().equals("Default")) {
                    restoreOriginalOrder();
                } else {
                    // Sort based on the selected parameter and order
                    sortList(spinnerSort.getSelectedItem().toString(), parent.getItemAtPosition(position).toString());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

private void sortList(String sortParameter, String sortOrder) {
    Comparator<SimilarItem> comparator = null;

    // Choose the appropriate comparator based on the sort parameter
    switch (sortParameter) {
        case "Name":
            comparator = Comparator.comparing(SimilarItem::getTitle);
            break;
        case "Price":
            comparator = Comparator.comparingDouble(item -> Double.parseDouble(item.getPrice()));
            break;
        case "Days":
            comparator = Comparator.comparingInt(item -> Integer.parseInt(item.getDaysLeft()));
            break;
    }

    // If a valid comparator is selected and sortOrder is 'Descending', then reverse it
    if (comparator != null && "Descending".equalsIgnoreCase(sortOrder)) {
        comparator = comparator.reversed();
    }

    // Perform the sort if a comparator is set
    if (comparator != null) {
        Collections.sort(similarItems, comparator);
    }

    // Notify the adapter of the data change if the adapter is not null
    if (adapter != null) {
        adapter.notifyDataSetChanged();
    }
}

    private void restoreOriginalOrder() {
        // Assuming 'similarItemsOriginal' is a copy of the original list before any sorting
        if (similarItemsOriginal != null) {
            similarItems.clear();
            similarItems.addAll(similarItemsOriginal);
        }

        // Notify the adapter of the data change if the adapter is not null
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

}

