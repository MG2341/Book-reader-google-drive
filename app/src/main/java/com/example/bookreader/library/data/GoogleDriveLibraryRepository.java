package com.example.bookreader.library.data;

import com.example.bookreader.library.data.model.LibraryItem;           // Domain model for a library item
import com.example.bookreader.library.data.remote.DriveFileDto;         // Google Drive API response DTO (Data Transfer Object)
import com.example.bookreader.library.data.remote.GoogleDriveApi;       // Interface for Google Drive API calls
import com.example.bookreader.library.ui.state.Breadcrumb;              // Navigation breadcrumb model

import java.util.ArrayList;                                              // Resizable array implementation (builds dynamic lists)
import java.util.List;                                                   // Core Collections interface - ordered collection

/**
 * Fetches library data from the real Google Drive API.
 * Filters out unsupported file types and maps Google Drive DTOs to domain models.
 */
public class GoogleDriveLibraryRepository implements LibraryRepository {

    private final GoogleDriveApi driveApi;

    public GoogleDriveLibraryRepository(GoogleDriveApi driveApi) {
        this.driveApi = driveApi;
    }

    @Override
    public List<LibraryItem> fetchFolderContents(String collectionId) {
        // Fetch raw data from API and convert DTOs to domain model
        List<DriveFileDto> files = driveApi.listFolderContents(collectionId);
        List<LibraryItem> result = new ArrayList<>();
        for (DriveFileDto file : files) {
            LibraryItem item = new LibraryItem(
                    file.getId(),
                    file.getName(),
                    file.getMimeType(),
                    file.getParentId(),
                    file.getReadingProgressPercent(),
                    file.getLastReadAt()
            );
            // Only include supported types (folders and known document formats)
            if (item.isCollection() || item.isDocument()) {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public String fetchCollectionName(String collectionId) {
        return driveApi.getCollectionName(collectionId);
    }

    @Override
    public List<Breadcrumb> fetchBreadcrumbs(String collectionId) {
        return driveApi.getBreadcrumbs(collectionId);
    }

    @Override
    public String fetchDocumentContent(String fileId) {
        // TODO: Implement actual file download from Google Drive API
        // Currently returns placeholder message
        return "Content loading not implemented for Google Drive repository.";
    }
}