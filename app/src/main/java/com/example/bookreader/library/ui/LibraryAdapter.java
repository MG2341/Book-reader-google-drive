package com.example.bookreader.library.ui;

import android.view.LayoutInflater;                  // Android Framework - converts XML layout files into View objects
import android.view.View;                            // Android Framework - base class for all UI widgets (used for visibility control)
import android.view.ViewGroup;                       // Android Framework - container for Views
import android.widget.ImageView;                     // Android Widget - displays images or icons
import android.widget.ProgressBar;                   // Android Widget - progress indicator (shows reading progress)
import android.widget.TextView;                      // Android Widget - displays text

import androidx.annotation.NonNull;                  // Androidx - marks parameters/methods that should never be null (used for RecyclerView contract)
import androidx.recyclerview.widget.RecyclerView;    // Android Jetpack - efficient list view that recycles items

import com.example.bookreader.R;                             // Android Resource binding - generated class with references to layout/drawable resources
import com.example.bookreader.library.data.model.LibraryItem;  // Domain model for a library item

import java.util.List;                               // Java Collections - interface for ordered collections

/**
 * RecyclerView adapter for displaying library items (folders and documents).
 * Shows different UI based on item type: folder icon for collections, document icon for files.
 * Displays reading progress for documents.
 */
public class LibraryAdapter extends BaseListAdapter<LibraryItem, LibraryAdapter.ViewHolder> {

    private final OnItemClickListener listener;  // Callback for item clicks

    public interface OnItemClickListener {
        void onItemClick(LibraryItem item);
    }

    public LibraryAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_library, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LibraryItem item = items.get(position);
        holder.bind(item, listener);
    }

    /** ViewHolder for a single library item row */
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTitle;        // Item name (folder or document)
        private final TextView textViewSubtitle;     // Reading progress or other info
        private final ImageView imageViewIcon;      // Folder or document icon
        private final ProgressBar progressBarReading;  // Reading progress bar (documents only)

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewSubtitle = itemView.findViewById(R.id.textViewSubtitle);
            imageViewIcon = itemView.findViewById(R.id.imageViewIcon);
            progressBarReading = itemView.findViewById(R.id.progressBarReading);
        }

        /** Bind item data to views; show different UI based on item type */
        public void bind(LibraryItem item, OnItemClickListener listener) {
            textViewTitle.setText(item.getTitle());

            if (item.isCollection()) {
                // Folder: show folder icon, hide progress
                imageViewIcon.setImageResource(android.R.drawable.ic_menu_agenda);
                textViewSubtitle.setVisibility(View.GONE);
                progressBarReading.setVisibility(View.GONE);
            } else if (item.isDocument()) {
                // Document: show file icon and reading progress if available
                imageViewIcon.setImageResource(android.R.drawable.ic_menu_view);
                
                if (item.getReadingProgressPercent() != null) {
                    progressBarReading.setVisibility(View.VISIBLE);
                    progressBarReading.setProgress(item.getReadingProgressPercent());
                    textViewSubtitle.setVisibility(View.VISIBLE);
                    textViewSubtitle.setText(item.getReadingProgressPercent() + "% read");
                } else {
                    progressBarReading.setVisibility(View.GONE);
                    textViewSubtitle.setVisibility(View.GONE);
                }
            } else {
                // Unknown type: show help icon
                imageViewIcon.setImageResource(android.R.drawable.ic_menu_help);
                textViewSubtitle.setVisibility(View.GONE);
                progressBarReading.setVisibility(View.GONE);
            }

            // Set click listener
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}