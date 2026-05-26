package com.example.bookreader.library.ui;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic base adapter for RecyclerView to reduce code duplication.
 * Handles common adapter operations: list management, item click listeners, and UI updates.
 *
 * @param <T> The type of items displayed in the list
 * @param <VH> The ViewHolder type
 */
public abstract class BaseListAdapter<T, VH extends RecyclerView.ViewHolder> 
        extends RecyclerView.Adapter<VH> {

    protected List<T> items = new ArrayList<>();

    /** Update the list and refresh the view */
    public void submitList(List<T> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
