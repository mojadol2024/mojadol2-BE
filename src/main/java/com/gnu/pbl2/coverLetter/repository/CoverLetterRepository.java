package com.gnu.pbl2.coverLetter.repository;

import com.gnu.pbl2.coverLetter.entity.CoverLetter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoverLetterRepository extends JpaRepository<CoverLetter, Long> {

    @Query("SELECT c FROM CoverLetter c WHERE c.user.userId = :userId AND c.isDeleted = 1")
    Page<CoverLetter> findByUserId(@Param("userId") Long userId, Pageable pageable);

    Optional<CoverLetter> findByCoverLetterIdAndIsDeleted(Long coverLetterId, int isDeleted);



}
