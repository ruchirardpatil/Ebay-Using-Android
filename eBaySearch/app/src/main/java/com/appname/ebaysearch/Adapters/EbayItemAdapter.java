package com.appname.ebaysearch.Adapters;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.appname.ebaysearch.API.APICalls;
import com.appname.ebaysearch.API.InterfaceAPI;
import com.appname.ebaysearch.Activity.ProductActivity;
import com.appname.ebaysearch.Items.EbayItem;
import com.appname.ebaysearch.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class EbayItemAdapter extends RecyclerView.Adapter<EbayItemAdapter.ViewHolder> {

    private List<EbayItem> ebayItems;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Define the view holder for each item
        public TextView textViewTitle,textViewPrice,textViewZipcode,textViewShippingInfo,textViewCondition;
        public ImageView imageViewItem;

        public CardView cardView;
        public ImageView imageViewCart;
        // ... other views for your item

        public ViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            imageViewItem = itemView.findViewById(R.id.imageViewItem);
            textViewZipcode = itemView.findViewById(R.id.textViewZipcode);
            textViewShippingInfo = itemView.findViewById(R.id.textViewShippingInfo);
            textViewCondition = itemView.findViewById(R.id.textViewCondition);
            imageViewCart = itemView.findViewById(R.id.imageViewCart);
            cardView = (CardView) itemView;
        }
    }

    public EbayItemAdapter(List<EbayItem> items) {
        this.ebayItems = items;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view by inflating the layout
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ebay_item_layout, parent, false);
        return new ViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get element from your dataset at this position and replace the contents of the view with that element
        EbayItem item = ebayItems.get(position);
        holder.textViewTitle.setText(item.getTitle());
        holder.textViewPrice.setText(item.getPrice());
        holder.textViewCondition.setText(item.getCondition());
        holder.textViewZipcode.setText("Zip: " + item.getZipCode());
        holder.textViewShippingInfo.setText(item.getItemShipping());

        // Use Picasso or another image loading library to load the image
        if (item.getImage() != null && !item.getImage().isEmpty()) {
            // Use the first image URL from the list
            String imageUrl = item.getImage().get(0);
            Log.d("Image", imageUrl);
            if (!imageUrl.isEmpty()) {
                Picasso.get().load(imageUrl).into(holder.imageViewItem);
            }
        }

        checkIfInWishlistAndUpdateIcon(holder.imageViewCart, ebayItems.get(position).getId().get(0));

        holder.imageViewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                // Determine the current state based on the icon
                boolean isInWishlist = (Integer) holder.imageViewCart.getTag() == R.drawable.cart_remove;
                String truncatedTitle = truncateTitle(ebayItems.get(position).getTitle());

                if (!isInWishlist) {
                    // Item is not in wishlist, add it
                    APICalls.getInstance(view.getContext()).sendDataMongo(ebayItems.get(position), new InterfaceAPI() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            Log.d("Values", response.toString());
                            updateCartIcon(holder.imageViewCart, true);
                            Toast.makeText(view.getContext(), truncatedTitle + "was added to wishlist", Toast.LENGTH_SHORT).show();
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
                    APICalls.getInstance(view.getContext()).removeDataMongo(ebayItems.get(position).getId().get(0), new InterfaceAPI() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            Log.d("Values", response.toString());
                            updateCartIcon(holder.imageViewCart, false); // Update to 'add to wishlist' icon
                            Toast.makeText(view.getContext(), truncatedTitle + " was removed from wishlist", Toast.LENGTH_SHORT).show();
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


        // Set the on click listener for the whole card
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                 Open a new activity when the card is clicked
//                Intent intent = new Intent(view.getContext(), ProductActivity.class);
//                // Add extras or data to your intent if needed
//                intent.putExtra("item", ebayItems.get(position));
//                view.getContext().startActivity(intent);
                Intent intent = new Intent(view.getContext(), ProductActivity.class);
                intent.putExtra("item", ebayItems.get(position));
                ((Activity) view.getContext()).startActivityForResult(intent, 100);
            }

        });

    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return ebayItems.size();
    }

    // Update the dataset and refresh the adapter
    public void setData(List<EbayItem> newItems) {
        ebayItems = newItems;
        notifyDataSetChanged();
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

    public void updateWishlistState(String id,boolean updatedCartData) {
        for (EbayItem item : ebayItems) {
            if (item.getId().get(0).equals(id)) {
                // Update the wishlist state of the item
                // Notify the adapter to refresh this item
                Log.d("Cart Update", String.valueOf(updatedCartData));
                notifyItemChanged(ebayItems.indexOf(item));
                break;
            }
        }
    }


}
