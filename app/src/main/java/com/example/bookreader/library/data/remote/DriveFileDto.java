package com.example.bookreader.library.data.remote;

import java.time.Instant;

/**
 * Data Transfer Object (DTO) representing a file/folder from Google Drive API response.
 *
 * Purpose:
 * - Intermediate data structure that mirrors Google Drive API response format
 * - Decouples API layer from domain logic (API changes don't affect app domain models)
 * - Contains raw metadata from Google Drive without transformation or filtering
 *
 * Lifecycle:
 * 1. GoogleDriveApi returns List<DriveFileDto> from API call
 * 2. GoogleDriveLibraryRepository converts each DriveFileDto → LibraryItem (domain model)
 * 3. Domain model (LibraryItem) is used by ViewModel and UI layer
 *
 * Why separate DTO and domain model?
 * - API contract (DTO) should be stable but may change independently of business logic
 * - Domain model (LibraryItem) represents business concepts and is optimized for app logic
 * - Example: If Google Drive API adds fields, DTO can change without affecting app logic
 */
public class DriveFileDto {
    // Unique identifier assigned by Google Drive - stable across API calls
    private final String id;

    // Display name of the file/folder - may contain spaces, special characters, non-ASCII
    private final String name;

    // MIME type determines what kind of file this is:
    // - "application/vnd.google-apps.folder" = folder/collection
    // - "application/pdf" = PDF document
    // - "application/epub+zip" = EPUB ebook
    // Used by domain model to determine item.isCollection() / item.isDocument()
    private final String mimeType;

    // ID of parent folder containing this file
    // null = this file is at root level (top of library)
    // Used to build breadcrumb navigation path
    private final String parentId;

    // Reading progress for documents (0-100 percentage)
    // null = not applicable (folders have no progress) or never read (0)
    // Used to display progress bars in library UI
    private final Integer readingProgressPercent;

    // Timestamp when user last opened this document
    // null = file has never been opened
    // Used to calculate "last read" display (e.g., "read 2 hours ago")
    private final Instant lastReadAt;

    public DriveFileDto(String id, String name, String mimeType, String parentId, Integer readingProgressPercent, Instant lastReadAt) {
        this.id = id;
        this.name = name;
        this.mimeType = mimeType;
        this.parentId = parentId;
        this.readingProgressPercent = readingProgressPercent;
        this.lastReadAt = lastReadAt;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getMimeType() { return mimeType; }
    public String getParentId() { return parentId; }
    public Integer getReadingProgressPercent() { return readingProgressPercent; }
    public Instant getLastReadAt() { return lastReadAt; }
}