package com.example.bookreader.feature.library.model;

/**
 * Navigation breadcrumb showing the folder hierarchy.
 * Clicking a breadcrumb navigates back to that folder.
 *
 * @param id Folder ID to navigate to, null represents "root" or library home
 * @param label Display text for this breadcrumb (e.g., "Documents", "2024 Books")
 */
public class Breadcrumb {
    private final String id;
    private final String label;

    public Breadcrumb(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }
}