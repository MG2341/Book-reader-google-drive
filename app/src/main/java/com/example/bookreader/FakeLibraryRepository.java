package com.example.bookreader;

import com.example.bookreader.library.data.LibraryRepository;       // Interface for data source abstraction
import com.example.bookreader.library.data.model.LibraryItem;       // Domain model for a library item
import com.example.bookreader.library.ui.state.Breadcrumb;          // Navigation breadcrumb model

import java.time.Instant;                                            // Java 8 Date/Time API - represents a moment in time (used for lastReadAt timestamps)
import java.util.ArrayList;                                          // Resizable array implementation (used to build dynamic lists)
import java.util.Arrays;                                             // Utility class for array operations (provides Arrays.asList() for creating fixed-size lists)
import java.util.List;                                               // Core Collections interface - represents an ordered collection

/**
 * Test implementation of LibraryRepository with hardcoded sample data.
 * Simulates network delays to mimic real Google Drive API behavior.
 */
public class FakeLibraryRepository implements LibraryRepository {

    @Override
    public List<LibraryItem> fetchFolderContents(String collectionId) {
        // Simulate network delay
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Return hardcoded data based on folder ID
        if (collectionId == null) {
            return Arrays.asList(
                    new LibraryItem("c1", "Textbooks", "application/vnd.google-apps.folder", null, null, null),
                    new LibraryItem("c2", "Currently Reading", "application/vnd.google-apps.folder", null, null, null),
                    new LibraryItem("d1", "Kotlin in Depth", "application/pdf", null, 34, Instant.now().minusSeconds(3600)),
                    new LibraryItem("d2", "Clean Code", "application/pdf", null, 67, Instant.now().minusSeconds(86400))
            );
        } else if ("c1".equals(collectionId)) {
            return Arrays.asList(
                    new LibraryItem("d3", "Algorithms Design Manual", "application/pdf", "c1", 12, Instant.now().minusSeconds(604800)),
                    new LibraryItem("d4", "Modern Android Development", "application/epub+zip", "c1", 45, Instant.now().minusSeconds(1800))
            );
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public String fetchCollectionName(String collectionId) {
        // Simulate network delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Return folder name based on ID
        if (collectionId == null) return "My Library";
        if ("c1".equals(collectionId)) return "Textbooks";
        if ("c2".equals(collectionId)) return "Currently Reading";
        return "Folder";
    }

    @Override
    public List<Breadcrumb> fetchBreadcrumbs(String collectionId) {
        // Simulate network delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Return breadcrumb path (only shows "My Library" for non-root folders)
        if (collectionId != null && !collectionId.equals("null")) {
            return Arrays.asList(new Breadcrumb(null, "My Library"));
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public String fetchDocumentContent(String fileId) {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if ("d1".equals(fileId)) {
            return "Kotlin in Depth\n\nChapter 1 — Getting started with Kotlin.\nKotlin is a modern JVM language that combines object-oriented and functional features.\n\n(This is placeholder content for the fake repository.)";
        } else if ("d2".equals(fileId)) {
            return "Clean Code\n\nChapter 3 — Functions.\nGood functions are small, do one thing, and have descriptive names.\n\n(This is placeholder content for the fake repository.)";
        } else if ("d3".equals(fileId)) {
            return "The Algorithm Design Manual\n\nChapter 2 — Algorithmic Techniques.\nAlgorithms are recipes to solve computational problems efficiently.\n\n(This is placeholder content for the fake repository.)";
        } else if ("d4".equals(fileId)) {
            return "Modern Android Development\n\nChapter 5 — Jetpack Compose Basics.\nCompose lets you build declarative UI with Kotlin.\n\n(This is placeholder content for the fake repository.)";
        } else {
            return "Content not available for this document.";
        }
    }
}