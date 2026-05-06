package com.bobridze5.TeleMed_backend.core.repository;

import com.bobridze5.TeleMed_backend.core.entity.notification.Reminder;
import com.bobridze5.TeleMed_backend.core.entity.notification.ReminderKind;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    Page<Reminder> findByPatientId(Long patientId, Pageable pageable);

    Optional<Reminder> findByIdAndPatientId(Long id, Long patientId);

    List<Reminder> findAllByEnabledTrueAndKind(ReminderKind kind);

    List<Reminder> findAllByEnabledTrueAndKindAndScheduledAtBefore(
            ReminderKind kind, Instant before);
}
