package com.gdg.linking.domain.user;

import com.gdg.linking.domain.user.dto.User;
import com.gdg.linking.domain.user.dto.request.UserCreateRequest;
import com.gdg.linking.domain.user.dto.request.UserLoginRequest;
import com.gdg.linking.domain.user.dto.response.UserCreateResponse;
import com.gdg.linking.domain.user.dto.response.UserLoginResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import static com.gdg.linking.global.utils.SHA256Util.encryptSHA256;

@Service
public class UserServiceImpl implements UserService{


    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserCreateResponse register(UserCreateRequest request) {

        User existUser = userRepository.findByLoginId(request.getLoginId());
        if (existUser != null) {
            throw new RuntimeException("이미 존재하는 Id 입니다");
        }

        //비밀번호 암호화
        String encryptPassword = encryptSHA256(request.getPassword());

        User user = User.builder()
                .loginId(request.getLoginId())
                .password(encryptPassword)
                .email(request.getEmail())
                .nickName(request.getNickName())
                .build();

        userRepository.save(user);
        UserCreateResponse result = new UserCreateResponse(request.getLoginId());

        return result;
    }


    @Override
    @Transactional
    public UserLoginResponse login(UserLoginRequest request) {

        String encryptPassword = encryptSHA256(request.getPassword());

        User user = userRepository.findByIdAndPassword(request.getLoginId(),encryptPassword);

        if(user == null) {
            throw new RuntimeException("아이디나 비밀번호가 틀렸습니다");
        }

        UserLoginResponse response = new UserLoginResponse(user.getLoginId(),user.isAdmin());

        return response;
    }
}
