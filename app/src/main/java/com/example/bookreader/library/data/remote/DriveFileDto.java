package com.example.bookreader.library.data.remote;

import java.time.Instant;  // Java 8 Date/Time API - represents an immutable moment in time (used for lastReadAt timestamp)

public class DriveFileDto {
    private final String id;
    private final String name;
    private final String mimeType;
    private final String parentId;
    private final Integer readingProgressPercent;
    private final Instant lastReadAt;

    public DriveFileDto(String id, String name, String mimeType, String parentId, Integer readingProgressPercent, Instant lastReadAt) {
        this.id = id;
        this.name = name;
        this.mimeType = mimeType;
        this.parentId = parentId;
        this.readingProgressPercent = readingProgressPercent;
        this.lastReadAt = lastReadAt;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getMimeType() { return mimeType; }
    public String getParentId() { return parentId; }
    public Integer getReadingProgressPercent() { return readingProgressPercent; }
    public Instant getLastReadAt() { return lastReadAt; }
}