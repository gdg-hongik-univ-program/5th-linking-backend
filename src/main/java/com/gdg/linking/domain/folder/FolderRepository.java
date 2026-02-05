package com.gdg.linking.domain.folder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {

    List<Folder> findByUser_UserId(Long userId); // 특정 사용자의 폴더 목록만 가져오는 기능



    // LEFT JOIN을 사용하여 아이템이 없는 폴더도 0개로 집계되도록 함
    @Query("SELECT f.fId, COUNT(i) FROM Folder f LEFT JOIN f.items i WHERE f.user.userId = :userId GROUP BY f.fId")
    List<Object[]> countItemsByUserId(@Param("userId") Long userId);


}
