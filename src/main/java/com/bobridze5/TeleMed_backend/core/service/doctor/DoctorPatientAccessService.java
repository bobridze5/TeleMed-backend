package com.bobridze5.TeleMed_backend.core.service.doctor;

import com.bobridze5.TeleMed_backend.core.entity.medical.Doctor;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;

public interface DoctorPatientAccessService {
    Patient getPatientForDoctor(Doctor doctor, Long patientId);
}
