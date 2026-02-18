package com.gdg.linking.domain.user.dto.response;

import com.gdg.linking.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoResponse {
    private String loginId;
    private String nickName;
    private String profileImage;


    // User 엔티티를 DTO로 변환하는 생성자
    public static UserInfoResponse from(User user) {
        return new UserInfoResponse(
                user.getLoginId(),
                user.getNickName(),
                user.getProfileImage()
        );
    }
}