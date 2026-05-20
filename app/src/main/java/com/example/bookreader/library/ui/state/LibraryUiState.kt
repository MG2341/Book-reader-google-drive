package com.example.bookreader.library.ui.state

import com.example.bookreader.library.data.model.LibraryItem

/**
 * Sealed class representing all possible UI states for the library screen.
 * The UI renders based on which state is active, ensuring type safety.
 */
sealed class LibraryUiState {
    /** Loading state: data is being fetched. */
    data object Loading : LibraryUiState()

    /**
     * Success state: library data is ready to display.
     *
     * @param items List of files/folders in current collection
     * @param currentCollectionId Folder ID we're viewing, null for root
     * @param currentCollectionName Display name of current folder
     * @param breadcrumbs Navigation path to current location
     */
    data class Success(
        val items: List<LibraryItem>,
        val currentCollectionId: String? = null,
        val currentCollectionName: String,
        val breadcrumbs: List<Breadcrumb>,
    ) : LibraryUiState()

    /**
     * Error state: something went wrong.
     *
     * @param message Error description to show user
     */
    data class Error(
        val message: String,
    ) : LibraryUiState()
}
