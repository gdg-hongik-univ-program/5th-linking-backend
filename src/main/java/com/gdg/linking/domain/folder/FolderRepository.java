package com.gdg.linking.domain.folder;

import com.gdg.linking.domain.item.Item;
import com.gdg.linking.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {

    // 특정 사용자의 폴더 목록만 가져오는 기능
    List<Folder> findByUser_UserIdAndStatus(Long userId, Item.ItemStatus status);

    // 아이템 개수를 집계할 때, 휴지통(TRASH)에 있는 아이템은 제외하고 카운트
    @Query("SELECT f.fId, COUNT(i) FROM Folder f " +
            "LEFT JOIN f.items i ON i.status = 'ACTIVE' " + // ACTIVE 상태인 아이템만 연결
            "WHERE f.user.userId = :userId " +
            "GROUP BY f.fId")
    List<Object[]> countItemsByUserId(@Param("userId") Long userId);


    //fid와 userId로 탐색
    @Query("SELECT f FROM Folder f WHERE f.fId = :fId AND f.user = :user")
    Optional<Folder> findByFIdAndUser(@Param("fId") Long fId, @Param("user") User user);
}
