package com.isra.israel.travelmem.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.isra.israel.travelmem.R;
import com.isra.israel.travelmem.model.Travel;

import java.util.ArrayList;

public class TravelsAdapter extends RecyclerView.Adapter<TravelsAdapter.ViewHolder> {

    private ArrayList<Travel> travels = new ArrayList<>();
    private OnTravelClickedListener onTravelClickedListener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_travel, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Travel travel = travels.get(position);

        holder.nameTextView.setText(travel.getName());
        holder.startDateTextView.setText(travel.getStartDate());
        holder.endDateTextView.setText(travel.getEndDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTravelClickedListener.onTravelClicked(travel, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return travels.size();
    }

    public void setTravels(@NonNull ArrayList<Travel> travels) {
        this.travels = travels;
        notifyDataSetChanged();
    }

    public void setTravelAt(@NonNull Travel travel, int index) {
        this.travels.set(index, travel);
        notifyItemChanged(index);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.i_travel_t_name);
            startDateTextView = itemView.findViewById(R.id.i_travel_t_start_date);
            endDateTextView = itemView.findViewById(R.id.i_travel_t_end_date);
        }

        private TextView nameTextView;
        private TextView startDateTextView;
        private TextView endDateTextView;
    }

    public void setOnTravelClickedListener(OnTravelClickedListener l) {
        onTravelClickedListener = l;
    }

    public interface OnTravelClickedListener {
        void onTravelClicked(Travel travel, int position);
    }
}
