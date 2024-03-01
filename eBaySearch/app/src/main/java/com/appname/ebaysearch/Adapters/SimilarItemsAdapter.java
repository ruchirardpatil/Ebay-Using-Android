package com.appname.ebaysearch.Adapters;

// In SimilarItemsAdapter.java

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.appname.ebaysearch.Items.SimilarItem;
import com.appname.ebaysearch.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SimilarItemsAdapter extends RecyclerView.Adapter<SimilarItemsAdapter.ViewHolder> {

    private ArrayList<SimilarItem> similarItems;
    private Context context;

    public SimilarItemsAdapter(ArrayList<SimilarItem> similarItems, Context context) {
        this.similarItems = similarItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_similar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SimilarItem item = similarItems.get(position);
        holder.title.setText(item.getTitle());
        holder.price.setText(item.getPrice());
        holder.shippingCost.setText(item.getShippingCost());
        holder.daysLeft.setText(item.getDaysLeft()+" Days Left");

        Picasso.get().load(item.getImage()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return similarItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, price, shippingCost, daysLeft;
        ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.textViewTitle);
            price = view.findViewById(R.id.textViewPrice);
            shippingCost = view.findViewById(R.id.textViewShippingCost);
            daysLeft = view.findViewById(R.id.textViewDaysLeft);
            imageView = view.findViewById(R.id.imageViewSimilarItem);
        }
    }
}

