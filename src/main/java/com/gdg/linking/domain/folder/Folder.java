package com.gdg.linking.domain.folder;

import com.gdg.linking.domain.item.Item;
import com.gdg.linking.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "f_id")
    private Long fId;

    @Column(name = "f_name", nullable = false, length = 45)
    private String folderName;

    @ManyToOne(fetch = FetchType.LAZY) // 여러 개의 폴더가 한 명의 사용자에게 속함, 지연 로딩 권장
    @JoinColumn(name = "user_id") // 외래키 user_id
    private User user;

    // 계층형 폴더 기능(폴더 내 폴더)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Folder parentFolder;

    // 엔티티가 생성될 때 자동으로 시간이 저장
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 하위 폴더 연쇄 삭제
    @OneToMany(mappedBy = "parentFolder", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Builder.Default
    private List<Folder> childFolders = new ArrayList<>();

    // 폴더 내 Item 연쇄 삭제 설정
    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Builder.Default
    private List<Item> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "status")
    private Item.ItemStatus status = Item.ItemStatus.ACTIVE; // 기본값은 ACTIVE

    @Column(name = "deleted_at")
    private java.time.LocalDate deletedAt; // 삭제된 날짜 기록

    // 상태 변경을 위한 편의 메서드
    public void updateStatus(Item.ItemStatus status) {
        this.status = status;
        if (status == Item.ItemStatus.TRASH) {
            this.deletedAt = java.time.LocalDate.now();
        } else {
            this.deletedAt = null;
        }
    }

    // 복원을 위한 메소드
    public void restore() {
        this.status = Item.ItemStatus.ACTIVE;
        this.deletedAt = null;
    }

}
