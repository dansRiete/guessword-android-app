package com.alexsoft;

/**
 * Created by Aleks on 20.03.2017.
 */
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kuzko.aleksey.alexsoft.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<String> aqiMeasurements;

    RecyclerAdapter(List<String> dataset) {
        aqiMeasurements = dataset;
    }

    RecyclerAdapter(String dataset) {
        List<String> list = Arrays.asList(dataset.split("<br>"));
        Collections.reverse(list);
        aqiMeasurements = list;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewRecyclerItem;
        ViewHolder(View view) {
            super(view);
            textViewRecyclerItem = (TextView) view.findViewById(R.id.textViewRecyclerItem);
        }
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textViewRecyclerItem.setText(aqiMeasurements.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return aqiMeasurements.size();
    }
}
