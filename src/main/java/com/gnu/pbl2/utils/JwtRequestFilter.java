package com.gnu.pbl2.utils;

import com.gnu.pbl2.exception.handler.UserHandler;
import com.gnu.pbl2.response.code.status.ErrorStatus;
import com.gnu.pbl2.user.service.CustomUserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserService customUserService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final List<String> EXCLUDE_URL = List.of(
            "/mojadol/api/v1/auth/**", "/actuator/**"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {

        try {
            String authorizationHeader = request.getHeader("Authorization");

            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                chain.doFilter(request, response);
                return;
            }

            String jwt = authorizationHeader.substring(7).trim();
            Long userId = null;

            try {
                userId = jwtUtil.extractUserId(jwt);
            } catch (ExpiredJwtException e) {
                // Access Token이 만료되었을 경우
                log.error("토큰 만료 오류 발생, JWT: {}", jwt);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("토큰 만료");
                return;
            }

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.info("JWT 유효성 검증 시작, 사용자 ID: {}", userId);
                UserDetails userDetails = customUserService.loadUserByUsername(userId.toString());

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    log.info("JWT 유효성 검증 성공, 사용자 인증 완료");
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
                    );
                }
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            log.error("JWT 필터 처리 중 예외 발생: ", e);
            throw new UserHandler(ErrorStatus.JWT_FILTER_ERROR);
        }

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return EXCLUDE_URL.stream().anyMatch(pattern -> pathMatcher.match(pattern, uri));
    }

}
