package com.gnu.pbl2.trackingResult.repository;

import com.gnu.pbl2.trackingResult.entity.Tracking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackingRepository extends JpaRepository<Tracking, Long> {
}
