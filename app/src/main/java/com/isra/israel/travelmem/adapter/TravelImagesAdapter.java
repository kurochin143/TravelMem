package com.isra.israel.travelmem.adapter;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.isra.israel.travelmem.R;
import com.isra.israel.travelmem.model.TravelImage;

import java.util.ArrayList;

public class TravelImagesAdapter extends RecyclerView.Adapter<TravelImagesAdapter.ViewHolder> {

    private ArrayList<TravelImage> travelImages = new ArrayList<>();
    private OnTravelImageClickListener onTravelImageClickListener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_travel_image, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final TravelImage travelImage = travelImages.get(position);

        if (travelImage.getUriStr() == null) {
            holder.imageView.setImageURI(null);
        } else {
            holder.imageView.setImageURI(travelImage.getUri());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTravelImageClickListener.onTravelImageClick(travelImage, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return travelImages.size();
    }

    public ArrayList<TravelImage> getTravelImages() {
        return travelImages;
    }

    public void setTravelImages(@NonNull ArrayList<TravelImage> travelImages) {
        this.travelImages = travelImages;
        notifyDataSetChanged();
    }

    public void addTravelImage(TravelImage travelImage) {
        travelImages.add(travelImage);
        notifyItemInserted(travelImages.size() - 1);
    }

    public void removeTravelImage(int position) {
        travelImages.remove(position);
        notifyItemRemoved(position);
    }

    public void setTravelImageAt(TravelImage travelImage, int position) {
        travelImages.set(position, travelImage);
        notifyItemChanged(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.i_travel_image_i_image);
        }

        private ImageView imageView;
    }

    public void setOnTravelImageClickListener(OnTravelImageClickListener l) {
        onTravelImageClickListener = l;
    }

    public interface OnTravelImageClickListener {
        void onTravelImageClick(TravelImage travelImage, int position);
    }
}
