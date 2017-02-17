package com.ben.thingscam;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.ImageViewHolder> {
    private List<ThingsCamImages> dataset;

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public TextView label;

        public ImageViewHolder(View itemView) {
            super(itemView);

            this.image = (ImageView) itemView.findViewById(R.id.imageView1);
            this.label = (TextView) itemView.findViewById(R.id.textView1);
        }
    }

    private Context mApplicationContext;

    public ViewAdapter(List<ThingsCamImages> ds) {
        dataset = ds;
    }

    @Override
    public ViewAdapter.ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_adapter, parent, false);
        // set the view's size, margins, paddings and layout parameters
        //...
        ImageViewHolder vh = new ImageViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        holder.label.setText(dataset.get(position).getLabel());
        byte [] image = Base64.decode(dataset.get(position).getImage(), Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
        holder.image.setImageBitmap(bmp);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.size();
    }

}
