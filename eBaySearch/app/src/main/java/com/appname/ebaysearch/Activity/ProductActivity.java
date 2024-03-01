package com.appname.ebaysearch.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appname.ebaysearch.API.APICalls;
import com.appname.ebaysearch.API.InterfaceAPI;
import com.appname.ebaysearch.Fragments.PhotosFragment;
import com.appname.ebaysearch.Fragments.ProductFragment;
import com.appname.ebaysearch.Fragments.SearchFragment;
import com.appname.ebaysearch.Fragments.ShippingFragment;
import com.appname.ebaysearch.Fragments.SimilarFragment;
import com.appname.ebaysearch.Fragments.WishListFragment;
import com.appname.ebaysearch.Items.EbayItem;
import com.appname.ebaysearch.Items.PhotoItem;
import com.appname.ebaysearch.Items.ProductItem;
import com.appname.ebaysearch.Items.ShippingItem;
import com.appname.ebaysearch.Items.SimilarItem;
import com.appname.ebaysearch.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductActivity extends AppCompatActivity {

    private String id,shipping,title;

    private FloatingActionButton cart;
    private ImageView back_button;
    private TextView title_text;
    private String facebook_value;
    private ImageView facebook_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        ViewPager viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        back_button = findViewById(R.id.back_button);
        title_text = findViewById(R.id.title_text);
        facebook_logo = findViewById(R.id.facebook_logo);

        cart = findViewById(R.id.fabCart);
        cart.setTag(R.drawable.cart_plus);

        EbayItem item = (EbayItem) getIntent().getSerializableExtra("item");
        Log.d("Values",item.getId().toString());
        checkIfInWishlistAndUpdateIcon(cart, item.getId().get(0));
        id = item.getId().get(0);
        shipping = item.getItemShipping();
        title = item.getTitle();

        title_text.setText(title);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onBackPressed();
                Intent returnIntent = new Intent();
                // Put the updated cart data in returnIntent
                boolean isInWishlist = (Integer)cart.getTag() == R.drawable.cart_remove;
                returnIntent.putExtra("id",id);
                returnIntent.putExtra("updated_cart_data", isInWishlist);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });


        String truncatedTitle = truncateTitle(title);

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isInWishlist = (Integer)cart.getTag() == R.drawable.cart_remove;

                if (!isInWishlist) {
                    // Item is not in wishlist, add it
                    APICalls.getInstance(v.getContext()).sendDataMongo(item, new InterfaceAPI() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            Log.d("Values", response.toString());
                            updateCartIcon(cart, true);  // Update to 'remove from wishlist' icon
                            Toast.makeText(v.getContext(),  truncatedTitle + "was added to wishlist", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(JSONArray response) {

                        }

                        @Override
                        public void onError(Exception error) {
                            Log.d("Values", error.getMessage());
                        }
                    });
                } else {
                    // Item is in wishlist, remove it
                    APICalls.getInstance(v.getContext()).removeDataMongo(item.getId().get(0), new InterfaceAPI() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            Log.d("Values", response.toString());
                            updateCartIcon(cart, false); // Update to 'add to wishlist' icon
                            Toast.makeText(v.getContext(), truncatedTitle + " was removed from wishlist", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(JSONArray response) {

                        }

                        @Override
                        public void onError(Exception error) {
                            Log.d("Values", error.getMessage());
                        }
                    });
                }
            }
        });

        // Setup the adapter for ViewPager
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return ProductFragment.newInstance(
                                id,
                                shipping
                        );
                    case 1:
                        return new ShippingFragment().newInstance(
                                id,
                                shipping
                        );
                    case 2:
                        return new PhotosFragment().newInstance(
                                title
                        );
                    case 3:
                        return new SimilarFragment().newInstance(
                                id
                        );
                    default:
                        return null;
                }
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "Product";
                    case 1:
                        return "Shipping";
                    case 2:
                        return "Photos";
                    case 3:
                        return "Similar";
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        });

        // Connect the TabLayout with the ViewPager
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.information_variant);
        tabLayout.getTabAt(1).setIcon(R.drawable.truck_delivery);
        tabLayout.getTabAt(2).setIcon(R.drawable.google);
        tabLayout.getTabAt(3).setIcon(R.drawable.equal);
        tabLayout.setTabIconTintResource(R.color.tab_icon_color_selector);

        fetchProductDetails(id, shipping);

        facebook_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hashtag = "#CSCI571Fall23AndroidApp";
                String encodedHashtag = Uri.encode(hashtag); // Encode the hashtag
                String facebookShareUrl = "https://www.facebook.com/sharer/sharer.php?u="
                        + Uri.encode(facebook_value)
                        + "&display=popup&hashtag="
                        + encodedHashtag;

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookShareUrl));
                startActivity(browserIntent);
            }
        });


//        facebook_logo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String facebookShareUrl = "https://www.facebook.com/sharer/sharer.php?u=" + Uri.encode(facebook_value) + "&display=popup";
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookShareUrl));
//                startActivity(browserIntent);
//            }
//        });


    }

    private void fetchProductDetails(String id, String shipping) {
        // Make an API call to fetch product data
        APICalls.getInstance(this).getEbayItem(id, new InterfaceAPI() {
            @Override
            public void onSuccess(JSONObject response) {


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

        // Check if the "Item" JSON object exists
        if (response.has("Item")) {
            JSONObject itemObject = null;
            try {
                itemObject = response.getJSONObject("Item");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            // Title
            try {
                facebook_value = itemObject.getString("ViewItemURLForNaturalSearch");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            Log.d("Values",facebook_value);



        }
    }
    private void checkIfInWishlistAndUpdateIcon(ImageView imageView, String itemId) {
//        String url = "https://ruchibackend.uc.r.appspot.com/wishlist/ids";
        // Make a request to your API to check if the item is in the wishlist
        // Update the icon in the response listener
        // Use Volley or any other network library you are using
        APICalls.getInstance(imageView.getContext()).checkCart(itemId, new InterfaceAPI() {
            @Override
            public void onSuccess(JSONObject response) {
                // Assuming the response contains a boolean indicating if the item is in the cart
                boolean isInWishlist = false;
                try {
                    isInWishlist = response.getBoolean("isInWishlist");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                // Update the icon based on whether the item is in the wishlist
                updateCartIcon(imageView, isInWishlist);

                Log.d("Values", response.toString());
            }

            @Override
            public void onSuccess(JSONArray response) {

            }


            @Override
            public void onError(Exception error) {
                Log.d("Values", error.getMessage());
            }
        });
    }
    private void updateCartIcon(ImageView imageView, boolean isInWishlist) {
        if (isInWishlist) {
            imageView.setImageResource(R.drawable.cart_remove_white);
            imageView.setTag(R.drawable.cart_remove);
        } else {
            imageView.setImageResource(R.drawable.cart_plus_white);
            imageView.setTag(R.drawable.cart_plus);
        }
    }
    private String truncateTitle(String title) {
        final int MAX_LENGTH = 10; // Define maximum length for the title
        if (title.length() > MAX_LENGTH) {
            // Truncate and append "..." if the title is too long
            return title.substring(0, MAX_LENGTH) + "...";
        } else {
            return title;
        }
    }
}