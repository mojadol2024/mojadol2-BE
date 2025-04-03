package com.gnu.pbl2.interview.repository;

import com.gnu.pbl2.interview.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {

    List<Interview> findByIsDeletedAndDeletedAtBefore(Integer isDeleted, LocalDateTime deleteDay);

}
