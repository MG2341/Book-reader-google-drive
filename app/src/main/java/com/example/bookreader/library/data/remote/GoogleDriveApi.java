package com.example.bookreader.library.data.remote;

/**
 * Low-level interface for Google Drive API calls.
 *
 * Purpose:
 * - Abstracts Google Drive API details from business logic
 * - Allows testing with fake implementations (FakeLibraryRepository)
 * - Central contract for all drive API interactions
 *
 * Implementation Notes:
 * - Returns DTOs (Data Transfer Objects) that mirror API response format
 * - Handles authentication, network errors, and rate limiting (in real implementation)
 * - Methods correspond 1:1 with Google Drive REST API endpoints
 */
public interface GoogleDriveApi {
    /**
     * Fetch all files and folders in a specific Google Drive folder.
     * @param folderId ID of folder to list contents from. null = root folder.
     * @return List of files/folders in that folder (may be empty if folder is empty).
     *         Each item includes ID, name, MIME type, and metadata (progress, last read time).
     */
    List<DriveFileDto> listFolderContents(String folderId);

    /**
     * Get the display name of a folder.
     * @param collectionId ID of folder to fetch name for. null = root folder.
     * @return User-friendly display name (e.g., "My Documents", "Textbooks").
     */
    String getCollectionName(String collectionId);

    /**
     * Get the navigation breadcrumb path to a folder.
     * @param collectionId ID of folder to get breadcrumbs for. null = root folder.
     * @return List of breadcrumbs showing folder hierarchy (e.g., [Home, Documents, Physics]).
     *         Empty list if at root. Can be clicked to navigate back up the hierarchy.
     */
    List<Breadcrumb> getBreadcrumbs(String collectionId);
}