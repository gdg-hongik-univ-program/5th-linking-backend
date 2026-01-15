package com.gdg.linking.domain.folder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {

    List<Folder> findByUser_UserId(Long userId); // 특정 사용자의 폴더 목록만 가져오는 기능
}
