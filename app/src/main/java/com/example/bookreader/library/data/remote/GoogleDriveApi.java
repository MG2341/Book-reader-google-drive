package com.example.bookreader.library.data.remote;

import com.example.bookreader.library.ui.state.Breadcrumb;  // Navigation breadcrumb model
import java.util.List;                                       // Java Collections - interface for ordered collections

public interface GoogleDriveApi {
    List<DriveFileDto> listFolderContents(String folderId);
    String getCollectionName(String collectionId);
    List<Breadcrumb> getBreadcrumbs(String collectionId);
}