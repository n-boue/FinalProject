package tsu.finalproject.feature.enrollment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tsu.finalproject.feature.enrollment.dto.CourseRosterResponse;
import tsu.finalproject.feature.enrollment.dto.EnrollmentRequest;
import tsu.finalproject.feature.enrollment.dto.EnrollmentResponse;
import tsu.finalproject.feature.enrollment.dto.FinalGradeRequest;

import java.security.Principal;

@RestController
@RequestMapping("${api.prefix}/courses/{courseId}")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping("/enroll")
    @PreAuthorize("hasRole('STUDENT')")
    @ResponseStatus(HttpStatus.CREATED)
    @NonNull
    public EnrollmentResponse enroll(
            @PathVariable @NonNull Long courseId,
            @RequestBody @Valid EnrollmentRequest request,
            Principal principal
    ) {
        return enrollmentService.enrollStudent(principal.getName(), courseId, request);
    }

    @PutMapping("/enroll")
    @PreAuthorize("hasRole('STUDENT')")
    @NonNull
    public EnrollmentResponse updateEnrollment(
            @PathVariable @NonNull Long courseId,
            @RequestBody @Valid EnrollmentRequest request,
            Principal principal
    ) {
        return enrollmentService.updateEnrollmentSessions(principal.getName(), courseId, request);
    }

    @DeleteMapping("/enroll")
    @PreAuthorize("hasRole('STUDENT')")
    @NonNull
    public EnrollmentResponse dropCourse(
            @PathVariable @NonNull Long courseId,
            Principal principal
    ) {
        return enrollmentService.dropCourse(principal.getName(), courseId);
    }


    @GetMapping("/enrollments")
    @PreAuthorize("hasRole('PROF') or hasRole('ADMIN')")
    @NonNull
    public Page<CourseRosterResponse> getCourseRoster(
            @PathVariable @NonNull Long courseId,
            Pageable pageable,
            Principal principal
    ) {
        return enrollmentService.getCourseRoster(principal.getName(), courseId, pageable);
    }

    @PutMapping("/enrollments/{enrollmentId}/grade")
    @PreAuthorize("hasRole('PROF') or hasRole('ADMIN')")
    @NonNull
    public CourseRosterResponse assignFinalGrade(
            @PathVariable @NonNull Long courseId,
            @PathVariable @NonNull Long enrollmentId,
            @RequestBody @Valid FinalGradeRequest request,
            Principal principal
    ) {
        return enrollmentService.assignFinalGrade(principal.getName(), courseId, enrollmentId, request);
    }
}