package com.example.bookreader.library.ui.state

/**
 * Navigation breadcrumb showing the folder hierarchy.
 * Clicking a breadcrumb navigates back to that folder.
 *
 * @param id Folder ID to navigate to, null represents "root" or library home
 * @param label Display text for this breadcrumb (e.g., "Documents", "2024 Books")
 */
data class Breadcrumb(
    val id: String? = null,
    val label: String,
)
