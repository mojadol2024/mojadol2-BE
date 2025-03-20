package com.gnu.pbl2.coverLetter.repository;

import com.gnu.pbl2.coverLetter.entity.CoverLetter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CoverLetterRepository extends JpaRepository<CoverLetter, Long> {

    Page<CoverLetter> findByCoverLetterId(Long coverLetterId, Pageable pageable);
}
