package com.example.bookreader.library.data

import com.example.bookreader.library.data.model.LibraryItem
import com.example.bookreader.library.ui.state.Breadcrumb

interface LibraryRepository {
    suspend fun fetchFolderContents(collectionId: String?): List<LibraryItem>

    suspend fun fetchCollectionName(collectionId: String?): String

    suspend fun fetchBreadcrumbs(collectionId: String?): List<Breadcrumb>
}
