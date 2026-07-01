package tsu.finalproject.feature.dashboard;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tsu.finalproject.common.manager.DomainLookupService;
import tsu.finalproject.feature.course.CourseMapper;
import tsu.finalproject.feature.course.dto.CourseResponse;
import tsu.finalproject.feature.course.repository.CourseRepository;
import tsu.finalproject.feature.course.repository.CourseSessionRepository;
import tsu.finalproject.feature.dashboard.dto.*;
import tsu.finalproject.feature.dashboard.enums.ScheduleEventType;
import tsu.finalproject.feature.enrollment.Enrollment;
import tsu.finalproject.feature.enrollment.EnrollmentRepository;
import tsu.finalproject.feature.enrollment.EnrollmentService;
import tsu.finalproject.feature.enrollment.dto.EnrollmentResponse;
import tsu.finalproject.feature.enrollment.enums.EnrollmentStatus;
import tsu.finalproject.feature.feed.entity.Assignment;
import tsu.finalproject.feature.feed.entity.Post;
import tsu.finalproject.feature.feed.entity.Quiz;
import tsu.finalproject.feature.feed.repository.PostRepository;
import tsu.finalproject.feature.notification.NotificationService;
import tsu.finalproject.feature.submission.SubmissionMapper;
import tsu.finalproject.feature.submission.dto.SubmissionResponse;
import tsu.finalproject.feature.submission.repository.SubmissionRepository;
import tsu.finalproject.feature.user.entity.Professor;
import tsu.finalproject.feature.user.entity.Student;
import tsu.finalproject.feature.user.entity.User;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDashboardService {

    private final DomainLookupService domainLookupService;
    private final EnrollmentService enrollmentService;
    private final NotificationService notificationService;
    private final SubmissionRepository submissionRepository;
    private final PostRepository postRepository;
    private final CourseRepository courseRepository;
    private final CourseSessionRepository courseSessionRepository;
    private final EnrollmentRepository enrollmentRepository;

    private final DashboardMapper dashboardMapper;
    private final SubmissionMapper submissionMapper;
    private final CourseMapper courseMapper;
    private final Clock clock;

    @NonNull
    public List<EnrollmentResponse> getActiveEnrollments(@NonNull String email) {
        return enrollmentService.getMyActiveEnrollments(email);
    }

    @NonNull
    public List<GradeRecordResponse> getGrades(@NonNull String email) {
        Student student = domainLookupService.getStudentByEmail(email);

        return enrollmentRepository.findByStudentId(student.getId()).stream()
                       .map(enrollment -> new GradeRecordResponse(
                               enrollment.getCourse().getId(),
                               enrollment.getCourse().getTitle(),
                               enrollment.getCourse().getSemester().getId(),
                               enrollment.getCourse().getSemester().getName(),
                               enrollment.getFinalScore(),
                               enrollment.getFinalGrade()
                       ))
                       .collect(Collectors.toList());
    }

    @NonNull
    public Page<SubmissionResponse> getSubmissions(@NonNull String email, @NonNull Pageable pageable) {
        Student student = domainLookupService.getStudentByEmail(email);
        return submissionRepository.findByStudentIdOrderBySubmittedAtDesc(student.getId(), pageable)
                       .map(submissionMapper::toResponse);
    }

    @NonNull
    public ScheduleResponse getSchedule(@NonNull String email) {
        User user = domainLookupService.getAuthor(email);
        List<ScheduleEventResponse> events = new ArrayList<>();

        if (user instanceof Student student) {
            enrollmentRepository.findByStudentIdAndStatus(student.getId(), tsu.finalproject.feature.enrollment.enums.EnrollmentStatus.ENROLLED)
                    .stream()
                    .flatMap(e -> e.getSelectedSessions().stream())
                    .forEach(session -> events.add(dashboardMapper.toScheduleEvent(session)));

            getPendingTasks(email).forEach(task -> events.add(new ScheduleEventResponse(
                    task.courseId(),
                    task.courseTitle(),
                    task.taskTitle(),
                    ScheduleEventType.valueOf(task.type().name() + "_DUE"),
                    null, null, null, null,
                    task.dueDate()
            )));

        } else if (user instanceof Professor professor) {
            courseSessionRepository.findByProfessorId(professor.getId()).forEach(session ->
                                                                                         events.add(dashboardMapper.toScheduleEvent(session)));
        }

        return new ScheduleResponse(events);
    }

    @NonNull
    public List<PendingTaskResponse> getPendingTasks(@NonNull String email) {
        Student student = domainLookupService.getStudentByEmail(email);
        List<Post> pendingPosts = postRepository.findPendingTasksForStudent(student.getId(), LocalDateTime.now(clock));

        return pendingPosts.stream().map(post -> {
            Integer maxPoints = null;
            LocalDateTime dueDate = null;

            if (post instanceof Assignment assignment) {
                maxPoints = assignment.getMaxPoints();
                dueDate = assignment.getDueDate();
            } else if (post instanceof Quiz quiz) {
                maxPoints = quiz.getPassingScore();
                dueDate = quiz.getDueDate();
            }

            return new PendingTaskResponse(
                    post.getCourse().getId(),
                    post.getCourse().getTitle(),
                    post.getId(),
                    post.getTitle(),
                    post.getType(),
                    dueDate,
                    maxPoints != null ? maxPoints : 0
            );
        }).collect(Collectors.toList());
    }

    @NonNull
    public Page<NotificationResponse> getNotifications(@NonNull String email, Boolean unreadOnly, @NonNull Pageable pageable) {
        User user = domainLookupService.getAuthor(email);
        return notificationService.getUserNotifications(user.getId(), unreadOnly, pageable);
    }

    @Transactional
    @NonNull
    public NotificationResponse markNotificationAsRead(@NonNull String email, @NonNull Long notificationId) {
        User user = domainLookupService.getAuthor(email);
        return notificationService.markAsRead(user.getId(), notificationId);
    }

    @Transactional
    public void markAllNotificationsAsRead(@NonNull String email) {
        User user = domainLookupService.getAuthor(email);
        notificationService.markAllAsRead(user.getId());
    }

    @NonNull
    public List<CourseResponse> getCourses(@NonNull String email) {
        User user = domainLookupService.getAuthor(email);

        if (user instanceof Student student) {
            return enrollmentRepository.findByStudentIdAndStatus(student.getId(), EnrollmentStatus.ENROLLED)
                           .stream()
                           .map(Enrollment::getCourse)
                           .map(courseMapper::toResponse)
                           .collect(Collectors.toList());

        } else if (user instanceof Professor professor) {
            return courseRepository.findByLeadProfessorIdAndSemesterId(
                            professor.getId(), null, Pageable.unpaged())
                           .map(courseMapper::toResponse)
                           .toList();
        }

        return List.of();
    }
}