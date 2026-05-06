package com.bobridze5.TeleMed_backend.core.service.review;

import com.bobridze5.TeleMed_backend.api.dto.review.ReviewRequest;
import com.bobridze5.TeleMed_backend.api.dto.review.ReviewResponse;
import com.bobridze5.TeleMed_backend.core.entity.medical.Appointment;
import com.bobridze5.TeleMed_backend.core.entity.medical.AppointmentStatus;
import com.bobridze5.TeleMed_backend.core.entity.medical.Review;
import com.bobridze5.TeleMed_backend.core.exceptions.EntityNotFoundException;
import com.bobridze5.TeleMed_backend.core.repository.AppointmentRepository;
import com.bobridze5.TeleMed_backend.core.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final AppointmentRepository appointmentRepository;

    @Transactional
    public ReviewResponse createReview(Long appointmentId, Long patientUserId, ReviewRequest request) {
        Appointment appointment = appointmentRepository.findByIdAndUserId(appointmentId, patientUserId)
                .orElseThrow(() -> new EntityNotFoundException("Запись не найдена"));

        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Отзыв можно оставить только после завершённой консультации");
        }

        if (reviewRepository.existsByAppointmentId(appointmentId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Отзыв для этой записи уже существует");
        }

        Review review = Review.builder()
                .appointment(appointment)
                .patient(appointment.getPatient())
                .doctor(appointment.getDoctor())
                .rating(request.rating())
                .comment(request.comment())
                .build();

        return toResponse(reviewRepository.save(review));
    }

    /**
     * Пациент редактирует свой отзыв. Доступ проверяется по совпадению
     * patient.id у отзыва с patientUserId — иначе можно было бы редактировать
     * чужие отзывы, зная их id.
     */
    @Transactional
    public ReviewResponse updateReview(Long appointmentId, Long patientUserId, ReviewRequest request) {
        Review review = reviewRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Отзыв не найден"));
        if (!review.getPatient().getId().equals(patientUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Редактировать можно только свой отзыв");
        }
        if (request.rating() != null) review.setRating(request.rating());
        // Для комментария null означает «не менять», а пустая строка → стереть.
        if (request.comment() != null) {
            review.setComment(request.comment().isBlank() ? null : request.comment());
        }
        return toResponse(reviewRepository.save(review));
    }

    @Transactional
    public void deleteReview(Long appointmentId, Long patientUserId) {
        Review review = reviewRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Отзыв не найден"));
        if (!review.getPatient().getId().equals(patientUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Удалять можно только свой отзыв");
        }
        reviewRepository.delete(review);
    }

    public Page<ReviewResponse> getDoctorReviews(Long doctorId, int page, int size) {
        return reviewRepository.findByDoctorIdOrderByCreatedAtDesc(
                doctorId,
                PageRequest.of(page, size, Sort.by("createdAt").descending())
        ).map(this::toResponse);
    }

    /**
     * Получить отзыв по своему приёму — используется фронтом при открытии
     * формы редактирования отзыва (нужно подгрузить текущий рейтинг и текст).
     * Если отзыв чужой — кидаем 404 (намеренно, чтобы не палить сам факт).
     */
    @Transactional(readOnly = true)
    public ReviewResponse getReviewByAppointment(Long appointmentId, Long patientUserId) {
        Review review = reviewRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Отзыв не найден"));
        if (!review.getPatient().getId().equals(patientUserId)) {
            throw new EntityNotFoundException("Отзыв не найден");
        }
        return toResponse(review);
    }

    private ReviewResponse toResponse(Review r) {
        String patientName = Stream.of(
                r.getPatient().getLastName(),
                r.getPatient().getFirstName(),
                r.getPatient().getMiddleName()
        ).filter(s -> s != null && !s.isBlank())
                .reduce("", (a, b) -> a.isBlank() ? b : a + " " + b);

        return new ReviewResponse(
                r.getId(),
                r.getAppointment().getId(),
                r.getPatient().getId(),
                patientName,
                r.getDoctor().getId(),
                r.getRating(),
                r.getComment(),
                r.getCreatedAt()
        );
    }
}
