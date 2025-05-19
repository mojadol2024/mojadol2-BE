package com.gnu.pbl2.question.repository;

import com.gnu.pbl2.coverLetter.entity.CoverLetter;
import com.gnu.pbl2.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByCoverLetter(CoverLetter coverLetter);
}
