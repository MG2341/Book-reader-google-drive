package com.example.bookreader.library.data.model;

import java.time.Instant;  // Java 8 Date/Time API - represents an immutable moment in time on the timeline (used for lastReadAt)

/**
 * Represents a single item in the library (file or folder from Google Drive).
 * Automatically determines its kind (Collection, Document, or Unsupported) based on MIME type.
 */
public class LibraryItem {
    private final String id;                          // Unique identifier from Google Drive
    private final String title;                        // Display name of the file/folder
    private final String mimeType;                     // MIME type (determines if folder or document)
    private final String parentId;                     // ID of parent folder, null if root
    private final Integer readingProgressPercent;      // 0-100 for documents, null if not applicable
    private final Instant lastReadAt;                  // When this was last opened, null if never

    public LibraryItem(String id, String title, String mimeType, String parentId, Integer readingProgressPercent, Instant lastReadAt) {
        this.id = id;
        this.title = title;
        this.mimeType = mimeType;
        this.parentId = parentId;
        this.readingProgressPercent = readingProgressPercent;
        this.lastReadAt = lastReadAt;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getMimeType() { return mimeType; }
    public String getParentId() { return parentId; }
    public Integer getReadingProgressPercent() { return readingProgressPercent; }
    public Instant getLastReadAt() { return lastReadAt; }

    /**
     * Determines the kind of library item based on its MIME type.
     * @return The item's kind (Collection, Document, or Unsupported)
     */
    public LibraryItemKind getKind() {
        if (DriveMimeTypes.FOLDER.equals(mimeType)) {
            return LibraryItemKind.Collection;
        } else if (DriveMimeTypes.SUPPORTED_DOCUMENTS.contains(mimeType)) {
            return LibraryItemKind.Document;
        } else {
            return LibraryItemKind.Unsupported;
        }
    }

    public boolean isCollection() {
        return getKind() == LibraryItemKind.Collection;
    }

    public boolean isDocument() {
        return getKind() == LibraryItemKind.Document;
    }
}
