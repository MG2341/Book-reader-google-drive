package com.example.bookreader.library.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bookreader.library.data.LibraryRepository
import com.example.bookreader.library.data.model.LibraryItem
import com.example.bookreader.library.ui.state.Breadcrumb
import com.example.bookreader.library.ui.state.LibraryUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Manages library screen state and data fetching.
 * Exposes UI state via StateFlow—UI observes and renders accordingly.
 *
 * @param repository Data source for library contents and metadata
 */
class LibraryViewModel(
    private val repository: LibraryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<LibraryUiState>(LibraryUiState.Loading)
    /** Public read-only flow of current UI state. */
    val uiState: StateFlow<LibraryUiState> = _uiState

    init {
        loadCollection(null)
    }

    /**
     * Fetch and display contents of a folder.
     * Updates UI state (Loading → Success/Error).
     *
     * @param collectionId Folder ID to load, null for root
     */
    fun loadCollection(collectionId: String?) {
        viewModelScope.launch {
            _uiState.value = LibraryUiState.Loading
            try {
                val items = repository.fetchFolderContents(collectionId)
                val collectionName = repository.fetchCollectionName(collectionId)
                val breadcrumbs = repository.fetchBreadcrumbs(collectionId)

                _uiState.value = LibraryUiState.Success(
                    items = items,
                    currentCollectionId = collectionId,
                    currentCollectionName = collectionName,
                    breadcrumbs = breadcrumbs,
                )
            } catch (e: Exception) {
                _uiState.value = LibraryUiState.Error(
                    message = e.message ?: "Unable to load library."
                )
            }
        }
    }

    /** User clicked a folder item—navigate into it. */
    fun onCollectionClicked(item: LibraryItem) {
        if (item.isCollection()) {
            loadCollection(item.id)
        }
    }

    /** User clicked a breadcrumb—navigate to that folder. */
    fun onBreadcrumbClicked(crumb: Breadcrumb) {
        loadCollection(crumb.id)
    }

    /**
     * Factory for creating ViewModel with injected repository.
     * Required because ViewModel has constructor parameters.
     */
    class Factory(
        private val repository: LibraryRepository,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LibraryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LibraryViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
