package com.gdg.linking.domain.item;

import com.gdg.linking.domain.folder.Folder;
import com.gdg.linking.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name ="items")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Item {

    public enum ItemStatus {
        ACTIVE, COMPLETED, TRASH
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;


    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 설정 (성능 최적화)
    @JoinColumn(name = "user_id") // 실제 DB 컬럼명 설정 및 필수값 지정
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    private Folder folder;
    
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
    @Column(name = "status")
    private ItemStatus status = ItemStatus.ACTIVE;

    @Column(name = "deleted_at")
    private LocalDate deletedAt;

    @CreatedDate
    @Column(name = "created_at", updatable = false) // 생성 시각은 수정되지 않도록 설정 권장
    private LocalDateTime createdAt;

    @ManyToMany
    @JoinTable(
            name = "item_relations",           // 생성될 중간 테이블 이름
            joinColumns = @JoinColumn(name = "from_id"),    // 현재 아이템(출발점) 외래키
            inverseJoinColumns = @JoinColumn(name = "to_id") // 연결될 아이템(도착점) 외래키
    )
    private List<Item> relatedItems = new ArrayList<>();

    // 연결 편의 메서드
    public void addRelation(Item target) {
        if (target != null && !this.relatedItems.contains(target)) {
            this.relatedItems.add(target);
        }
    }

    // 연결 해제 메서드
    public void removeRelation(Item target) {
        if (target != null) {
            this.relatedItems.remove(target);
        }
    }

    //업데이트 전용 메서드
    public void update(String url, String title, String memo, boolean importance, LocalDate deadline) {
    this.url = url;
    this.title = title;
    this.memo = memo;
    this.importance = importance;
    this.deadline = deadline;
    }

    //상태 변경 전용 메서드
    public void updateStatus(ItemStatus status) {
        this.status = status;
        if (status == ItemStatus.TRASH) {
            this.deletedAt = LocalDate.now();
        }
    }

}
