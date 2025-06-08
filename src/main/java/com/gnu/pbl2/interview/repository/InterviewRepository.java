package com.gnu.pbl2.interview.repository;

import com.gnu.pbl2.interview.entity.Interview;
import com.gnu.pbl2.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {

    List<Interview> findByIsDeletedAndDeletedAtBefore(Integer isDeleted, LocalDateTime deleteDay);

    @Query("SELECT i FROM Interview i WHERE i.question.questionId = :questionId AND i.isDeleted = :isDeleted")
    List<Interview> findByQuestionIdAndDeletedAt(@Param("questionId") Long questionId, @Param("isDeleted") Integer isDeleted);

    Interview findByQuestionAndIsDeleted(Question question, Integer isDeleted);

    @Query("SELECT i FROM Interview i WHERE i.question.questionId = :questionId")
    Optional<Interview> findByQuestionId(@Param("questionId") Long questionId);


}
