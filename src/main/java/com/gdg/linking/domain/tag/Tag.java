package com.gdg.linking.domain.tag;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter // 필요시 추가
@Builder // 빌더 패턴 사용 가능하게 함
@AllArgsConstructor // 빌더 사용을 위해 모든 필드 생성자 필요
@Table(name ="tag")
@NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tagId;

    @Column(unique = true, nullable = false)
    private String tagName;

    @OneToMany(mappedBy = "tag")
    @Builder.Default
    private List<ItemTag> postTags = new ArrayList<>();


}
