package com.gnu.pbl2.trackingResult.repository;

import com.gnu.pbl2.interview.entity.Interview;
import com.gnu.pbl2.trackingResult.entity.Tracking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackingRepository extends JpaRepository<Tracking, Long> {
    boolean existsByInterview(Interview interview);

    Tracking findByInterview(Interview interview);
}
