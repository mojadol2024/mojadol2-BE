package com.gnu.pbl2.user.repository;

import com.gnu.pbl2.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserLoginId(String userLoginId);

    Optional<User> findByNickname(String nickname);

    Optional<User> findByEmail(String email);

    Optional<User> findByUserLoginIdAndEmail(String userLoginId, String email);

    List<User> findByDeletedTimeBefore(LocalDateTime time);

    List<User> findByDeletedTimeIsNull();


}
