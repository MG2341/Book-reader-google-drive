package com.example.bookreader.library.ui.state;

import com.example.bookreader.library.data.model.LibraryItem;  // Domain model for a library item
import java.util.List;                                          // Core Collections interface - holds ordered list of items

/**
 * Represents all possible states of the library UI.
 * The UI renders differently based on which state is active (Loading, Success, or Error).
 */
public abstract class LibraryUiState {

    /** Loading state: data is being fetched from the repository */
    public static class Loading extends LibraryUiState {
    }

    /** Success state: library data is ready to display */
    public static class Success extends LibraryUiState {
        private final List<LibraryItem> items;                    // Files/folders in current collection
        private final String currentCollectionId;                 // Current folder ID (null for root)
        private final String currentCollectionName;               // Display name of current folder
        private final List<Breadcrumb> breadcrumbs;              // Navigation path to current location

        public Success(List<LibraryItem> items, String currentCollectionId, String currentCollectionName, List<Breadcrumb> breadcrumbs) {
            this.items = items;
            this.currentCollectionId = currentCollectionId;
            this.currentCollectionName = currentCollectionName;
            this.breadcrumbs = breadcrumbs;
        }

        public List<LibraryItem> getItems() { return items; }
        public String getCurrentCollectionId() { return currentCollectionId; }
        public String getCurrentCollectionName() { return currentCollectionName; }
        public List<Breadcrumb> getBreadcrumbs() { return breadcrumbs; }
    }

    /** Error state: something went wrong during data fetch */
    public static class Error extends LibraryUiState {
        private final String message;  // Error description to show user

        public Error(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
    }
}