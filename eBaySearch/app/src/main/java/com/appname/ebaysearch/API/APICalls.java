package com.appname.ebaysearch.API;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.appname.ebaysearch.Items.EbayItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class APICalls {

    private static APICalls instance = null;
    public RequestQueue queue;

    private APICalls(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    public static synchronized APICalls getInstance(Context context){
        if(null == instance){
            instance = new APICalls(context);
        }
        return instance;
    }

    public static synchronized APICalls getInstance() {
        return instance;
    }


    public void getautocomplete(String keyword, InterfaceAPI anInterfaceAPI){
        Uri.Builder builder = Uri.parse("https://assignment3backend.uc.r.appspot.com/getautocomplete")
                .buildUpon()
                .appendQueryParameter("Keyword", keyword);

        String url = builder.build().toString();

        // ...
        Log.d("Values",url);
        // Add error handling in your Volley ErrorListener
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                anInterfaceAPI::onSuccess, // Method reference for cleaner code
                error -> {
                    Log.e("Values", "Error: " + error.getMessage());
                    // Consider adding a method in your InterfaceAPI for handling errors
                });

        queue.add(jsonObjectRequest);
    }

    public void getBusiness(String keyword, String category, JSONArray conditions, JSONArray shipping, String distance, String zipcode, InterfaceAPI anInterfaceAPI) {
        // Encode the parameters to ensure they are properly escaped
        Uri.Builder builder = Uri.parse("https://assignment3backend.uc.r.appspot.com/getebaydata")
                .buildUpon()
                .appendQueryParameter("keyword", keyword)
                .appendQueryParameter("category", category)
                .appendQueryParameter("distance", distance)
                .appendQueryParameter("zipCode", zipcode);

        if (conditions != null && conditions.length() > 0) {
            builder.appendQueryParameter("condition", conditions.toString());
        }

        if (shipping != null && shipping.length() > 0) {
            builder.appendQueryParameter("shipping", shipping.toString());
        }

        String url = builder.build().toString();

        // ...
        Log.d("Values",url);
        // Add error handling in your Volley ErrorListener
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                anInterfaceAPI::onSuccess, // Method reference for cleaner code
                error -> {
                    Log.e("Values", "Error: " + error.getMessage());
                    // Consider adding a method in your InterfaceAPI for handling errors
                });

        queue.add(jsonObjectRequest);

    }

    public void getEbayItem(String id, InterfaceAPI anInterfaceAPI) {
        // Encode the parameters to ensure they are properly escaped
        Uri.Builder builder = Uri.parse("https://assignment3backend.uc.r.appspot.com/singledata")
                .buildUpon()
                .appendQueryParameter("itemId", id);

        String url = builder.build().toString();

        // ...
        Log.d("Values",url);
        // Add error handling in your Volley ErrorListener
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                anInterfaceAPI::onSuccess, // Method reference for cleaner code
                error -> {
                    Log.e("Values", "Error: " + error.getMessage());
                    // Consider adding a method in your InterfaceAPI for handling errors
                });

        queue.add(jsonObjectRequest);

    }

    public void getphotos(String key, InterfaceAPI anInterfaceAPI){
        Uri.Builder builder = Uri.parse("https://assignment3backend.uc.r.appspot.com/getphotos")
                .buildUpon()
                .appendQueryParameter("keyword", key);

        String url = builder.build().toString();

        // ...
        Log.d("Values",url);
        // Add error handling in your Volley ErrorListener
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                anInterfaceAPI::onSuccess, // Method reference for cleaner code
                error -> {
                    Log.e("Values", "Error: " + error.getMessage());
                    // Consider adding a method in your InterfaceAPI for handling errors
                });

        queue.add(jsonObjectRequest);
    }

    public void getsimilaritems(String id, InterfaceAPI anInterfaceAPI) {
        // Encode the parameters to ensure they are properly escaped
        Uri.Builder builder = Uri.parse("https://assignment3backend.uc.r.appspot.com/similardata")
                .buildUpon()
                .appendQueryParameter("itemId", id);

        String url = builder.build().toString();

        // ...
        Log.d("Values",url);
        // Add error handling in your Volley ErrorListener
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                anInterfaceAPI::onSuccess, // Method reference for cleaner code
                error -> {
                    Log.e("Values", "Error: " + error.getMessage());
                    // Consider adding a method in your InterfaceAPI for handling errors
                });

        queue.add(jsonObjectRequest);

    }

    public void sendDataMongo(EbayItem item,InterfaceAPI anInterfaceAPI){
        Log.d("Values",item.getId().toString());
        Log.d("Values",item.getImage().toString());
        Log.d("Values",item.getTitle().toString());
        Log.d("Values",item.getPrice().toString());
        Log.d("Values",item.getItemUrl().toString());
        Log.d("Values",item.getItemShipping().toString());
        Log.d("Values",item.getZipCode().toString());
        Log.d("Values",item.getCondition().toString());
        JSONObject postData = new JSONObject();
        try {
            // Add data to the JSON object, use "N/A" if any data is missing
            postData.put("itemId", !item.getId().isEmpty() ? new JSONArray(item.getId()) : "N/A");
            postData.put("itemImage", !item.getImage().isEmpty() ? new JSONArray(item.getImage()) : "N/A");
            postData.put("itemTitle", item.getTitle() != null ? item.getTitle() : "N/A");
            postData.put("itemPrice", item.getPrice() != null ? item.getPrice() : "N/A");
            postData.put("itemShipping", item.getItemShipping() != null ? item.getItemShipping() : "N/A");
            postData.put("shippingCost", item.getItemUrl() != null ? item.getItemUrl() : "N/A");
            postData.put("shippingLocation", item.getZipCode() != null ? item.getZipCode() : "N/A");
            postData.put("HandlingTime", item.getCondition() != null ? item.getCondition() : "N/A");
            postData.put("ExpiditedShipping", "N/A");
            postData.put("OneDayShippingAvailable", "N/A");
            postData.put("ReturnsAccepted", "N/A");
            postData.put("sellerName",  "N/A");
            postData.put("feedbackScore","N/A");
            postData.put("popularity", "N/A");
            postData.put("FeedbackRatingStar", "N/A");
            postData.put("topRated", "N/A");
            postData.put("StoreName", "N/A");
            postData.put("BuyProductAt",  "N/A");
            // Add other fields as per your requirement, filling in "N/A" if necessary

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Post the data using Volley
        String url = "https://assignment3backend.uc.r.appspot.com/wishlist";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle the response
                        anInterfaceAPI.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                anInterfaceAPI.onError(error);
            }
        });

        queue.add(jsonObjectRequest);
    }

    public void checkCart(String itemId, InterfaceAPI interfaceAPI) {
        String url = "https://assignment3backend.uc.r.appspot.com/wishlist/ids";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Convert response to a list of String
                            List<String> wishlistIds = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                wishlistIds.add(response.getString(i));
                            }

                            // Check if the current item's ID is in the wishlist
                            boolean isInWishlist = false;
                            if (wishlistIds.contains(itemId)) {
                                isInWishlist = true;
                            }


                            // Create a JSON Object to pass to onSuccess
                            JSONObject result = new JSONObject();
                            result.put("isInWishlist", isInWishlist);

                            // Call onSuccess of the InterfaceAPI
                            interfaceAPI.onSuccess(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            interfaceAPI.onError(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                interfaceAPI.onError(error);
            }
        });

        // Add the request to the RequestQueue
        // Assuming you have a RequestQueue instance named 'queue'
        queue.add(jsonArrayRequest);
    }

    public void removeDataMongo(String itemId, InterfaceAPI interfaceAPI) {
        String url = "https://assignment3backend.uc.r.appspot.com/wishlist/" + itemId;

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Convert response to JSONObject
                            JSONObject jsonResponse = new JSONObject(response);
                            // Call onSuccess of the InterfaceAPI
                            interfaceAPI.onSuccess(jsonResponse);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            interfaceAPI.onError(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                interfaceAPI.onError(error);
            }
        });

        // Add the request to the RequestQueue
        // Assuming you have a RequestQueue instance named 'queue'
        queue.add(stringRequest);
    }

    public void getWishList(InterfaceAPI anInterfaceAPI) {
        Uri.Builder builder = Uri.parse("https://assignment3backend.uc.r.appspot.com/wishlist")
                .buildUpon();

        String url = builder.build().toString();

        // ...
        Log.d("Values",url);
        // Add error handling in your Volley ErrorListener
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                anInterfaceAPI::onSuccess, // Method reference for cleaner code
                error -> {
                    Log.e("Values", "Error: " + error.getMessage());
                    // Consider adding a method in your InterfaceAPI for handling errors
                });

        queue.add(jsonArrayRequest);

    }


}
