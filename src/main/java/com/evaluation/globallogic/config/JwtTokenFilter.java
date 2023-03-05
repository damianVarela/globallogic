package com.evaluation.globallogic.config;

import com.evaluation.globallogic.service.AccountService;
import lombok.extern.apachecommons.CommonsLog;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@CommonsLog
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    private final AccountService accountService;

    @Autowired
    public JwtTokenFilter(JwtProvider jwtProvider,
                          AccountService accountService) {
        this.jwtProvider = jwtProvider;
        this.accountService = accountService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        try {
            val token = jwtProvider.getToken(request);
            val email = jwtProvider.getEmailFromToken(token);
            val userDetails = accountService.loadUserByUsername(email);
            val auth = new UsernamePasswordAuthenticationToken(email, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception e) {
            log.debug("authentication error for request " + request.getRequestURL().toString() + " : " + e.getMessage());
            SecurityContextHolder.clearContext();
        }
        chain.doFilter(request, response);
    }
}
