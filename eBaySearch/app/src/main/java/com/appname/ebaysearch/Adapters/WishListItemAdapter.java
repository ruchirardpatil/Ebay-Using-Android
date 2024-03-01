package com.appname.ebaysearch.Adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.appname.ebaysearch.API.APICalls;
import com.appname.ebaysearch.API.InterfaceAPI;
import com.appname.ebaysearch.Activity.ProductActivity;
import com.appname.ebaysearch.Fragments.WishListFragment;
import com.appname.ebaysearch.Items.EbayItem;
import com.appname.ebaysearch.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class WishListItemAdapter extends RecyclerView.Adapter<WishListItemAdapter.ViewHolder> {

    private List<EbayItem> ebayItems;
    private WishListFragment fragment;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Define the view holder for each item
        public TextView textViewTitle,textViewPrice,textViewZipcode,textViewShippingInfo,textViewCondition;
        public ImageView imageViewItem;
        public CardView cardView;
        public ImageView imageViewCart;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.wish_textViewTitle);
            textViewPrice = itemView.findViewById(R.id.wish_textViewPrice);
            imageViewItem = itemView.findViewById(R.id.wish_imageViewItem);
            textViewZipcode = itemView.findViewById(R.id.wish_textViewZipcode);
            textViewShippingInfo = itemView.findViewById(R.id.wish_textViewShippingInfo);
            textViewCondition = itemView.findViewById(R.id.wish_textViewCondition);
            imageViewCart = itemView.findViewById(R.id.wish_imageViewCart);
            cardView = itemView.findViewById(R.id.wish_cardView);
        }
    }

    public WishListItemAdapter(List<EbayItem> items,WishListFragment fragment) {

        this.ebayItems = items;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public WishListItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_item_layout, parent, false);
        return new WishListItemAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WishListItemAdapter.ViewHolder holder, int position) {

        EbayItem item = ebayItems.get(position);
        holder.textViewTitle.setText(item.getTitle());
        holder.textViewPrice.setText(item.getPrice());
        holder.textViewCondition.setText(item.getCondition());
        holder.textViewZipcode.setText(item.getZipCode());
        holder.textViewShippingInfo.setText(item.getItemShipping());

        // Use Picasso or another image loading library to load the image
        if (item.getImage() != null && !item.getImage().isEmpty()) {
            // Use the first image URL from the list
            String imageUrl = item.getImage().get(0);
            Log.d("Image",imageUrl);
            if (!imageUrl.isEmpty()) {
                Picasso.get().load(imageUrl).into(holder.imageViewItem);
            }
        }
        fragment.updateTotalPrice(calculateTotalPrice(),ebayItems.size());

        checkIfInWishlistAndUpdateIcon(holder.imageViewCart, ebayItems.get(position).getId().get(0));

        holder.imageViewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Determine the current state based on the icon
                String itemId = ebayItems.get(position).getId().get(0);
                APICalls.getInstance(view.getContext()).removeDataMongo(itemId, new InterfaceAPI() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Log.d("Values", "Item removed from wishlist");
                        // Remove the item from the adapter's dataset
                        ebayItems.remove(position);
                        // Notify the adapter of the item removal

                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, ebayItems.size());
                        Toast.makeText(view.getContext(), "Removed from wishlist", Toast.LENGTH_SHORT).show();
                        fragment.updateTotalPrice(calculateTotalPrice(),ebayItems.size());
                        fragment.checkAndUpdateUIForEmptyList();
                    }

                    @Override
                    public void onSuccess(JSONArray response) {
                        // Handle JSONArray response if needed
                    }

                    @Override
                    public void onError(Exception error) {
                        Log.d("Values", "Error removing item from wishlist: " + error.getMessage());
                    }
                });
//                WishListItemAdapter.this.removeItemAndUpdateTotal(position, fragment);
            }
        });


        // Set the on click listener for the whole card
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open a new activity when the card is clicked
                Intent intent = new Intent(view.getContext(), ProductActivity.class);
                // Add extras or data to your intent if needed
                intent.putExtra("item", ebayItems.get(position));
                view.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return ebayItems.size();
    }

    public void setData(List<EbayItem> newItems) {
        ebayItems = newItems;
        notifyDataSetChanged();
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
            imageView.setImageResource(R.drawable.cart_remove);
            imageView.setTag(R.drawable.cart_remove);
        } else {
            imageView.setImageResource(R.drawable.cart_plus);
            imageView.setTag(R.drawable.cart_plus);
        }
    }
    private double calculateTotalPrice() {
        double totalPrice = 0.0;
        for (EbayItem item : ebayItems) {
            try {
                totalPrice += Double.parseDouble(item.getPrice().replaceAll("[^\\d.]", ""));
            } catch (NumberFormatException e) {
                Log.e("WishList", "Error parsing price for item: " + item.getTitle(), e);
            }
        }
        return totalPrice;
    }

}
