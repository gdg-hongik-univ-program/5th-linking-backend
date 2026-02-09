package com.gdg.linking.domain.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface TagRepository extends JpaRepository<Tag,Long> {

    // 태그 이름으로 조회 (Optional로 반환하여 존재 여부 확인)
    Optional<Tag> findByTagName(String tagName);
}
