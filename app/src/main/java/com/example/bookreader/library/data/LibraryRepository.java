package com.example.bookreader.library.data;

import com.example.bookreader.library.data.model.LibraryItem;  // Domain model representing a library item (file/folder)
import com.example.bookreader.library.ui.state.Breadcrumb;     // UI state model for navigation breadcrumbs
import java.util.List;                                          // Core Java Collections - holds ordered collections of items

/**
 * Data source abstraction for library contents and metadata.
 * Implementations can fetch from Google Drive or use fake data for testing.
 */
public interface LibraryRepository {
    /** Fetch files and folders in a given collection/folder */
    List<LibraryItem> fetchFolderContents(String collectionId);
    
    /** Get the display name of a collection */
    String fetchCollectionName(String collectionId);
    
    /** Get the navigation breadcrumb path */
    List<Breadcrumb> fetchBreadcrumbs(String collectionId);
    
    /** Fetch the textual content of a document for reader display */
    String fetchDocumentContent(String fileId);
}