package com.gdg.linking.domain.folder;

import com.gdg.linking.domain.item.Item;
import com.gdg.linking.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Entity
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
}
