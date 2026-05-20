package com.example.bookreader.library.data

import com.example.bookreader.library.data.model.LibraryItem
import com.example.bookreader.library.data.remote.GoogleDriveApi
import com.example.bookreader.library.ui.state.Breadcrumb

class GoogleDriveLibraryRepository(
    private val driveApi: GoogleDriveApi,
) : LibraryRepository {

    override suspend fun fetchFolderContents(collectionId: String?): List<LibraryItem> {
        return driveApi.listFolderContents(collectionId)
            .map { file ->
                LibraryItem(
                    id = file.id,
                    title = file.name,
                    mimeType = file.mimeType,
                    parentId = file.parentId,
                    readingProgressPercent = file.readingProgressPercent,
                    lastReadAt = file.lastReadAt,
                )
            }
            .filter { it.isCollection() || it.isDocument() }
    }

    override suspend fun fetchCollectionName(collectionId: String?): String {
        return driveApi.getCollectionName(collectionId)
    }

    override suspend fun fetchBreadcrumbs(collectionId: String?): List<Breadcrumb> {
        return driveApi.getBreadcrumbs(collectionId)
    }
}
