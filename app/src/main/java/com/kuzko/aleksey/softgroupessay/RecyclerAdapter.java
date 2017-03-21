package com.kuzko.aleksey.softgroupessay;

/**
 * Created by Aleks on 20.03.2017.
 */
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.kuzko.aleksey.softgroupessay.datamodel.Forecastday;
import java.util.List;


class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<Forecastday> forecastdays;

    RecyclerAdapter(List<Forecastday> dataset) {
        forecastdays = dataset;
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
        holder.textViewRecyclerItem.setText(forecastdays.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return forecastdays.size();
    }
}
