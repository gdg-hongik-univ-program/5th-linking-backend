package com.gdg.linking.domain.tag;


import com.gdg.linking.domain.item.Item;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name ="item_tag")
public class ItemTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemTagId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    // 편의 메서드: 연관관계 설정을 도와줌
    public static ItemTag createItemTag(Item item, Tag tag) {
        ItemTag itemTag = new ItemTag();
        itemTag.item = item;
        itemTag.tag = tag;
        return itemTag;
    }
}
