package com.gdg.linking.global.utils;

import jakarta.servlet.http.HttpSession;

public class SessionUtil {

    private static final String LOGIN_USER_ID = "LOGIN_USER_ID";
    private static final String LOGIN_ADMIN_ID = "LOGIN_ADMIN_ID";


    public SessionUtil(){

    }
    public static Long getLoginUserId(HttpSession session){
        return (Long) session.getAttribute(LOGIN_USER_ID);
    }

    public static Long getLoginAdminId(HttpSession session){
        return (Long) session.getAttribute(LOGIN_ADMIN_ID);
    }

    public static void setLoginUserId(HttpSession session,String id){
        session.setAttribute( LOGIN_USER_ID , id);
    }

    public static void setLoginAdminId(HttpSession session,String id){
        session.setAttribute( LOGIN_ADMIN_ID , id);
    }



}
