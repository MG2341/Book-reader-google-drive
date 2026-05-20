package com.example.bookreader.library.data.remote

import java.time.Instant

data class DriveFileDto(
    val id: String,
    val name: String,
    val mimeType: String,
    val parentId: String? = null,
    val readingProgressPercent: Int? = null,
    val lastReadAt: Instant? = null,
)
