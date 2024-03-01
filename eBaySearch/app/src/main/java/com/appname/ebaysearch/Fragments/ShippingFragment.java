package com.appname.ebaysearch.Fragments;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.appname.ebaysearch.API.APICalls;
import com.appname.ebaysearch.API.InterfaceAPI;
import com.appname.ebaysearch.Items.ShippingItem;
import com.appname.ebaysearch.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Handler;


public class ShippingFragment extends Fragment {

    private String storeName,feedBackScore,popularity,feedbackStar,shipping,globalShipping,handlingTime,policy,returnWithin,refundMode,shippedBy,id;

    private TextView tvStoreNameDetail,tvFeedbackScoreDetail,tvPopularityDetail,tvFeedbackStarDetail,tvShippingCostDetail,tvGlobalShippingDetail,tvHandlingTimeDetail,tvPolicyDetail,tvReturnWithinDetail,tvRefundModeDetail,tvShippedByDetail;
    private LinearLayout shipping_progress;

    private ImageView ivFeedbackStar;

    private LinearLayout StoreName,FeedbackScore,Popularity,FeedbackStar,ShippingCost,GlobalShipping,HandlingTime,Policy,ReturnsWithin,RefundMode,ShippedBy;
    ProgressBar progressBar;
    TextView progressText;
    public static ShippingFragment newInstance(String id, String shipping) {
        ShippingFragment fragment = new ShippingFragment();
        Bundle args = new Bundle();
        args.putString("id",id);
        args.putString("shipping",shipping);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.shipping_fragment, container, false);


        // Initialize UI components
        tvStoreNameDetail = view.findViewById(R.id.tvStoreNameDetail);
        tvFeedbackScoreDetail = view.findViewById(R.id.tvFeedbackScoreDetail);
//        tvPopularityDetail = view.findViewById(R.id.tvPopularityDetail);
//        tvFeedbackStarDetail = view.findViewById(R.id.tvFeedbackStarDetail);
        ivFeedbackStar = view.findViewById(R.id.ivFeedbackStar);
        tvShippingCostDetail = view.findViewById(R.id.tvShippingCostDetail);
        tvGlobalShippingDetail = view.findViewById(R.id.tvGlobalShippingDetail);
        tvHandlingTimeDetail = view.findViewById(R.id.tvHandlingTimeDetail);
        tvPolicyDetail = view.findViewById(R.id.tvPolicyDetail);
        tvReturnWithinDetail = view.findViewById(R.id.tvReturnWithinDetail);
        tvRefundModeDetail = view.findViewById(R.id.tvRefundModeDetail);
        tvShippedByDetail = view.findViewById(R.id.tvShippedByDetail);
        shipping_progress = view.findViewById(R.id.shipping_progress);
        progressBar = view.findViewById(R.id.circularProgressBar);
        progressText = view.findViewById(R.id.progressText);

        StoreName = view.findViewById(R.id.StoreName);
        FeedbackScore = view.findViewById(R.id.FeedbackScore);
        Popularity = view.findViewById(R.id.Popularity);
        FeedbackStar = view.findViewById(R.id.FeedbackStar);
        ShippingCost = view.findViewById(R.id.ShippingCost);
        GlobalShipping = view.findViewById(R.id.GlobalShipping);
        HandlingTime = view.findViewById(R.id.HandlingTime);
        Policy = view.findViewById(R.id.Policy);
        ReturnsWithin = view.findViewById(R.id.ReturnsWithin);
        RefundMode = view.findViewById(R.id.RefundMode);
        ShippedBy = view.findViewById(R.id.ShippedBy);



        id = getArguments().getString("id");
        shipping = getArguments().getString("shipping");

        shipping_progress.setVisibility(View.VISIBLE);

        fetchProductDetails(id, shipping);


        return view;
    }
    private void fetchProductDetails(String id, String shipping) {
        // Make an API call to fetch product data
        APICalls.getInstance(getContext()).getEbayItem(id, new InterfaceAPI() {
            @Override
            public void onSuccess(JSONObject response) {

                shipping_progress.setVisibility(View.GONE);

                parseShippingItems(response);

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
    private void parseShippingItems(JSONObject response) {

        try {
            // Check if the "Item" JSON object exists
            if (response.has("Item")) {
                JSONObject itemObject = response.getJSONObject("Item");

                // Use optJSONObject and optString to avoid JSONException
                JSONObject currentNameObject = itemObject.optJSONObject("Storefront");
                 storeName = currentNameObject != null ? currentNameObject.optString("StoreName", "NA") : "NA";

                String storeUrl = itemObject != null ? itemObject.optString("ViewItemURLForNaturalSearch", "") : "";

//

                JSONObject sellerObject = itemObject.optJSONObject("Seller");
                feedBackScore = sellerObject != null ? sellerObject.optString("FeedbackScore", "NA") : "NA";
//

                 popularity = sellerObject != null ? sellerObject.optString("PositiveFeedbackPercent", "NA") : "NA";
                float popularityValue;
                try {
                    popularityValue = Float.parseFloat(popularity);
                } catch (NumberFormatException e) {
                    popularityValue = 0; // Set to default value if parsing fails
                    Log.e("parseShippingItems", "Error parsing popularity", e);
                }
                int roundedPopularity = Math.round(popularityValue);
//

                feedbackStar = sellerObject != null ? sellerObject.optString("FeedbackRatingStar", "NA") : "NA";


//

                 globalShipping = itemObject.optString("GlobalShipping", "NA");
//

                 handlingTime = itemObject.optString("HandlingTime", "NA");
//

                JSONObject returnsObject = itemObject.optJSONObject("ReturnPolicy");
                 policy = returnsObject != null ? returnsObject.optString("ReturnsAccepted", "NA") : "NA";
//

                 returnWithin = returnsObject != null ? returnsObject.optString("ReturnsWithin", "NA") : "NA";
//

                 refundMode = returnsObject != null ? returnsObject.optString("Refund", "NA") : "NA";
//

                 shippedBy = returnsObject != null ? returnsObject.optString("ShippingCostPaidBy", "NA") : "NA";
//


                tvStoreNameDetail.setText(storeName);
                tvStoreNameDetail.setPaintFlags(tvStoreNameDetail.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

// Set onClickListener for opening the store URL
                tvStoreNameDetail.setOnClickListener(v -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(storeUrl));
                    startActivity(browserIntent);
                });

// Handler to start the scrolling effect
                final Handler handler = new Handler();
                final Runnable runnable = new Runnable() {
                    int scrollPos = 0;
                    public void run() {
                        if(scrollPos < tvStoreNameDetail.getWidth()) {
                            scrollPos += 7; // Speed of scroll
                            tvStoreNameDetail.scrollTo(scrollPos, 0);
                        } else {
                            scrollPos = -tvStoreNameDetail.getWidth();
                        }
                        handler.postDelayed(this, 50); // Delay between scroll updates
                    }
                };

// Start the scrolling
                handler.postDelayed(runnable, 1000);



                switch (feedbackStar){
                    case "Blue":
                        ivFeedbackStar.setImageResource(R.drawable.star_circle_outline);
                        ivFeedbackStar.setColorFilter(ContextCompat.getColor(getContext(),R.color.blue));
                        break;
                    case "Green":
                        ivFeedbackStar.setImageResource(R.drawable.star_circle_outline);
                        ivFeedbackStar.setColorFilter(ContextCompat.getColor(getContext(),R.color.green));
                        break;
                    case "Purple":
                        ivFeedbackStar.setImageResource(R.drawable.star_circle_outline);
                        ivFeedbackStar.setColorFilter(ContextCompat.getColor(getContext(),R.color.purple));
                        break;
                    case "Red":
                        ivFeedbackStar.setImageResource(R.drawable.star_circle_outline);
                        ivFeedbackStar.setColorFilter(ContextCompat.getColor(getContext(),R.color.red));
                        break;
                    case "Turquoise":
                        ivFeedbackStar.setImageResource(R.drawable.star_circle_outline);
                        ivFeedbackStar.setColorFilter(ContextCompat.getColor(getContext(),R.color.Turquoise));
                        break;
                    case "Yellow":
                        ivFeedbackStar.setImageResource(R.drawable.star_circle_outline);
                        ivFeedbackStar.setColorFilter(ContextCompat.getColor(getContext(),R.color.Yellow));
                        break;
                    case "GreenShooting":
                        ivFeedbackStar.setImageResource(R.drawable.star_circle);
                        ivFeedbackStar.setColorFilter(ContextCompat.getColor(getContext(),R.color.green));
                        break;
                    case "PurpleShooting":
                        ivFeedbackStar.setImageResource(R.drawable.star_circle);
                        ivFeedbackStar.setColorFilter(ContextCompat.getColor(getContext(),R.color.purple));
                        break;
                    case "RedShooting":
                        ivFeedbackStar.setImageResource(R.drawable.star_circle);
                        ivFeedbackStar.setColorFilter(ContextCompat.getColor(getContext(),R.color.red));
                        break;
                    case "SilverShooting":
                        ivFeedbackStar.setImageResource(R.drawable.star_circle);
                        ivFeedbackStar.setColorFilter(ContextCompat.getColor(getContext(),R.color.Silver));
                        break;
                    case "TurquoiseShooting":
                        ivFeedbackStar.setImageResource(R.drawable.star_circle);
                        ivFeedbackStar.setColorFilter(ContextCompat.getColor(getContext(),R.color.Turquoise));
                        break;
                    case "YellowShooting":
                        ivFeedbackStar.setImageResource(R.drawable.star_circle);
                        ivFeedbackStar.setColorFilter(ContextCompat.getColor(getContext(),R.color.Yellow));
                        break;
                }
//                tvStoreNameDetail.setText(storeName);
                tvFeedbackScoreDetail.setText(feedBackScore);
//                tvPopularityDetail.setText(popularity);
                progressBar.setProgress(roundedPopularity);
                int percent = (int) popularityValue ;
                progressText.setText(String.valueOf(percent)+"%");
//                tvFeedbackStarDetail.setText(feedbackStar);
                tvShippingCostDetail.setText(shipping);
                if(globalShipping=="true"){
                    tvGlobalShippingDetail.setText("Yes");
                }
                else{
                    tvGlobalShippingDetail.setText("No");
                }

                tvHandlingTimeDetail.setText(handlingTime);
                tvPolicyDetail.setText(policy);
                tvReturnWithinDetail.setText(returnWithin);
                tvRefundModeDetail.setText(refundMode);
                tvShippedByDetail.setText(shippedBy);

            } else {
                // Handle the case where the "Item" object is missing
                Log.e("parseProductDetails", "No 'Item' object in response");
            }


        } catch (JSONException e) {
            // Log the exception
            Log.e("parseProductDetails", "Error parsing JSON", e);
        }

        // Return the parsed product details
    }
}
