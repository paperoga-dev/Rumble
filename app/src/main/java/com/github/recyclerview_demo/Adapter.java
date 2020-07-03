package com.github.recyclerview_demo;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static class LoadingViewHolder extends RecyclerView.ViewHolder {
        private final ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);

            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvItem;

        public ItemViewHolder(View itemView) {
            super(itemView);

            tvItem = itemView.findViewById(R.id.textView);
        }

        public void setText(String text) {
            tvItem.setText(text);
        }
    }

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private final List<String> items;

    public Adapter() {
        super();

        this.items = new ArrayList<>();
    }

    void addNullItem() {
        items.add(null);
        notifyItemInserted(items.size() - 1);
    }

    void removeNullItem() {
        items.remove(items.size() - 1);
        notifyItemRemoved(items.size());
    }

    void addItems(List<String> newItems) {
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return (items.get(position) == null)? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ITEM:
                return new ItemViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_row, parent, false)
                );

            case VIEW_TYPE_LOADING:
                return new LoadingViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_loading, parent, false)
                );

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ItemViewHolder) {

            String item = items.get(position);
            ((ItemViewHolder) viewHolder).setText(item);

            if ((position % 20 ) == 0) {
                ((ItemViewHolder) viewHolder).tvItem.setBackground(new ColorDrawable(Color.BLUE));
            } else {
                ((ItemViewHolder) viewHolder).tvItem.setBackground(null);
            }

        } else if (viewHolder instanceof LoadingViewHolder) {
            //
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
