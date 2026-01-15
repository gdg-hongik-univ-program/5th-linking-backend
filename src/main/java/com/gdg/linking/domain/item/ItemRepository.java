package com.gdg.linking.domain.item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>{

    List<Item> findAllByUser_UserIdOrderByCreatedAtDesc(Long userId);
    
}
