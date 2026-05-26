package com.example.bookreader;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.github.barteksc.pdfviewer.PDFView;
import java.io.File;

/**
 * Activity for displaying PDF documents.
 *
 * Responsibilities:
 * - Loads and displays PDF files using the android-pdf-viewer library
 * - Shows progress indicator while PDF is loading
 * - Handles navigation (back button) to return to library
 *
 * Usage:
 * Intent intent = new Intent(context, PdfViewerActivity.class);
 * intent.putExtra(PdfViewerActivity.EXTRA_PDF_PATH, filePath);
 * intent.putExtra(PdfViewerActivity.EXTRA_PDF_TITLE, "Book Title");
 * startActivity(intent);
 */
public class PdfViewerActivity extends AppCompatActivity {

    // Intent extras keys
    public static final String EXTRA_PDF_PATH = "pdf_path";    // File path to the PDF
    public static final String EXTRA_PDF_TITLE = "pdf_title";  // Display title for the PDF

    // UI components
    private PDFView pdfView;          // PDF viewer library component
    private ProgressBar progressBar;  // Loading indicator

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        // Get the PDF path and title from the intent
        // Path: File location passed from MainActivity
        // Title: Document name to show in action bar
        Intent intent = getIntent();
        String pdfPath = intent.getStringExtra(EXTRA_PDF_PATH);
        String pdfTitle = intent.getStringExtra(EXTRA_PDF_TITLE);

        // Validate that we received a valid PDF path
        if (pdfPath == null) {
            Toast.makeText(this, "Invalid PDF path", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set the title in the action bar (displays document name)
        if (pdfTitle != null) {
            setTitle(pdfTitle);
        }

        // Bind UI components from layout
        pdfView = findViewById(R.id.pdfView);
        progressBar = findViewById(R.id.progressBar);

        // Load the PDF file
        loadPdf(pdfPath);
    }

    /**
     * Loads a PDF file from the given path and displays it.
     * Shows a loading indicator while the PDF is being rendered.
     *
     * @param pdfPath Absolute file path to the PDF (usually in cache directory)
     */
    private void loadPdf(String pdfPath) {
        // Show loading indicator
        progressBar.setVisibility(ProgressBar.VISIBLE);

        // Create a File object from the path
        File pdfFile = new File(pdfPath);

        // Verify the file exists
        if (!pdfFile.exists()) {
            progressBar.setVisibility(ProgressBar.GONE);
            Toast.makeText(this, "PDF file not found: " + pdfPath, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load and display the PDF
        // Configuration:
        // - .pages(0, 1) = display all pages (render as user scrolls)
        // - .enableSwipe(true) = allow swiping between pages
        // - .swipeHorizontal(false) = scroll vertically (not horizontally)
        // - .onLoad() = callback when rendering completes (hide loading indicator)
        pdfView.fromFile(pdfFile)
                .enableSwipe(true)  // Enable swipe navigation between pages
                .swipeHorizontal(false)  // Vertical scrolling (default for PDFs)
                .onLoad(nbPage -> {
                    // Callback when PDF finishes loading and rendering
                    progressBar.setVisibility(ProgressBar.GONE);
                    Toast.makeText(PdfViewerActivity.this, "PDF loaded: " + nbPage + " pages", Toast.LENGTH_SHORT).show();
                })
                .onError(t -> {
                    // Callback if PDF loading fails
                    progressBar.setVisibility(ProgressBar.GONE);
                    Toast.makeText(PdfViewerActivity.this, "Error loading PDF: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                })
                .load();
    }

    /**
     * Handle back button press - return to library
     * Called when user clicks the action bar back button or device back button
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
