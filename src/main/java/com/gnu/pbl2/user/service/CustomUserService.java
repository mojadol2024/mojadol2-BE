package com.gnu.pbl2.user.service;

import com.gnu.pbl2.user.entity.CustomUserDetails;
import com.gnu.pbl2.user.entity.User;
import com.gnu.pbl2.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        try {
            // identifier가 숫자면 userId(Long)로 조회
            Long userId = Long.parseLong(identifier);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다: " + identifier));

            return new CustomUserDetails(user);

        } catch (NumberFormatException e) {
            // identifier가 문자열이면 userLoginId(String)로 조회
            User user = userRepository.findByUserLoginId(identifier)
                    .orElseThrow(() -> new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다: " + identifier));

            return new CustomUserDetails(user);
        }
    }


}
