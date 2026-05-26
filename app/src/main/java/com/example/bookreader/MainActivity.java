package com.example.bookreader;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookreader.library.data.LibraryRepository;
import com.example.bookreader.library.ui.BreadcrumbAdapter;
import com.example.bookreader.library.ui.LibraryAdapter;
import com.example.bookreader.library.ui.LibraryViewModel;
import com.example.bookreader.library.ui.state.LibraryUiState;

import java.io.File;
import java.util.concurrent.Executors;

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

    // Enum for type-safe UI state visibility management
    private enum UiStateVisibility {
        LOADING, SUCCESS, ERROR
    }

    private LibraryViewModel viewModel;
    private LibraryAdapter libraryAdapter;
    private BreadcrumbAdapter breadcrumbAdapter;
    private RecyclerView recyclerViewItems;
    private RecyclerView recyclerViewBreadcrumbs;
    private ProgressBar progressBar;
    private TextView textViewError;
    private TextView textViewCollectionName;
    private LibraryRepository repository;

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
        this.repository = fakeRepo;  // Store repository reference for document fetching
        LibraryViewModel.Factory factory = new LibraryViewModel.Factory(fakeRepo);
        viewModel = new ViewModelProvider(this, factory).get(LibraryViewModel.class);

        // Create adapters with item click listeners
        libraryAdapter = new LibraryAdapter(item -> {
            viewModel.onCollectionClicked(item);
            // When user clicks a document, fetch and open it in PDF viewer
            if (item.isDocument()) {
                openDocument(item);
            }
        });

        breadcrumbAdapter = new BreadcrumbAdapter(crumb -> {
            viewModel.onBreadcrumbClicked(crumb);
        });

        // Attach adapters to RecyclerViews
        recyclerViewItems.setAdapter(libraryAdapter);
        recyclerViewBreadcrumbs.setAdapter(breadcrumbAdapter);

        // Observe state changes and update UI
        observeLibraryState();
    }

    /** Observe ViewModel state changes and update UI accordingly */
    private void observeLibraryState() {
        viewModel.getUiState().observe(this, this::handleUiStateChange);
    }

    /** Handle UI state changes from ViewModel */
    private void handleUiStateChange(LibraryUiState uiState) {
        if (uiState instanceof LibraryUiState.Loading) {
            updateStateVisibility(UiStateVisibility.LOADING);
        } else if (uiState instanceof LibraryUiState.Success) {
            handleSuccessState((LibraryUiState.Success) uiState);
        } else if (uiState instanceof LibraryUiState.Error) {
            handleErrorState((LibraryUiState.Error) uiState);
        }
    }

    /** Handle success state: update lists and folder name */
    private void handleSuccessState(LibraryUiState.Success successState) {
        updateStateVisibility(UiStateVisibility.SUCCESS);
        libraryAdapter.submitList(successState.getItems());
        textViewCollectionName.setText(successState.getCurrentCollectionName());

        if (successState.getBreadcrumbs().isEmpty()) {
            recyclerViewBreadcrumbs.setVisibility(View.GONE);
        } else {
            recyclerViewBreadcrumbs.setVisibility(View.VISIBLE);
            breadcrumbAdapter.submitList(successState.getBreadcrumbs());
        }
    }

    /** Handle error state: display error message */
    private void handleErrorState(LibraryUiState.Error errorState) {
        updateStateVisibility(UiStateVisibility.ERROR);
        textViewError.setText(errorState.getMessage());
    }

    /** Open a document (PDF) in the PDF viewer activity */
    private void openDocument(com.example.bookreader.library.data.model.LibraryItem item) {
        Toast.makeText(this, "Opening: " + item.getTitle(), Toast.LENGTH_SHORT).show();

        Executors.newSingleThreadExecutor().execute(() -> {
            File documentFile = repository.fetchDocumentContent(item.getId());
            runOnUiThread(() -> {
                if (documentFile != null && documentFile.exists()) {
                    Intent intent = new Intent(MainActivity.this, PdfViewerActivity.class);
                    intent.putExtra(PdfViewerActivity.EXTRA_PDF_PATH, documentFile.getAbsolutePath());
                    intent.putExtra(PdfViewerActivity.EXTRA_PDF_TITLE, item.getTitle());
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Failed to load document", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    /** Update visibility of UI elements based on current state */
    private void updateStateVisibility(UiStateVisibility state) {
        boolean isLoading = state == UiStateVisibility.LOADING;
        boolean isError = state == UiStateVisibility.ERROR;
        boolean isSuccess = state == UiStateVisibility.SUCCESS;

        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        textViewError.setVisibility(isError ? View.VISIBLE : View.GONE);
        recyclerViewItems.setVisibility(isSuccess ? View.VISIBLE : View.GONE);
    }
}
