package com.gdg.linking.global.aop;


import com.gdg.linking.global.utils.SessionUtil;
import jakarta.servlet.ServletRequestAttributeEvent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class LoginCheckAspect {

    //loginchek 변수에 LoginCheck에 관련된 값을 바인딩
    @Around("@annotation(loginCheck)")

    //Throwable 전역 예외 처리 코드 추가시 변경하기
    public Object checkLogin(ProceedingJoinPoint pjp,LoginCheck loginCheck) throws Throwable {

        //1. 웹에서 요청을 받았는지 확인
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();


        //웹이 아니라면 즉시 차단
        if(attrs == null) {
            throw new IllegalStateException("No request context");
        }

        // 2. ttpServletRequest 획득
        HttpServletRequest request = attrs.getRequest();

        // 3. 세션 획득 (없으면 null 반환)
        HttpSession session = request.getSession(false);

        // 4. 세션 자체가 없으면 → 로그인 안 됨
        if (session == null) {
            throw new HttpStatusCodeException(HttpStatus.UNAUTHORIZED, "No session") {};
        }

        String userId;
        switch(loginCheck.type()) {

            case USER -> userId = SessionUtil.getLoginUserId(session);
            case ADMIN -> userId = SessionUtil.getLoginAdminId(session);
            default -> throw new IllegalStateException("Unknow type");

        }

        //User와 Admin 격리된 구조
        //어떻게 더 개선시켜야될까
        if(userId ==null){
            throw new HttpStatusCodeException(HttpStatus.UNAUTHORIZED, "NO_LOGIN") {};
        }

        return pjp.proceed();
    }


}
