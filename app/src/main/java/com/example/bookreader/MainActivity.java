package com.example.bookreader;

import android.os.Bundle;                           // Android Framework - contains key-value pairs for saving/restoring state
import android.view.View;                           // Android Framework - base class for all UI widgets (used for visibility constants)
import android.widget.ProgressBar;                  // Android Widget - spinning loading indicator
import android.widget.TextView;                     // Android Widget - displays text
import android.widget.Toast;                        // Android Widget - shows brief popup notification

import androidx.appcompat.app.AppCompatActivity;    // Android Jetpack AppCompat - backwards-compatible Activity base class with Material Design support
import androidx.lifecycle.ViewModelProvider;        // Android Jetpack - creates ViewModel instances
import androidx.recyclerview.widget.LinearLayoutManager;  // Android Jetpack RecyclerView - arranges items in a vertical or horizontal list
import androidx.recyclerview.widget.RecyclerView;   // Android Jetpack - efficient list view that recycles item views (replaces ListView)

import com.example.bookreader.library.ui.LibraryViewModel;       // ViewModel managing library state
import com.example.bookreader.library.ui.state.LibraryUiState;   // Sealed class for UI states
import com.example.bookreader.library.ui.LibraryAdapter;          // RecyclerView adapter for library items
import com.example.bookreader.library.ui.BreadcrumbAdapter;       // RecyclerView adapter for breadcrumbs

/**
 * Main entry point activity.
 * Sets up the ViewModel and displays the library with RecyclerViews.
 * Observes state changes and updates UI accordingly (show/hide spinner, list, or error).
 */
public class MainActivity extends AppCompatActivity {

    private LibraryViewModel viewModel;
    private LibraryAdapter libraryAdapter;
    private BreadcrumbAdapter breadcrumbAdapter;

    private RecyclerView recyclerViewItems;
    private RecyclerView recyclerViewBreadcrumbs;
    private ProgressBar progressBar;
    private TextView textViewError;
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

        // Initialize ViewModel with fake repository for now
        FakeLibraryRepository fakeRepo = new FakeLibraryRepository();
        LibraryViewModel.Factory factory = new LibraryViewModel.Factory(fakeRepo);
        viewModel = new ViewModelProvider(this, factory).get(LibraryViewModel.class);

        // Create adapters with item click listeners
        libraryAdapter = new LibraryAdapter(item -> {
            viewModel.onCollectionClicked(item);
            if (item.isDocument()) {
                Toast.makeText(this, "Open document: " + item.getId(), Toast.LENGTH_SHORT).show();
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
            // Show loading spinner
            if (uiState instanceof LibraryUiState.Loading) {
                progressBar.setVisibility(View.VISIBLE);
                textViewError.setVisibility(View.GONE);
                recyclerViewItems.setVisibility(View.GONE);
            } else if (uiState instanceof LibraryUiState.Success) {
                // Show data successfully loaded
                LibraryUiState.Success successState = (LibraryUiState.Success) uiState;
                progressBar.setVisibility(View.GONE);
                textViewError.setVisibility(View.GONE);
                recyclerViewItems.setVisibility(View.VISIBLE);

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
                // Show error message
                // Show error message
                LibraryUiState.Error errorState = (LibraryUiState.Error) uiState;
                progressBar.setVisibility(View.GONE);
                recyclerViewItems.setVisibility(View.GONE);
                textViewError.setVisibility(View.VISIBLE);
                textViewError.setText(errorState.getMessage());
            }
        });
    }
}