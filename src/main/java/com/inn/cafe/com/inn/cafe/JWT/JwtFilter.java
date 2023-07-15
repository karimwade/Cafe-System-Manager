package com.inn.cafe.com.inn.cafe.JWT;

import io.jsonwebtoken.Claims;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.ServletException;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CustomerUsersDetailsService service;
    Claims claims = null;
    private String userName= null;

    public boolean isAdmin(){
        return "admin".equalsIgnoreCase((String) claims.get("role"));
    }
    public boolean isUser(){
        return "user".equalsIgnoreCase((String) claims.get("role"));
    }
    public String getCurrentUser(){
        return userName;
    }

    @Override
    protected void doFilterInternal(javax.servlet.http.HttpServletRequest httpServletRequest, javax.servlet.http.HttpServletResponse httpServletResponse, javax.servlet.FilterChain filterChain) throws ServletException, IOException {
        if(httpServletRequest.getServletPath().matches("/user/login|/user/forgotPassword|/user/signup")){
            filterChain.doFilter(httpServletRequest,httpServletResponse);
        }
        else {
            String authorizationHeader = httpServletRequest.getHeader("Authorization");
            String token = null;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer")){
                token = authorizationHeader.substring(7);
                userName = jwtUtil.extractUsername(token);
                claims = jwtUtil.extractAllClaims(token);
            }

            if(userName != null && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails = service.loadUserByUsername(userName);
                if (jwtUtil.validateToken(token,userDetails)){
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken= new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(httpServletRequest)
                    );
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
            filterChain.doFilter(httpServletRequest,httpServletResponse);
        }

    }
}
