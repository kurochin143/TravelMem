package com.isra.israel.travelmem.adapter;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.isra.israel.travelmem.R;
import com.isra.israel.travelmem.model.TravelVideo;

import java.util.ArrayList;

public class TravelVideosAdapter extends RecyclerView.Adapter<TravelVideosAdapter.ViewHolder> {

    private ArrayList<TravelVideo> travelVideos = new ArrayList<>();
    private OnTravelVideoClickListener onTravelVideoClickListener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_travel_video, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final TravelVideo travelVideo = travelVideos.get(position);

        if (travelVideo.getUriStr() == null) {
            holder.imageView.setImageURI(null);
        } else {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(holder.itemView.getContext(), travelVideo.getUri());
            String durationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long duration = Long.parseLong(durationStr);
            long half = duration / 2;
            // TODO VERY LOW get scaled frame
            Bitmap frameAtHalf = mediaMetadataRetriever.getFrameAtTime(half * 1000);

            holder.imageView.setImageBitmap(frameAtHalf);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTravelVideoClickListener.onTravelVideoClick(travelVideo, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return travelVideos.size();
    }

    public ArrayList<TravelVideo> getTravelVideos() {
        return travelVideos;
    }

    public void setTravelVideos(@NonNull ArrayList<TravelVideo> travelVideos) {
        this.travelVideos = travelVideos;
        notifyDataSetChanged();
    }

    public void addTravelVideo(TravelVideo travelVideo) {
        travelVideos.add(travelVideo);
        notifyItemInserted(travelVideos.size() - 1);
    }

    public void removeTravelVideo(int position) {
        travelVideos.remove(position);
        notifyItemChanged(position);
    }

    public void setTravelVideoAt(TravelVideo travelVideo, int position) {
        travelVideos.set(position, travelVideo);
        notifyItemChanged(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.i_travel_video_i_image);
        }

        private ImageView imageView;
    }

    public void setOnTravelVideoClickListener(OnTravelVideoClickListener l) {
        onTravelVideoClickListener = l;
    }

    public interface OnTravelVideoClickListener {
        void onTravelVideoClick(TravelVideo travelVideo, int position);
    }
}
