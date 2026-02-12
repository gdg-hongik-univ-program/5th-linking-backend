package com.gdg.linking.domain.tag;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface TagRepository extends JpaRepository<Tag,Long> {

    // 태그 이름으로 조회 (Optional로 반환하여 존재 여부 확인)
    Optional<Tag> findByTagName(String tagName);

    @Query("SELECT t.tagName, COUNT(it) as tagCount " +
            "FROM Tag t JOIN t.postTags it " +
            "WHERE it.item.user.userId = :userId " +
            "GROUP BY t.tagId, t.tagName " +
            "ORDER BY tagCount DESC")
    List<Object[]> findTop5TagsByUserId(@Param("userId") Long userId, Pageable pageable);
}
