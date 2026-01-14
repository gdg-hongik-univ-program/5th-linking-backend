package com.gdg.linking.domain.user;

import com.gdg.linking.domain.user.dto.User;
import com.gdg.linking.domain.user.dto.request.UserCreateRequest;
import com.gdg.linking.domain.user.dto.request.UserLoginRequest;
import com.gdg.linking.domain.user.dto.response.UserCreateResponse;
import com.gdg.linking.domain.user.dto.response.UserLoginResponse;
import com.gdg.linking.global.exception.custom.BadRequestException;
import com.gdg.linking.global.exception.message.ErrorMessage;
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
            throw new BadRequestException(ErrorMessage.MEMBER_NOTFOUND);
        }

        UserLoginResponse response = new UserLoginResponse(user.getLoginId(),user.isAdmin());

        return response;
    }

    @Override
    @Transactional
    public Boolean findById(String id) {

        User user = userRepository.findByLoginId(id);
        
        //null 비어있으면 사용 가능 값
        if(user ==null){
            return true;
            
            // 뭔가 있으면 사용 불가능
        } else{
            return false;
        }

    }
}
