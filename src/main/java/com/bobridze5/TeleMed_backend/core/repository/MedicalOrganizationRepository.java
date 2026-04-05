package com.bobridze5.TeleMed_backend.core.repository;

import com.bobridze5.TeleMed_backend.core.entity.medical.MedicalOrganization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalOrganizationRepository extends JpaRepository<MedicalOrganization, Long> {
}
