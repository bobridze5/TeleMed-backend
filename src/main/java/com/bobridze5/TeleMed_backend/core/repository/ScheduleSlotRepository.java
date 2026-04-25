package com.bobridze5.TeleMed_backend.core.repository;

import com.bobridze5.TeleMed_backend.core.entity.medical.ScheduleSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleSlotRepository extends JpaRepository<ScheduleSlot, Long> {
    
    List<ScheduleSlot> findByScheduleId(Long scheduleId);
    
    List<ScheduleSlot> findByScheduleIdOrderByStartTime(Long scheduleId);
    
    Optional<ScheduleSlot> findByScheduleIdAndStartTime(Long scheduleId, LocalTime startTime);
    
    void deleteByScheduleId(Long scheduleId);
    
    boolean existsByScheduleIdAndStartTime(Long scheduleId, LocalTime startTime);
}
