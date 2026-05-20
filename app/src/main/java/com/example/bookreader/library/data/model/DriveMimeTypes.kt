package com.example.bookreader.library.data.model

object DriveMimeTypes {
    const val FOLDER = "application/vnd.google-apps.folder"
    const val PDF = "application/pdf"
    const val EPUB = "application/epub+zip"

    val SUPPORTED_DOCUMENTS = setOf(PDF, EPUB)
}
