package com.gdg.linking.domain.folder;

import com.gdg.linking.domain.folder.dto.*;

import java.util.List;

public interface FolderService {
    FolderResponse createFolder(Long userId, FolderCreateRequest request);

    List<FolderResponse> getFolders(Long userId);

    FolderResponse updateFolder(Long folderId, FolderUpdateRequest request);

    void deleteFolder(Long folderId);

    void moveFolders(FolderMoveRequest request, Long userId);

    void restoreItem(Long itemId, Long userId);

    void deleteFolders(FolderDeleteRequest request, Long userId);

}
