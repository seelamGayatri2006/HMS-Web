package com.hms.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) {
        if (modelAndView != null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                boolean isAdmin  = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMIN"));
                boolean isDoctor = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("DOCTOR"));
                boolean isReceptionist = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("RECEPTIONIST"));

                modelAndView.addObject("isAdmin", isAdmin);
                modelAndView.addObject("isDoctor", isDoctor);
                modelAndView.addObject("isReceptionist", isReceptionist);
                modelAndView.addObject("loggedInUser", auth.getName());
            }
        }
    }
}
