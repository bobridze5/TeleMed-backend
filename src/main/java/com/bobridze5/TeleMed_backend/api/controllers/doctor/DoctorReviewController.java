package com.bobridze5.TeleMed_backend.api.controllers.doctor;

import com.bobridze5.TeleMed_backend.api.controllers.API;
import com.bobridze5.TeleMed_backend.api.dto.review.ReviewResponse;
import com.bobridze5.TeleMed_backend.core.security.UserDetailsImpl;
import com.bobridze5.TeleMed_backend.core.service.review.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(API.DOCTOR_ME + "/reviews")
@RequiredArgsConstructor
@Tag(name = "Отзывы врача")
public class DoctorReviewController {
    private final ReviewService reviewService;

    @GetMapping
    @Operation(summary = "Получить отзывы о враче")
    public Page<ReviewResponse> getMyReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return reviewService.getDoctorReviews(userDetails.getUserId(), page, size);
    }
}
