package com.strongcom.doormate.util;

import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Service
public class CookieUtil {

    public Cookie createCookie(String cookieName, String token, String cookieDvcd) {
        Cookie cookie = new Cookie(cookieName, token);
        cookie.setHttpOnly(true);

        if ("A".equals(cookieDvcd)) {
            // 30분 = 60 * 30
            cookie.setMaxAge(60*2);
        }
        else if ("R".equals(cookieDvcd)) {
            // 3시간 = 60 * 60 * 3
            cookie.setMaxAge(60*5);
        }
        else {
            // Token 쿠키 삭제시 삭제 처리를 위하여 10년 설정
            cookie.setMaxAge(60 * 60 * 24 * 365 * 10);
        }
        cookie.setPath("/");

        return cookie;
    }

    public Cookie expireCookie(String cookieName) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setMaxAge(0);

        return cookie;
    }

    public Cookie getCookie(HttpServletRequest request, String cookieName) {
        final Cookie[] cookies = request.getCookies();

        if (null == cookies) return null;

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName))
                return  cookie;
        }

        return null;
    }

}
