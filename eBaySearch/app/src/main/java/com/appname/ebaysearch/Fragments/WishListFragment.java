package com.appname.ebaysearch.Fragments;
import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appname.ebaysearch.API.APICalls;
import com.appname.ebaysearch.API.InterfaceAPI;
import com.appname.ebaysearch.Adapters.WishListItemAdapter;
import com.appname.ebaysearch.Items.EbayItem;
import com.appname.ebaysearch.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WishListFragment extends Fragment {

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private WishListItemAdapter adapter;
    private TextView totalTextView,number_item;
    private LinearLayout no_result,main_page;

    public WishListFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_wishlist, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_wishlist);
        progressBar = view.findViewById(R.id.progressBar);
        totalTextView = view.findViewById(R.id.total_price_text);
        number_item = view.findViewById(R.id.number_item);
        no_result = view.findViewById(R.id.no_result);
        main_page = view.findViewById(R.id.main_page);

        adapter = new WishListItemAdapter(new ArrayList<>(), this);
        int numberOfColumns = 2;// Start with an empty list
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), numberOfColumns));;

        progressBar.setVisibility(View.VISIBLE);

        APICalls.getInstance(view.getContext()).getWishList(new InterfaceAPI() {
            @Override
            public void onSuccess(JSONObject response) {

            }

            @Override
            public void onSuccess(JSONArray response) {

                progressBar.setVisibility(View.GONE);

                Log.d("WishList","Finding");
                Log.d("WishList", response.toString());
                ArrayList<EbayItem> data = parseItems(response);
                adapter.setData(data);
                Log.d("Values",data.toString());
            }

            @Override
            public void onError(Exception error) {
                Log.d("Values",error.getMessage());
            }
        });

        return view;

    }
    @Override
    public void onResume() {
        super.onResume();
        refreshWishlistData(); // Refresh wishlist data whenever the fragment resumes
    }
    private void refreshWishlistData() {
        progressBar.setVisibility(View.VISIBLE);
        APICalls.getInstance(getContext()).getWishList(new InterfaceAPI() {
            @Override
            public void onSuccess(JSONObject response) {
                // Handle JSONObject response
            }

            @Override
            public void onSuccess(JSONArray response) {
                progressBar.setVisibility(View.GONE);
                ArrayList<EbayItem> data = parseItems(response);
                adapter.setData(data);
                checkAndUpdateUIForEmptyList();
//                if (response.length() == 0) {
//                    // If no data, show error message and hide the RecyclerView
//                    no_result.setVisibility(View.VISIBLE);
//                    main_page.setVisibility(View.GONE);
//                } else {
//                    // If data exists, update the adapter and show the RecyclerView
//                    ArrayList<EbayItem> data = parseItems(response);
//                    adapter.setData(data);
//                    no_result.setVisibility(View.GONE);
//                    main_page.setVisibility(View.VISIBLE); // Update total price and number of items
//                }
            }

            @Override
            public void onError(Exception error) {
                // Handle error
            }
        });
    }
    private ArrayList<EbayItem> parseItems(JSONArray jsonArray) {
        ArrayList<EbayItem> items = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                EbayItem item = new EbayItem();

                // Set the itemId, which appears to be a single String inside a List
                List<String> idList = new ArrayList<>();
                idList.add(jsonObject.getString("itemId"));
                item.setId(idList);

                // Set the image, which also seems to be a single String inside a List
                List<String> imageList = new ArrayList<>();
                imageList.add(jsonObject.getString("itemImage"));
                item.setImage(imageList);

                // Set other attributes of EbayItem
                item.setTitle(jsonObject.getString("itemTitle"));
                item.setPrice(jsonObject.getString("itemPrice"));
                item.setItemUrl(jsonObject.getString("shippingCost")); // Assuming this is the item URL
                item.setItemShipping(jsonObject.getString("itemShipping"));
                item.setZipCode(jsonObject.getString("shippingLocation"));
                item.setCondition(jsonObject.getString("HandlingTime")); // Assuming this is the condition

                // Add the EbayItem to the list
                items.add(item);
                Log.d("wishlist", item.getTitle());
                Log.d("wishlist", item.getPrice());
                Log.d("wishlist", item.getItemShipping());
                Log.d("wishlist", item.getItemUrl());
                Log.d("wishlist", item.getImage().toString());
                Log.d("wishlist", item.getCondition());
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions
        }
        return items;
    }
    public void updateTotalPrice(double total,int items) {
        totalTextView.setText("$" + String.format("%.2f", total));
        number_item.setText("Wishlist Total (" + items + " items)");
    }

    public void checkAndUpdateUIForEmptyList() {
        if (adapter.getItemCount() == 0) {
            // If the wishlist is empty, show the 'no_result' layout
            no_result.setVisibility(View.VISIBLE);
            main_page.setVisibility(View.GONE);
        } else {
            // If the wishlist is not empty, show the 'main_page' layout
            no_result.setVisibility(View.GONE);
            main_page.setVisibility(View.VISIBLE);
        }
    }


}
