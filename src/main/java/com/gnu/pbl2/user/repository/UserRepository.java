package com.gnu.pbl2.user.repository;

import com.gnu.pbl2.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserName(String userName);

    Optional<User> findByUserLoginId(String userLoginId);

    Optional<User> findByUserId(Long userId);

    Optional<User> findByNickname(String nickname);

    Optional<User> findByEmail(String email);

    Optional<User> findByUserLoginIdAndEmail(String userLoginId, String email);
}
