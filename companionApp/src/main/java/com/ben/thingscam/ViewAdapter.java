package com.ben.thingscam;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;

public class ViewAdapter extends FirebaseRecyclerAdapter<ThingsCamImages, ViewAdapter.ImagesViewHolder> {

    public static class ImagesViewHolder extends RecyclerView.ViewHolder {

        public final ImageView image;
        public final TextView label;

        public ImagesViewHolder(View itemView) {
            super(itemView);

            this.image = (ImageView) itemView.findViewById(R.id.imageView1);
            this.label = (TextView) itemView.findViewById(R.id.textView1);
        }
    }

    private Context mApplicationContext;

    public ViewAdapter(Context context, DatabaseReference ref) {
        super(ThingsCamImages.class, R.layout.view_adapter, ImagesViewHolder.class, ref);

        mApplicationContext = context.getApplicationContext();
    }

    @Override
    protected void populateViewHolder(ImagesViewHolder viewHolder, ThingsCamImages model, int position) {
        viewHolder.label.setText(model.getLabel());

        if (model.getImage() != null) {
            byte[] imageBytes = Base64.decode(model.getImage(), Base64.NO_WRAP | Base64.URL_SAFE);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            if (bitmap != null) {
                viewHolder.image.setImageBitmap(bitmap);
            } else {
                Drawable placeholder =
                        ContextCompat.getDrawable(mApplicationContext, R.drawable.ic_image);
                viewHolder.image.setImageDrawable(placeholder);
            }
        }
    }

}