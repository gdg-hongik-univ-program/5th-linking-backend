package com.gdg.linking.domain.item;

import com.gdg.linking.domain.folder.Folder;
import com.gdg.linking.domain.tag.ItemTag;
import com.gdg.linking.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
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
        ACTIVE, COMPLETED, TRASH,ORPHAN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;


    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 설정 (성능 최적화)
    @JoinColumn(name = "user_id") // 실제 DB 컬럼명 설정 및 필수값 지정
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "f_id")
    private Folder folder;


    //ItemTag 테이블 연결
    //빌더를 통해 객체를 만들때에도 자동으로 초기화 해주는 코드
    //orphanRemoval 고아 객체 삭제
    @Builder.Default
    @OneToMany(mappedBy = "item",   cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<ItemTag> itemTags = new ArrayList<>();

    @Column(name = "url", length = 2048)
    private String url;

    @Column(name = "title", length = 70)
    private String title;

    @Column(name = "memo", length = 1024)
    private String memo;

    @Builder.Default
    @Column(name = "importance")
    private boolean importance = false;

    @Column(name = "deadline")
    private LocalDate deadline;



    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private ItemStatus status = ItemStatus.ACTIVE;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate // JPA Auditing으로 자동 갱신 설정
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 썸네일 이미지 주소 저장 필드
    @Column(name = "image_url", length = 1024)
    private String imageUrl;

    public void restore() {
        this.status = ItemStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now(); // 복구 시 시각을 현재로 갱신 (50일 기준 리셋)
        this.deletedAt = null;
    }

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "item_relations",           // 생성될 중간 테이블 이름
            joinColumns = @JoinColumn(name = "from_id"),    // 현재 아이템(출발점) 외래키
            inverseJoinColumns = @JoinColumn(name = "to_id") // 연결될 아이템(도착점) 외래키
    )
    @OnDelete(action = OnDeleteAction.CASCADE) // item이 사라지면 item_tag 테이블 값도 제거하기
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
    public void update(String url,
                       String title,
                       String memo,
                       boolean importance,
                       LocalDate deadline,
                       String imageUrl) {
    this.url = url;
    this.title = title;
    this.memo = memo;
    this.importance = importance;
    this.deadline = deadline;
    this.imageUrl = imageUrl;
    }

    //상태 변경 전용 메서드
    public void updateStatus(ItemStatus status) {
        this.status = status;
        if (status == ItemStatus.TRASH) {
            this.deletedAt = LocalDateTime.now();
        }
    }

    // Item 엔티티와 tag와 연결하기
    public void addItemTag(ItemTag itemTag) {
        this.itemTags.add(itemTag); // 자바 객체 세상에서의 동기화
        itemTag.setItem(this);     // 실제 DB FK 권한을 가진 주인에게 세팅 (중요!)
    }

    // Item 중요도 즉각 반영
    public void toggleImportance() {
        this.importance = !this.importance;
    }

    // 폴더 이동 편의 메서드
    public void updateFolder(Folder folder) {
        this.folder = folder;
    }


}
