package com.appname.ebaysearch.Fragments;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.appname.ebaysearch.API.APICalls;
import com.appname.ebaysearch.API.InterfaceAPI;
import com.appname.ebaysearch.Items.ProductItem;
import com.appname.ebaysearch.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductFragment extends Fragment {

    // Define UI components
    private LinearLayout llImagesContainer;
    private TextView tvProductTitle;
    private TextView tvPriceAndShipping;
    private TextView tvBrand;

    private  TextView tvPrice;
    private TextView tvSpecifications;

    // Data variables
    private String id, title, price, brand, shipping;
    private ArrayList<String> images, specificsList;
    private LinearLayout product_progress;

        public static ProductFragment newInstance(String id, String shipping) {
            ProductFragment fragment = new ProductFragment();
            Bundle args = new Bundle();
            args.putString("id", id);
            args.putString("shipping", shipping);
            fragment.setArguments(args);
            return fragment;
        }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_details, container, false);

        // Initialize UI components
        llImagesContainer = view.findViewById(R.id.llImagesContainer);
        tvProductTitle = view.findViewById(R.id.tvProductTitle);
        tvPriceAndShipping = view.findViewById(R.id.tvPriceAndShipping);
        tvBrand = view.findViewById(R.id.tvBrandDetail);
        tvSpecifications = view.findViewById(R.id.tvSpecifications);
        tvPrice = view.findViewById((R.id.tvPriceDetail));
        product_progress = view.findViewById(R.id.product_progress);

        product_progress.setVisibility(View.VISIBLE);

        id = getArguments().getString("id");
        shipping = getArguments().getString("shipping");


        fetchProductDetails(id, shipping);

        return view;
    }
    private void fetchProductDetails(String id, String shipping) {
        // Make an API call to fetch product data
        APICalls.getInstance(getContext()).getEbayItem(id, new InterfaceAPI() {
            @Override
            public void onSuccess(JSONObject response) {

                product_progress.setVisibility(View.GONE);

                   parseProductItems(response);

            }

            @Override
            public void onSuccess(JSONArray response) {

            }

            @Override
            public void onError(Exception error) {
                Log.d("Values",error.getMessage());
            }
        });

        // Use Volley or any other network library to fetch the data
        // On response, parse the JSON and populate the views
    }
    private void parseProductItems(JSONObject response) {

        try {
            // Check if the "Item" JSON object exists
            if (response.has("Item")) {
                JSONObject itemObject = response.getJSONObject("Item");

                // Title
                title = itemObject.getString("Title");
                tvProductTitle.setText(title);
//                productDetails.setTitle(title);

                // Price - Assuming you want the ConvertedCurrentPrice
                JSONObject currentPriceObject = itemObject.getJSONObject("ConvertedCurrentPrice");
                price = currentPriceObject.getString("Value");
                tvPriceAndShipping.setText(String.format("$%s %s", price, shipping.equals("0.0") ? "With Free Shipping" : String.format("With $%s Shipping", shipping)));
//
                tvPrice.setText(String.format("Price           $%s", price));



                // Brand - Assuming it's in the ItemSpecifics array
                JSONArray itemSpecifics = itemObject.getJSONObject("ItemSpecifics").getJSONArray("NameValueList");
                brand = "";
                for (int i = 0; i < itemSpecifics.length(); i++) {
                    JSONObject nameValuePair = itemSpecifics.getJSONObject(i);
                    Log.d("Values",nameValuePair.toString());
                    if ("Brand".equals(nameValuePair.getString("Name"))) {
                        brand = nameValuePair.getJSONArray("Value").getString(0);
                        break;
                    }
                }
//
                tvBrand.setText(String.format("Brand            %s", brand));
                // Images
                JSONArray pictureURLArray = itemObject.getJSONArray("PictureURL");
                images = new ArrayList<>();
                for (int i = 0; i < pictureURLArray.length(); i++) {
                    images.add(pictureURLArray.getString(i));
                }
                int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

                for (String imageUrl : images) {
                    ImageView imageView = new ImageView(getContext());
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(screenWidth, LinearLayout.LayoutParams.MATCH_PARENT);
                    imageView.setLayoutParams(layoutParams);
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    Picasso.get().load(imageUrl).into(imageView);
                    llImagesContainer.addView(imageView);
                }
//
//                for (String imageUrl : images) {
//                    ImageView imageView = new ImageView(getContext());
//                    imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                    Picasso.get().load(imageUrl).into(imageView);
//                    llImagesContainer.addView(imageView);
//                }
                // Item specifics
                specificsList = new ArrayList<>();
                for (int i = 0; i < itemSpecifics.length(); i++) {
                    JSONObject nameValuePair = itemSpecifics.getJSONObject(i);
                    String name = nameValuePair.getString("Name");
                    JSONArray values = nameValuePair.getJSONArray("Value");
                    StringBuilder valuesConcat = new StringBuilder();
                    for (int j = 0; j < values.length(); j++) {
                        if (j > 0) {
                            valuesConcat.append(", ");
                        }
                        valuesConcat.append(values.getString(j));
                    }
                    specificsList.add(String.valueOf(valuesConcat));
                }
//
                StringBuilder specificsBuilder = new StringBuilder();
                for (String specific : specificsList) {
                    specificsBuilder.append("- "+specific).append("\n");
                }
                tvSpecifications.setText(specificsBuilder.toString().trim());

            } else {
                // Handle the case where the "Item" object is missing
                Log.e("parseProductDetails", "No 'Item' object in response");
            }
        } catch (JSONException e) {
            // Log the exception
            Log.e("parseProductDetails", "Error parsing JSON", e);
        }

    }
}

