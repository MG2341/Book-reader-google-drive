package com.example.bookreader.library.data.model;

import java.util.Arrays;       // Java Utility - provides utility methods for arrays (Arrays.asList() converts array to List)
import java.util.HashSet;      // Java Collections - unordered collection of unique items (used for SUPPORTED_DOCUMENTS set)
import java.util.Set;          // Java Collections - interface for unordered unique collections

public class DriveMimeTypes {
    public static final String FOLDER = "application/vnd.google-apps.folder";
    public static final String PDF = "application/pdf";
    public static final String EPUB = "application/epub+zip";

    public static final Set<String> SUPPORTED_DOCUMENTS = new HashSet<>(Arrays.asList(PDF, EPUB));
}
