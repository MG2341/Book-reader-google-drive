package com.example.bookreader.library.ui;

import androidx.lifecycle.LiveData;                  // Android Jetpack - observable mutable state holder (survives configuration changes like device rotation)
import androidx.lifecycle.MutableLiveData;           // Mutable subclass of LiveData - allows setting values from data layer
import androidx.lifecycle.ViewModel;                 // Android Jetpack - base class that holds UI-related data that persists across configuration changes
import androidx.lifecycle.ViewModelProvider;        // Factory pattern - creates ViewModel instances with custom constructors

import com.example.bookreader.library.data.LibraryRepository;       // Data source abstraction interface
import com.example.bookreader.library.data.model.LibraryItem;       // Domain model for a library item
import com.example.bookreader.library.ui.state.Breadcrumb;          // Navigation breadcrumb model
import com.example.bookreader.library.ui.state.LibraryUiState;      // Sealed class representing all possible UI states

import java.util.List;                                               // Core Collections interface - holds ordered list
import java.util.concurrent.ExecutorService;                         // Java Concurrency - thread pool executor for background tasks
import java.util.concurrent.Executors;                               // Utility class - creates thread pools (newSingleThreadExecutor creates one background thread)

/**
 * Manages library screen state and coordinates data fetching.
 * Exposes UI state via LiveData—Activity observes and renders accordingly.
 * Loads folder contents and navigates between collections.
 */
public class LibraryViewModel extends ViewModel {

    private final LibraryRepository repository;                                          // Data source
    private final MutableLiveData<LibraryUiState> uiState = new MutableLiveData<>(new LibraryUiState.Loading());  // Observable state
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();  // Background thread for data fetching

    public LibraryViewModel(LibraryRepository repository) {
        this.repository = repository;
        // Load root folder on initialization
        loadCollection(null);
    }

    public LiveData<LibraryUiState> getUiState() {
        return uiState;
    }

    /**
     * Fetch and display contents of a folder.
     * Updates UI state: Loading → Success/Error
     * @param collectionId Folder ID to load, null for root
     */
    public void loadCollection(String collectionId) {
        // Show loading state immediately
        uiState.postValue(new LibraryUiState.Loading());
        
        // Fetch data on background thread
        executorService.submit(() -> {
            try {
                // Fetch items, folder name, and breadcrumbs
                List<LibraryItem> items = repository.fetchFolderContents(collectionId);
                String collectionName = repository.fetchCollectionName(collectionId);
                List<Breadcrumb> breadcrumbs = repository.fetchBreadcrumbs(collectionId);

                // Post success state back to main thread
                uiState.postValue(new LibraryUiState.Success(
                        items,
                        collectionId,
                        collectionName,
                        breadcrumbs
                ));
            } catch (Exception e) {
                // Post error state on failure
                uiState.postValue(new LibraryUiState.Error(
                        e.getMessage() != null ? e.getMessage() : "Unable to load library."
                ));
            }
        });
    }

    /** User clicked a folder item—navigate into it */
    public void onCollectionClicked(LibraryItem item) {
        if (item.isCollection()) {
            loadCollection(item.getId());
        }
    }

    /** User clicked a breadcrumb—navigate to that folder */
    public void onBreadcrumbClicked(Breadcrumb crumb) {
        loadCollection(crumb.getId());
    }

    /** Clean up background thread when ViewModel is destroyed */
    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }

    /** Factory for creating ViewModel with injected repository dependency */
    public static class Factory implements ViewModelProvider.Factory {
        private final LibraryRepository repository;

        public Factory(LibraryRepository repository) {
            this.repository = repository;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            if (modelClass.isAssignableFrom(LibraryViewModel.class)) {
                return (T) new LibraryViewModel(repository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
        }
    }
}