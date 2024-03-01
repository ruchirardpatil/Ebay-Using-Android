package com.appname.ebaysearch.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.appname.ebaysearch.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder> {

    private ArrayList<String> imageUrls;

    public PhotosAdapter(ArrayList<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Picasso.get()
                .load(imageUrls.get(position))
                .into(holder.imageView);

        if (position < getItemCount() - 1) {
            holder.horizontalLine.setVisibility(View.VISIBLE);
        } else {
            holder.horizontalLine.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        View horizontalLine;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.image_view);
            horizontalLine = view.findViewById(R.id.horizontal_line);// assuming you have an ImageView in your item_photo.xml
        }
    }
}

