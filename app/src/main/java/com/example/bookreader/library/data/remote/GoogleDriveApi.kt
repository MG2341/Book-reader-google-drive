package com.example.bookreader.library.data.remote

import com.example.bookreader.library.ui.state.Breadcrumb

interface GoogleDriveApi {
    suspend fun listFolderContents(folderId: String?): List<DriveFileDto>

    suspend fun getCollectionName(collectionId: String?): String

    suspend fun getBreadcrumbs(collectionId: String?): List<Breadcrumb>
}
