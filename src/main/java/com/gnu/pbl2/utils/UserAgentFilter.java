package com.gnu.pbl2.utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

@Component
@Order(1)
public class UserAgentFilter extends OncePerRequestFilter {

    private static final List<Pattern> BLOCKED_PATTERNS = List.of(
            Pattern.compile("(?i)java"),
            Pattern.compile("(?i)dalvik"),
            Pattern.compile("(?i)headlesschrome"),
            Pattern.compile("(?i)python"),
            Pattern.compile("(?i)bot"),
            Pattern.compile("(?i)crawler")
    );


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String userAgent = request.getHeader("User-Agent");

        if (userAgent == null || isBlocked(userAgent)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"차단된 User-Agent입니다.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isBlocked(String userAgent) {
        return BLOCKED_PATTERNS.stream().anyMatch(p -> p.matcher(userAgent).find());
    }
}
