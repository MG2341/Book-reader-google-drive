package com.example.bookreader.library.data.model

import java.time.Instant

/**
 * Represents a single item in the library (file or folder from Google Drive).
 * Automatically determines its kind based on MIME type.
 *
 * @param id Unique identifier from Google Drive
 * @param title Display name of the file/folder
 * @param mimeType MIME type (determines if it's a folder or document)
 * @param parentId ID of parent folder, null if root
 * @param readingProgressPercent 0-100 for documents, null if not applicable
 * @param lastReadAt When this was last opened, null if never
 */
data class LibraryItem(
    val id: String,
    val title: String,
    val mimeType: String,
    val parentId: String? = null,
    val readingProgressPercent: Int? = null,
    val lastReadAt: Instant? = null,
) {
    val kind: LibraryItemKind
        get() = when {
            mimeType == DriveMimeTypes.FOLDER -> LibraryItemKind.Collection
            mimeType in DriveMimeTypes.SUPPORTED_DOCUMENTS -> LibraryItemKind.Document
            else -> LibraryItemKind.Unsupported
        }

    fun isCollection() = kind == LibraryItemKind.Collection
    fun isDocument() = kind == LibraryItemKind.Document
}
