package com.bobridze5.TeleMed_backend.core.service.params;

import com.bobridze5.TeleMed_backend.core.entity.Patient;
import org.springframework.data.domain.Page;

public interface CrudParamService<R, U, S, F> {
    S addRecord(Patient patient, R request);

    S updateRecord(Patient patient, Long id, U request);

    S getRecordById(Patient patient, Long id);

    Page<S> getRecords(Patient patient, F filter);

    void deleteRecord(Patient patient, Long id);
}
