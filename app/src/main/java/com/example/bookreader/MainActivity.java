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
 * Main entry point activity.
 * Observes the LibraryViewModel and updates the UI based on the current state.
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

        // Initialize ViewModel with fake repository
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
            if (uiState instanceof LibraryUiState.Loading) {
                progressBar.setVisibility(View.VISIBLE);
                textViewError.setVisibility(View.GONE);
                recyclerViewItems.setVisibility(View.GONE);
            } else if (uiState instanceof LibraryUiState.Success) {
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
                LibraryUiState.Error errorState = (LibraryUiState.Error) uiState;
                progressBar.setVisibility(View.GONE);
                recyclerViewItems.setVisibility(View.GONE);
                textViewError.setVisibility(View.VISIBLE);
                textViewError.setText(errorState.getMessage());
            }
        });
    }
}
