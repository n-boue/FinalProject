package tsu.finalproject.feature.enrollment;

import io.jsonwebtoken.lang.Assert;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tsu.finalproject.common.manager.DomainLookupService;
import tsu.finalproject.feature.course.entity.Course;
import tsu.finalproject.feature.course.entity.CourseSession;
import tsu.finalproject.feature.course.security.CourseSecurityManager;
import tsu.finalproject.feature.enrollment.dto.CourseRosterResponse;
import tsu.finalproject.feature.enrollment.dto.EnrollmentRequest;
import tsu.finalproject.feature.enrollment.dto.EnrollmentResponse;
import tsu.finalproject.feature.enrollment.dto.FinalGradeRequest;
import tsu.finalproject.feature.enrollment.enums.EnrollmentStatus;
import tsu.finalproject.feature.notification.NotificationDispatcher;
import tsu.finalproject.feature.user.entity.Student;
import tsu.finalproject.feature.user.entity.User;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final DomainLookupService domainLookupService;
    private final CourseEnrollmentValidator enrollmentValidator;
    private final EnrollmentMapper enrollmentMapper;
    private final CourseSecurityManager securityManager;
    private final NotificationDispatcher notificationDispatcher;

    @Transactional
    @NonNull
    public EnrollmentResponse enrollStudent(@NonNull String studentEmail, @NonNull Long courseId, @NonNull EnrollmentRequest request) {
        Student student = domainLookupService.getStudentByEmail(studentEmail);

        Assert.isTrue(enrollmentRepository.findByStudentIdAndCourseId(student.getId(), courseId).isEmpty(),
                "Student is already enrolled. Please use the update endpoint to change sessions.");

        Course course = domainLookupService.getCourse(courseId);
        List<CourseSession> requestedSessions = domainLookupService.getCourseSessions(request.sessionIds());

        enrollmentValidator.validateEnrollment(course, requestedSessions);

        Enrollment enrollment = Enrollment.builder()
                                        .student(student)
                                        .course(course)
                                        .selectedSessions(new HashSet<>(requestedSessions))
                                        .status(EnrollmentStatus.ENROLLED)
                                        .build();

        return enrollmentMapper.toResponse(enrollmentRepository.save(enrollment));
    }

    @Transactional
    @NonNull
    public EnrollmentResponse updateEnrollmentSessions(@NonNull String studentEmail, @NonNull Long courseId, @NonNull EnrollmentRequest request) {
        Student student = domainLookupService.getStudentByEmail(studentEmail);
        Course course = domainLookupService.getCourse(courseId);
        List<CourseSession> requestedSessions = domainLookupService.getCourseSessions(request.sessionIds());

        enrollmentValidator.validateEnrollment(course, requestedSessions);

        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(student.getId(), courseId)
                                        .orElseThrow(() -> new EntityNotFoundException("No existing enrollment found for this course."));

        enrollment.setStatus(EnrollmentStatus.ENROLLED);
        enrollment.setSelectedSessions(new HashSet<>(requestedSessions));

        return enrollmentMapper.toResponse(enrollmentRepository.save(enrollment));
    }

    @Transactional
    @NonNull
    public EnrollmentResponse dropCourse(@NonNull String studentEmail, @NonNull Long courseId) {
        Student student = domainLookupService.getStudentByEmail(studentEmail);

        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(student.getId(), courseId)
                                        .orElseThrow(() -> new EntityNotFoundException("Active enrollment not found for this course."));

        Assert.isTrue(enrollment.getStatus() != EnrollmentStatus.DROPPED);

        enrollment.setStatus(EnrollmentStatus.DROPPED);

        notificationDispatcher.dispatchCourseNotification(
                student.getId(),
                enrollment.getCourse().getTitle(),
                "You have been dropped from the course.",
                "/users/me/enrollments"
        );

        return enrollmentMapper.toResponse(enrollmentRepository.save(enrollment));
    }

    @Transactional(readOnly = true)
    @NonNull
    public List<EnrollmentResponse> getMyActiveEnrollments(@NonNull String studentEmail) {
        Student student = domainLookupService.getStudentByEmail(studentEmail);

        return enrollmentRepository.findByStudentIdAndStatus(student.getId(), EnrollmentStatus.ENROLLED)
                       .stream()
                       .map(enrollmentMapper::toResponse)
                       .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @NonNull
    public Page<CourseRosterResponse> getCourseRoster(
            @NonNull String requestorEmail, @NonNull Long courseId, @NonNull Pageable pageable) {

        User requestor = domainLookupService.getAuthor(requestorEmail);
        Course course = domainLookupService.getCourse(courseId);

        securityManager.verifyProfessorAuthorization(requestor, course);

        return enrollmentRepository.findByCourseId(courseId, pageable)
                       .map(enrollmentMapper::toRosterResponse);
    }

    @Transactional
    @NonNull
    public CourseRosterResponse assignFinalGrade(
            @NonNull String requestorEmail, @NonNull Long courseId, @NonNull Long enrollmentId, @NonNull FinalGradeRequest request) {

        User requestor = domainLookupService.getAuthor(requestorEmail);
        Enrollment enrollment = domainLookupService.getEnrollment(enrollmentId);

        Assert.isTrue(enrollment.getCourse().getId().equals(courseId), "Enrollment does not belong to this course.");
        securityManager.verifyProfessorAuthorization(requestor, enrollment.getCourse());

        enrollment.setFinalScore(request.finalScore());
        enrollment.setFinalGrade(request.finalGrade());

        notificationDispatcher.dispatchCourseNotification(
                enrollment.getStudent().getId(),
                enrollment.getCourse().getTitle(),
                "Your final grade has been posted: " + request.finalGrade(),
                "/users/me/grades"
        );

        return enrollmentMapper.toRosterResponse(enrollmentRepository.save(enrollment));
    }
}