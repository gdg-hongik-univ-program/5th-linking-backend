package com.gdg.linking.domain.user.dto;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="user_id")
    private Long userId;

    @Column(name ="login_id")
    private String loginId;

    @Column(name ="password")
    private String password;

    @Column(name ="nickname")
    private String nickName;

    @Column(name ="email")
    private String email;

    @Column(name ="is_admin")
    private boolean isAdmin;


}
