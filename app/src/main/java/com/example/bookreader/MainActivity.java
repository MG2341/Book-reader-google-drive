package com.example.bookreader;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookreader.library.ui.BreadcrumbAdapter;
import com.example.bookreader.library.ui.LibraryAdapter;
import com.example.bookreader.library.ui.LibraryViewModel;
import com.example.bookreader.library.ui.state.LibraryUiState;

/**
 * Main entry point activity for the Book Reader application.
 *
 * Responsibilities:
 * - Manages the UI layout and lifecycle of library browsing screens
 * - Observes the LibraryViewModel and updates UI views based on state changes (Loading/Success/Error)
 * - Handles user interactions: folder navigation, breadcrumb clicks, and document opens
 * - Displays loading indicator, error messages, and library folder/file contents
 *
 * Data Flow:
 * 1. onCreate() initializes ViewModel with FakeLibraryRepository
 * 2. ViewModel exposes getUiState() LiveData observable
 * 3. observe() callback responds to state changes and updates UI views
 * 4. User clicks trigger adapter callbacks which invoke ViewModel methods (onCollectionClicked, onBreadcrumbClicked)
 * 5. ViewModel fetches data asynchronously and posts new state
 * 6. UI automatically re-renders based on new state (reactive architecture)
 */
public class MainActivity extends AppCompatActivity {

    // ViewModel: Holds and manages library browsing state (Loading/Success/Error)
    // Survives configuration changes (rotation, etc.) via Android lifecycle awareness
    private LibraryViewModel viewModel;

    // LibraryAdapter: Renders the list of folders and documents (files)
    // Listens for item clicks and forwards them to viewModel.onCollectionClicked()
    private LibraryAdapter libraryAdapter;

    // BreadcrumbAdapter: Renders the navigation path (e.g., "My Library > Textbooks > Chapter 1")
    // Listens for breadcrumb clicks to navigate back in folder hierarchy
    private BreadcrumbAdapter breadcrumbAdapter;

    // Main list RecyclerView: Displays library items (files and folders) in vertical scroll
    // Only visible during Success state; hidden during Loading/Error
    private RecyclerView recyclerViewItems;

    // Breadcrumb RecyclerView: Displays folder navigation path in horizontal scroll
    // Only visible if breadcrumbs list is non-empty in Success state; hidden during Loading/Error/root
    private RecyclerView recyclerViewBreadcrumbs;

    // Progress indicator: Spinning wheel shown during Loading state
    // Hidden during Success/Error states (view visibility toggled in observer callback)
    private ProgressBar progressBar;

    // Error message TextView: Displays error description when data fetch fails
    // Only visible during Error state; hidden during Loading/Success
    private TextView textViewError;

    // Folder name TextView: Displays the current collection/folder name (e.g., "My Library", "Textbooks")
    // Updated whenever user navigates to a different folder (Success state)
    private TextView textViewCollectionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind views from layout
        recyclerViewItems = findViewById(R.id.recyclerViewItems);
        recyclerViewBreadcrumbs = findViewById(R.id.recyclerViewBreadcrumbs);
        progressBar = findViewById(R.id.progressBar);
        textViewError = findViewById(R.id.textViewError);
        textViewCollectionName = findViewById(R.id.textViewCollectionName);

        // Configure layouts for both lists
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBreadcrumbs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Initialize ViewModel with fake repository
        FakeLibraryRepository fakeRepo = new FakeLibraryRepository(this);
        LibraryViewModel.Factory factory = new LibraryViewModel.Factory(fakeRepo);
        viewModel = new ViewModelProvider(this, factory).get(LibraryViewModel.class);

        // Create adapters with item click listeners
        libraryAdapter = new LibraryAdapter(item -> {
            viewModel.onCollectionClicked(item);
            if (item.isDocument()) {
                Toast.makeText(this, "Open document: " + item.getId(), Toast.LENGTH_SHORT).show();
//
            }
        });

        breadcrumbAdapter = new BreadcrumbAdapter(crumb -> {
            viewModel.onBreadcrumbClicked(crumb);
        });

        // Attach adapters to RecyclerViews
        recyclerViewItems.setAdapter(libraryAdapter);
        recyclerViewBreadcrumbs.setAdapter(breadcrumbAdapter);

        // Observe state changes and update UI
        viewModel.getUiState().observe(this, uiState -> {
            if (uiState instanceof LibraryUiState.Loading) {
                updateStateVisibility("loading");
            } else if (uiState instanceof LibraryUiState.Success) {
                LibraryUiState.Success successState = (LibraryUiState.Success) uiState;
                updateStateVisibility("success");

                // Update list and folder name
                libraryAdapter.submitList(successState.getItems());
                textViewCollectionName.setText(successState.getCurrentCollectionName());

                // Show/hide breadcrumbs based on presence
                if (successState.getBreadcrumbs().isEmpty()) {
                    recyclerViewBreadcrumbs.setVisibility(View.GONE);
                } else {
                    recyclerViewBreadcrumbs.setVisibility(View.VISIBLE);
                    breadcrumbAdapter.submitList(successState.getBreadcrumbs());
                }
            } else if (uiState instanceof LibraryUiState.Error) {
                LibraryUiState.Error errorState = (LibraryUiState.Error) uiState;
                updateStateVisibility("error");
                textViewError.setText(errorState.getMessage());
            }
        });
    }

    /** Helper method to update visibility of main UI elements based on current state */
    private void updateStateVisibility(String state) {
        boolean isLoading = state.equals("loading");
        boolean isError = state.equals("error");
        boolean isSuccess = state.equals("success");

        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        textViewError.setVisibility(isError ? View.VISIBLE : View.GONE);
        recyclerViewItems.setVisibility(isSuccess ? View.VISIBLE : View.GONE);
    }
}
