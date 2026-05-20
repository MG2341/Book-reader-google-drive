package com.example.bookreader

import com.example.bookreader.library.data.LibraryRepository
import com.example.bookreader.library.data.model.LibraryItem
import com.example.bookreader.library.ui.state.Breadcrumb
import kotlinx.coroutines.delay
import java.time.Instant

class FakeLibraryRepository : LibraryRepository {

    override suspend fun fetchFolderContents(collectionId: String?): List<LibraryItem> {
        delay(300)
        return when (collectionId) {
            null -> listOf(
                LibraryItem(
                    id = "c1",
                    title = "Textbooks",
                    mimeType = "application/vnd.google-apps.folder",
                ),
                LibraryItem(
                    id = "c2",
                    title = "Currently Reading",
                    mimeType = "application/vnd.google-apps.folder",
                ),
                LibraryItem(
                    id = "d1",
                    title = "Kotlin in Depth",
                    mimeType = "application/pdf",
                    readingProgressPercent = 34,
                    lastReadAt = Instant.now().minusSeconds(3600),
                ),
                LibraryItem(
                    id = "d2",
                    title = "Clean Code",
                    mimeType = "application/pdf",
                    readingProgressPercent = 67,
                    lastReadAt = Instant.now().minusSeconds(86400),
                ),
            )
            "c1" -> listOf(
                LibraryItem(
                    id = "d3",
                    title = "Algorithms Design Manual",
                    mimeType = "application/pdf",
                    readingProgressPercent = 12,
                    lastReadAt = Instant.now().minusSeconds(604800),
                ),
                LibraryItem(
                    id = "d4",
                    title = "Modern Android Development",
                    mimeType = "application/epub+zip",
                    readingProgressPercent = 45,
                    lastReadAt = Instant.now().minusSeconds(1800),
                ),
            )
            else -> emptyList()
        }
    }

    override suspend fun fetchCollectionName(collectionId: String?): String {
        delay(100)
        return when (collectionId) {
            null -> "My Library"
            "c1" -> "Textbooks"
            "c2" -> "Currently Reading"
            else -> "Folder"
        }
    }

    override suspend fun fetchBreadcrumbs(collectionId: String?): List<Breadcrumb> {
        delay(100)
        return if (collectionId != null && collectionId != "null") {
            listOf(Breadcrumb(id = null, label = "My Library"))
        } else {
            emptyList()
        }
    }
}
