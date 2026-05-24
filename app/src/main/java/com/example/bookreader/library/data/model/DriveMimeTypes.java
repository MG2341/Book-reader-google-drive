package com.example.bookreader.library.data.model;

/**
 * Constants for Google Drive MIME types supported by the book reader app.
 *
 * Purpose:
 * - Centralize MIME type strings to avoid typos and duplication
 * - Define which file types the app can display/handle
 * - Allow easy expansion to support new formats (add constants, update SUPPORTED_DOCUMENTS)
 *
 * Supported Types:
 * 1. Folders: "application/vnd.google-apps.folder"
 *    - Created by Google Drive for organizing files
 *    - App treats folders as "collections" for navigation
 *    - Shows folder icon in UI, allows clicking to navigate inside
 *
 * 2. PDF: "application/pdf"
 *    - Standard document format widely available
 *    - Usually used for textbooks, technical papers, finished books
 *    - Display: document icon + reading progress bar in library UI
 *
 * 3. EPUB: "application/epub+zip"
 *    - Open standard ebook format (Electronic Publication)
 *    - Designed for reflowable content (adapts to screen size)
 *    - Common format for digital books on Google Play Books, Kobo, etc.
 *    - Display: document icon + reading progress bar in library UI
 *
 * Unsupported Types (filtered out by LibraryRepository):
 * - application/vnd.openxmlformats-officedocument.wordprocessingml.document (Word .docx)
 * - application/vnd.ms-excel (Excel spreadsheet)
 * - application/vnd.google-apps.spreadsheet (Google Sheets)
 * - text/plain, text/html (plain text / HTML files)
 * - Reason: Book reader app focuses on formatted documents, not generic office files
 */
public class DriveMimeTypes {
    // Google Drive MIME type for folders (collections used for navigation)
    public static final String FOLDER = "application/vnd.google-apps.folder";

    // MIME type for PDF documents
    // Standard format: ISO 32000 specification
    public static final String PDF = "application/pdf";

    // MIME type for EPUB ebooks
    // Standard format: Open EPUB specification (IDPF)
    // Note: ZIP-compressed container format with content.opf manifest
    public static final String EPUB = "application/epub+zip";

    // Set of all document types the app can display (folders are handled separately)
    // Used by LibraryRepository to filter: only items in this set are shown as documents
    // Items outside this set (even if files) are excluded from library display
    public static final Set<String> SUPPORTED_DOCUMENTS = new HashSet<>(Arrays.asList(PDF, EPUB));
}
