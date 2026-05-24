package com.example.bookreader;

import com.example.bookreader.library.data.LibraryRepository;       // Interface for data source abstraction
import com.example.bookreader.library.data.model.LibraryItem;       // Domain model for a library item
import com.example.bookreader.library.ui.state.Breadcrumb;          // Navigation breadcrumb model

import java.time.Instant;                                            // Java 8 Date/Time API - represents a moment in time (used for lastReadAt timestamps)
import java.util.ArrayList;                                          // Resizable array implementation (used to build dynamic lists)
import java.util.Arrays;                                             // Utility class for array operations (provides Arrays.asList() for creating fixed-size lists)
import java.util.List;                                               // Core Collections interface - represents an ordered collection

/**
 * Test/Mock implementation of LibraryRepository with hardcoded sample library data.
 *
 * Purpose:
 * - Allows development and UI testing without real Google Drive authentication/API access
 * - Simulates network delays (Thread.sleep) to test Loading state UI behavior
 * - Provides consistent test data across development sessions (no external dependencies)
 *
 * Test Data Structure:
 * Root folder (collectionId=null):
 *   ├─ c1: "Textbooks" (folder)
 *   ├─ c2: "Currently Reading" (folder)
 *   ├─ c3: "test" (folder)
 *   ├─ d1: "Kotlin in Depth" (PDF, 34% progress, read 1 hour ago)
 *   └─ d2: "Clean Code" (PDF, 67% progress, read 1 day ago)
 *
 * Textbooks folder (c1):
 *   ├─ d3: "Algorithms Design Manual" (PDF, 12% progress, read 1 week ago)
 *   └─ d4: "Modern Android Development" (EPUB, 45% progress, read 30 minutes ago)
 *
 * Empty folders: c2, c3 (Currently Reading, test) return no items
 *
 * Design Notes:
 * - Uses realistic progress percentages and timestamps to test UI formatting
 * - Simulates various read states: never read (null), partially read, heavily read
 * - Tests both PDF and EPUB MIME types
 * - Thread.sleep() delays simulate real network latency (~100-300ms)
 */
public class FakeLibraryRepository implements LibraryRepository {

    @Override
    public List<LibraryItem> fetchFolderContents(String collectionId) {
        // Simulate network latency (real API would have ~300ms delay)
        // This allows testing the Loading state UI and ensures async behavior
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Return hardcoded test data based on folder ID
        // Each folder contains specific items for testing different scenarios
        if (collectionId == null) {
            // ROOT FOLDER: Mix of folders and documents at top level
            // Tests: folder icons, document icons, progress bars, various progress percentages
            return Arrays.asList(
                    // Folder c1: "Textbooks" - empty test folder
                    new LibraryItem("c1", "Textbooks", "application/vnd.google-apps.folder", null, null, null),
                    // Folder c2: "Currently Reading" - will contain items user is actively reading
                    new LibraryItem("c2", "Currently Reading", "application/vnd.google-apps.folder", null, null, null),
                    // Folder c3: "test" - empty folder for miscellaneous testing
                    new LibraryItem("c3", "test", "application/vnd.google-apps.folder", null, null, null),
                    // Document d1: "Kotlin in Depth" - 34% progress, PDF format
                    // readingProgressPercent=34: tests progress bar UI at ~middle progress
                    // lastReadAt=now-1 hour: tests "recently read" calculation
                    new LibraryItem("d1", "Kotlin in Depth", "application/pdf", null, 34, Instant.now().minusSeconds(3600)),
                    // Document d2: "Clean Code" - 67% progress, PDF format
                    // readingProgressPercent=67: tests progress bar UI at advanced progress
                    // lastReadAt=now-1 day: tests "last read" timestamp formatting
                    new LibraryItem("d2", "Clean Code", "application/pdf", null, 67, Instant.now().minusSeconds(86400))
            );
        } else if ("c1".equals(collectionId)) {
            // TEXTBOOKS FOLDER: Contains technical book examples
            // Tests: nested folder navigation, different progress levels, EPUB format
            return Arrays.asList(
                    // Document d3: "Algorithms Design Manual" - 12% progress, PDF format
                    // readingProgressPercent=12: tests progress bar at very early stage
                    // parentId="c1": indicates this item belongs to Textbooks folder
                    // lastReadAt=now-1 week: tests old "last read" timestamps
                    new LibraryItem("d3", "Algorithms Design Manual", "application/pdf", "c1", 12, Instant.now().minusSeconds(604800)),
                    // Document d4: "Modern Android Development" - 45% progress, EPUB format
                    // MIME type "application/epub+zip": tests EPUB support (different from PDF)
                    // readingProgressPercent=45: tests mid-range progress display
                    // lastReadAt=now-30 min: tests recently read document (more recent than d1)
                    new LibraryItem("d4", "Modern Android Development", "application/epub+zip", "c1", 45, Instant.now().minusSeconds(1800))
            );
        } else {
            // ALL OTHER FOLDERS: Empty (c2, c3, and invalid IDs)
            // Tests: empty state UI, folder navigation to empty folders
            return new ArrayList<>();
        }
    }

    @Override
    public String fetchCollectionName(String collectionId) {
        // Simulate network delay for folder name lookup (~100ms)
        // Tests that UI correctly shows Loading spinner during metadata fetch
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Return folder display name based on ID (user-friendly strings shown in UI)
        // These names must match the LibraryItem titles created in fetchFolderContents()
        if (collectionId == null) return "My Library";  // Root folder display name
        if ("c1".equals(collectionId)) return "Textbooks";  // Matches c1 folder title
        if ("c2".equals(collectionId)) return "Currently Reading";  // Matches c2 folder title
        return "Folder";  // Default fallback for unknown folder IDs
    }

    @Override
    public List<Breadcrumb> fetchBreadcrumbs(String collectionId) {
        // Simulate network delay for breadcrumb path lookup (~100ms)
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Build navigation breadcrumb path for current folder
        // Breadcrumbs show the hierarchy: "My Library > Textbooks" allows jumping back to parents
        // Root folder (collectionId=null) has no breadcrumbs (already at top)
        // Non-root folders show breadcrumbs with "My Library" as parent link
        if (collectionId != null && !collectionId.equals("null")) {
            // Non-root folder: show parent breadcrumb
            // Breadcrumb(id=null, label="My Library") allows clicking to return to root
            return Arrays.asList(new Breadcrumb(null, "My Library"));
        } else {
            // Root folder: empty breadcrumb list (no parents to navigate to)
            return new ArrayList<>();
        }
    }

    @Override
    public String fetchDocumentContent(String fileId) {
        // Simulate network delay for document content fetch (~200ms)
        // In real implementation, this would download file from Google Drive
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Return placeholder content for specific test documents
        // Content format: Title + chapters/sections to simulate real document
        // Each document has different content to test reading progress with various texts
        if ("d1".equals(fileId)) {
            // Kotlin in Depth content - programming technical content
            return "Kotlin in Depth\n\nChapter 1 — Getting started with Kotlin.\nKotlin is a modern JVM language that combines object-oriented and functional features.\n\n(This is placeholder content for the fake repository.)";
        } else if ("d2".equals(fileId)) {
            // Clean Code content - software engineering best practices
            return "Clean Code\n\nChapter 3 — Functions.\nGood functions are small, do one thing, and have descriptive names.\n\n(This is placeholder content for the fake repository.)";
        } else if ("d3".equals(fileId)) {
            // Algorithm Design Manual content - computer science algorithms
            return "The Algorithm Design Manual\n\nChapter 2 — Algorithmic Techniques.\nAlgorithms are recipes to solve computational problems efficiently.\n\n(This is placeholder content for the fake repository.)";
        } else if ("d4".equals(fileId)) {
            // Modern Android Development content - Android UI framework
            return "Modern Android Development\n\nChapter 5 — Jetpack Compose Basics.\nCompose lets you build declarative UI with Kotlin.\n\n(This is placeholder content for the fake repository.)";
        } else {
            // Unknown document ID: return generic "not found" message
            return "Content not available for this document.";
        }
    }
}