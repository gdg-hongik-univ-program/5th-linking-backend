package com.gdg.linking.domain.item;

import com.gdg.linking.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name ="items")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;


    @OneToOne(fetch = FetchType.LAZY) // 지연 로딩 설정 (성능 최적화)
    @JoinColumn(name = "user_id") // 실제 DB 컬럼명 설정 및 필수값 지정
    private User user;

        //폴더기능 미완성
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "folder_id")
//    private Long folderId;
    
    //태그기능 미완성
//    @ManyToMany
//    @JoinTable(
//            name = "item_tag_map", // 연결 테이블 이름
//            joinColumns = @JoinColumn(name = "item_id"), // Item 쪽 외래키
//            inverseJoinColumns = @JoinColumn(name = "tag_id") // Tag 쪽 외래키
//    )
//    @Builder.Default // 빌더 패턴 사용 시 리스트가 null이 되지 않게 방지
//    private List<Tag> tags = new ArrayList<>();

    @Column(name = "url")
    private String url;

    @Column(name = "title")
    private String title;

    @Column(name = "memo")
    private String memo;

    @Column(name = "importance")
    private boolean importance = false;

    @Column(name = "deadline")
    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ItemStatus status = ItemStatus.ACTIVE;

    @Column(name = "deleted_at")
    private LocalDate deletedAt;

    @Column(name = "created_at")
    private LocalDate createdAt;


    public enum ItemStatus {
        ACTIVE, COMPLETED, TRASH
    }
}
