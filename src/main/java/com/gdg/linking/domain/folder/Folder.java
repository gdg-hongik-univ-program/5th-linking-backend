package com.gdg.linking.domain.folder;

import com.gdg.linking.domain.user.User; // 기존 User 엔티티 경로 확인 필요
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "f_id") // ERD의 f_id 매핑
    private Long fId;

    @Column(name = "f_name", nullable = false, length = 45) // ERD의 f_name
    private String folderName;

    @ManyToOne(fetch = FetchType.LAZY) // 여러 개의 폴더가 한 명의 사용자에게 속함, 지연 로딩 권장
    @JoinColumn(name = "user_id") // 외래키 user_id
    private User user;
}
