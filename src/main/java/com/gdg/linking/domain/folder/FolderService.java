package com.gdg.linking.domain.folder;

import com.gdg.linking.domain.folder.dto.FolderCreateRequest;
import com.gdg.linking.domain.folder.dto.FolderResponse;
import com.gdg.linking.domain.folder.dto.FolderUpdateRequest;

import java.util.List;

public interface FolderService {
    FolderResponse createFolder(Long userId, FolderCreateRequest request);

    List<FolderResponse> getFolders(Long userId);

    FolderResponse updateFolder(Long folderId, FolderUpdateRequest request);

    void deleteFolder(Long folderId);
}