package com.example.bookreader.library.ui;

import android.view.LayoutInflater;                  // Android Framework - converts XML layout files into View objects
import android.view.View;                            // Android Framework - base class for all UI widgets (used for visibility and click listeners)
import android.view.ViewGroup;                       // Android Framework - container for Views
import android.widget.TextView;                      // Android Widget - displays breadcrumb text

import androidx.annotation.NonNull;                  // Androidx - marks parameters/methods that should never be null (RecyclerView contract)
import androidx.recyclerview.widget.RecyclerView;    // Android Jetpack - efficient list view that recycles items

import com.example.bookreader.R;                                  // Android Resource binding - generated class with resource references
import com.example.bookreader.library.ui.state.Breadcrumb;        // Navigation breadcrumb model

import java.util.List;                               // Java Collections - interface for ordered collections

/**
 * RecyclerView adapter for displaying navigation breadcrumbs.
 * Shows the folder hierarchy path with ">"-separated items.
 * Allows clicking any breadcrumb to navigate back to that folder.
 */
public class BreadcrumbAdapter extends BaseListAdapter<Breadcrumb, BreadcrumbAdapter.ViewHolder> {

    private final OnItemClickListener listener;  // Callback for breadcrumb clicks

    public interface OnItemClickListener {
        void onItemClick(Breadcrumb crumb);
    }

    public BreadcrumbAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_breadcrumb, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Breadcrumb crumb = items.get(position);
        holder.bind(crumb, listener, position != items.size() - 1);
    }

    /** ViewHolder for a single breadcrumb item */
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewBreadcrumb;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewBreadcrumb = itemView.findViewById(R.id.textViewBreadcrumb);
        }

        /** Bind breadcrumb data; append ">"-separator if not the last item */
        public void bind(Breadcrumb crumb, OnItemClickListener listener, boolean hasNext) {
            String text = crumb.getLabel() + (hasNext ? " >" : "");
            textViewBreadcrumb.setText(text);
            itemView.setOnClickListener(v -> listener.onItemClick(crumb));
        }
    }
}