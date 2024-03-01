package com.appname.ebaysearch.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appname.ebaysearch.API.APICalls;
import com.appname.ebaysearch.Adapters.EbayItemAdapter;
import com.appname.ebaysearch.API.InterfaceAPI;
import com.appname.ebaysearch.Items.EbayItem;
import com.appname.ebaysearch.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {

    private JSONArray conditionsJsonArray,shippingJsonArray;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private EbayItemAdapter adapter;
    private TextView textViewLoading,textViewNoResult;
    private ImageView back_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Intent intent = getIntent();
        String keyword = intent.getStringExtra("keyword");
        String selectedCategory = intent.getStringExtra("category");
        String distance = intent.getStringExtra("distance");
        String zipcode = intent.getStringExtra("zipcode");

        // You'll need to parse the JSON from the string extra
        String conditionsString = intent.getStringExtra("conditions");
        String shippingString = intent.getStringExtra("shipping");

        try {
            conditionsJsonArray = new JSONArray(conditionsString);
            shippingJsonArray = new JSONArray(shippingString);

            // Now you can use these JSONArrays as needed
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("Values", keyword);
        Log.d("Values", selectedCategory);
        Log.d("Values", String.valueOf(conditionsJsonArray));
        Log.d("Values", String.valueOf(shippingJsonArray));
        Log.d("Values", distance);
        Log.d("Values", zipcode);


        recyclerView = findViewById(R.id.recycler_view_results);
        progressBar = findViewById(R.id.progressBar);
        textViewLoading = findViewById(R.id.textViewLoading);
        back_button = findViewById(R.id.back_button);
        textViewNoResult = findViewById(R.id.textViewNoResult);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Set up the RecyclerView with the adapter
        adapter = new EbayItemAdapter(new ArrayList<>());
        int numberOfColumns = 2;// Start with an empty list
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));;

        progressBar.setVisibility(View.VISIBLE);
        textViewLoading.setVisibility(View.VISIBLE);

        // Use the retrieved data as needed in your new activity
        APICalls.getInstance(this).getBusiness(keyword, selectedCategory, conditionsJsonArray, shippingJsonArray, distance, zipcode, new InterfaceAPI() {
            @Override
            public void onSuccess(JSONObject response) {

                progressBar.setVisibility(View.GONE);
                textViewLoading.setVisibility(View.GONE);

                Log.d("Values3", response.toString());
                ArrayList<EbayItem> data = parseItems(response);
                adapter.setData(data);
                Log.d("Values2",data.toString());
                if(data.toString() == "[]"){
                    textViewNoResult.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSuccess(JSONArray response) {

            }

            @Override
            public void onError(Exception error) {
                Log.d("Values",error.getMessage());
            }
        });

    }
    private ArrayList<EbayItem> parseItems(JSONObject response) {
        // Initialize the ArrayList to hold EbayItem objects
        ArrayList<EbayItem> items = new ArrayList<>();


        // Parse the JSON response and create EbayItem objects
        try {
            // Assuming the JSON response is an array of items
            JSONArray itemsArray = response.getJSONArray("findItemsAdvancedResponse")
                    .getJSONObject(0)
                    .getJSONArray("searchResult")
                    .getJSONObject(0)
                    .getJSONArray("item");


            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject itemObj = itemsArray.getJSONObject(i);
                EbayItem item = new EbayItem();

                // Extract the string from the JSON array
                JSONArray id = itemObj.getJSONArray("itemId");
                String title = itemObj.getJSONArray("title").getString(0);
                JSONArray galleryURL = itemObj.getJSONArray("galleryURL");
                String viewItemURL = itemObj.getJSONArray("viewItemURL").getString(0);

                // Extract price, which is nested within a JSON object and then within an array
                JSONObject sellingStatus = itemObj.getJSONArray("sellingStatus").getJSONObject(0);
                String price = sellingStatus.getJSONArray("currentPrice").getJSONObject(0).getString("__value__");

                String itemShipping = itemObj.getJSONArray("shippingInfo").getJSONObject(0).getJSONArray("shippingServiceCost").getJSONObject(0).getString("__value__");

                String zipCode = itemObj.getJSONArray("postalCode").getString(0);

                String condition = itemObj.getJSONArray("condition").getJSONObject(0).getJSONArray("conditionDisplayName").getString(0);
                // Set the extracted values to the item object
                item.setId(jsonArrayToList(id));
                item.setTitle(title);
                item.setImage(jsonArrayToList(galleryURL));
                item.setItemUrl(viewItemURL);
                item.setPrice(price);
                if(itemShipping.contains("0.0")){
                    item.setItemShipping("Free");
                }
                else {
                    item.setItemShipping(itemShipping);
                }
                item.setCondition(condition);
                item.setZipCode(zipCode);

                items.add(item);
            }
        } catch (JSONException e) {
            // Log the exception
            Log.e("Items", "Error parsing JSON", e);
        }
        // Return the list of items
        return items;
    }

    private List<String> jsonArrayToList(JSONArray jsonArray) throws JSONException {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonArray.getString(i));
        }
        return list;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            // Extract the updated cart data from the Intent
            // Update your UI or data set accordingly
            String id = data.getStringExtra("id");
            boolean updatedCartData = data.getBooleanExtra("updated_cart_data",false);
            if (adapter != null) {
                adapter.updateWishlistState(id,updatedCartData);
            }
        }
    }

}