package com.gdg.linking.domain.user;

import com.gdg.linking.domain.user.dto.request.UserCreateRequest;
import com.gdg.linking.domain.user.dto.request.UserLoginRequest;
import com.gdg.linking.domain.user.dto.request.UserPatchNickRequest;
import com.gdg.linking.domain.user.dto.request.UserPatchPasswordRequest;
import com.gdg.linking.domain.user.dto.response.UserCreateResponse;
import com.gdg.linking.domain.user.dto.response.UserInfoResponse;
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
                .isAdmin(false)
                .profileImage(request.getImageCode())
                .build();

        userRepository.save(user);
        UserCreateResponse result = new UserCreateResponse(
                user.getLoginId(),
                user.getProfileImage()
                );

        return result;
    }


    @Override
    @Transactional
    public UserLoginResponse login(UserLoginRequest request) {

        // 1. 아이디를 기준으로 먼저 사용자 조회
        User user = userRepository.findByLoginId(request.getLoginId());

        // 2. 사용자가 데이터베이스에 아예 존재하지 않는 경우
        if (user == null) {
            // ErrorMessage enum에 "존재하지 않는 사용자입니다"에 해당하는 상수를 넣어주세요.
            throw new BadRequestException(ErrorMessage.MEMBER_NOTFOUND);
        }

        // 3. 사용자가 존재한다면, 입력받은 비밀번호를 암호화하여 DB의 비밀번호와 비교
        String encryptPassword = encryptSHA256(request.getPassword());

        if (!user.getPassword().equals(encryptPassword)) {
            // ErrorMessage enum에 "아이디나 비밀번호가 틀렸습니다"에 해당하는 상수를 넣어주세요.
            throw new BadRequestException(ErrorMessage.INVALID_PASSWORD);
        }

        // 4. 아이디와 비밀번호가 모두 맞다면 로그인 성공 응답 반환
        return new UserLoginResponse(user.getUserId(), user.getLoginId(), user.isAdmin());
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

    @Override
    @Transactional
    public void patchNickName(Long userId, UserPatchNickRequest request) {
        User user = userRepository.findById(userId);

        // 유저가 없는 경우 예외 처리 (선택 사항이나 권장됨)
        if (user == null) {
            throw new BadRequestException(ErrorMessage.MEMBER_NOTFOUND);
        }

        user.updateNickName(request.getNickName());

        user.updateImageCode(request.getImageCode());
    }

    @Override
    @Transactional
    public void patchPassword(Long userId, UserPatchPasswordRequest request) {
        User user = userRepository.findById(userId);

        if (user == null) {
            throw new BadRequestException(ErrorMessage.MEMBER_NOTFOUND);
        }

        // 2. 비밀번호 암호화
        String encryptPassword = encryptSHA256(request.getPassword());

        // 3. 비밀번호 변경 (Dirty Checking)
        user.updatePassword(encryptPassword);
    }

    @Override
    @Transactional
    public UserInfoResponse getUserInfo(Long userId) {
        // 1. 사용자 조회
        User user = userRepository.findById(userId);

        // 2. 예외 처리 (방어적 코드)
        if (user == null) {
            throw new BadRequestException(ErrorMessage.MEMBER_NOTFOUND);
        }

        // 3. Entity -> DTO 변환 후 반환
        // (UserInfoResponse에 static factory method 'from'이 있다고 가정)
        return UserInfoResponse.from(user);
    }

    @Override
    @Transactional
    public Boolean checkPassword(Long userId, String password) {
        // 1. 사용자 조회
        User user = userRepository.findById(userId);

        if (user == null) {
            throw new BadRequestException(ErrorMessage.MEMBER_NOTFOUND);
        }

        // 2. 입력받은 평문 비밀번호를 암호화 (가입/로그인 로직과 동일해야 함)
        String encryptPassword = encryptSHA256(password);

        // 3. DB에 저장된 암호화된 비밀번호와 비교
        if (!user.getPassword().equals(encryptPassword)) {
            // 비밀번호가 틀리면 false 반환 (Controller에서 401 등을 처리하거나 여기서 예외 발생)
            return false;
        }

        return true;
    }
}
