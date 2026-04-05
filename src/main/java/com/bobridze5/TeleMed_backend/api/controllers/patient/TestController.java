package com.bobridze5.TeleMed_backend.api.controllers.patient;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.test.TestFindRiscFilterRequest;
import com.bobridze5.TeleMed_backend.api.dto.test.TestFindRiscRequest;
import com.bobridze5.TeleMed_backend.api.dto.test.TestFindRiscResponse;
import com.bobridze5.TeleMed_backend.core.annotations.CurrentPatient;
import com.bobridze5.TeleMed_backend.core.entity.medical.Patient;
import com.bobridze5.TeleMed_backend.core.service.test.TestFindRiscService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springdoc.core.annotations.ParameterObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(API.PATIENT_ME_TEST)
@RequiredArgsConstructor
@Tag(name = "Тестирование", description = "Тест FINDRISC — оценка риска развития диабета 2 типа")
public class TestController {
    private final TestFindRiscService testFindRiscService;

    @GetMapping
    @Operation(
            summary = "Получить все результаты тестов FINDRISC",
            description = "Возвращает список результатов FINDRISC тестов с пагинацией, сортировкой и фильтрацией по датам"
    )
    @ApiResponse(responseCode = "200", description = "Список результатов успешно получен")
    @ApiResponse(responseCode = "401", description = "Не авторизован")
    public Page<TestFindRiscResponse> getResults(
            @ParameterObject @ModelAttribute TestFindRiscFilterRequest filterRequest,
            @Parameter(description = "Номер страницы (начиная с 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "20") int size,
            @CurrentPatient Patient patient
    ) {
        return testFindRiscService.getTests(patient, page, size, filterRequest);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить результат теста по ID",
            description = "Возвращает детальный результат конкретного FINDRISC теста"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Результат успешно получен"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "404", description = "Тест не найден")
    })
    public TestFindRiscResponse getResult(
            @Parameter(description = "ID теста") @PathVariable Long id,
            @CurrentPatient Patient patient
    ) {
        return testFindRiscService.getTestById(id, patient);
    }

    @PostMapping
    @Operation(
            summary = "Создать новый результат теста",
            description = "Сохраняет результаты пройденного FINDRISC теста для текущего пациента"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Результат успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    public ResponseEntity<TestFindRiscResponse> postResult(
            @Valid @RequestBody TestFindRiscRequest request,
            @CurrentPatient Patient patient
    ) {
        TestFindRiscResponse response = testFindRiscService.writeTestResults(patient, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удалить результат теста",
            description = "Удаляет результат FINDRISC теста по ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Результат успешно удален"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "404", description = "Тест не найден")
    })
    public void deleteResult(
            @Parameter(description = "ID теста") @PathVariable Long id,
            @CurrentPatient Patient patient
    ) {
        testFindRiscService.deleteTestById(id, patient);
    }
}
