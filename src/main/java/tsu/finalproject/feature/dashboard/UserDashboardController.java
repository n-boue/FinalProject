package tsu.finalproject.feature.dashboard;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tsu.finalproject.feature.course.dto.CourseResponse;
import tsu.finalproject.feature.dashboard.dto.GradeRecordResponse;
import tsu.finalproject.feature.dashboard.dto.NotificationResponse;
import tsu.finalproject.feature.dashboard.dto.PendingTaskResponse;
import tsu.finalproject.feature.dashboard.dto.ScheduleResponse;
import tsu.finalproject.feature.enrollment.dto.EnrollmentResponse;
import tsu.finalproject.feature.submission.dto.SubmissionResponse;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/users/me")
@RequiredArgsConstructor
public class UserDashboardController {

    private final UserDashboardService userDashboardService;

    @GetMapping("/enrollments")
    @PreAuthorize("hasRole('STUDENT')")
    @NonNull
    public List<EnrollmentResponse> getMyActiveEnrollments(Principal principal) {
        return userDashboardService.getActiveEnrollments(principal.getName());
    }

    @GetMapping("/grades")
    @PreAuthorize("hasRole('STUDENT')")
    @NonNull
    public List<GradeRecordResponse> getMyGrades(Principal principal) {
        return userDashboardService.getGrades(principal.getName());
    }

    @GetMapping("/submissions")
    @PreAuthorize("hasRole('STUDENT')")
    @NonNull
    public Page<SubmissionResponse> getMySubmissions(Principal principal, Pageable pageable) {
        return userDashboardService.getSubmissions(principal.getName(), pageable);
    }

    @GetMapping("/schedule")
    @PreAuthorize("hasRole('STUDENT') or hasRole('PROF')")
    @NonNull
    public ScheduleResponse getMySchedule(Principal principal) {
        return userDashboardService.getSchedule(principal.getName());
    }

    @GetMapping("/tasks/pending")
    @PreAuthorize("hasRole('STUDENT')")
    @NonNull
    public List<PendingTaskResponse> getMyPendingTasks(Principal principal) {
        return userDashboardService.getPendingTasks(principal.getName());
    }

    @GetMapping("/notifications")
    @NonNull
    public Page<NotificationResponse> getMyNotifications(
            @RequestParam(required = false) Boolean unreadOnly,
            Pageable pageable,
            Principal principal
    ) {
        return userDashboardService.getNotifications(principal.getName(), unreadOnly, pageable);
    }

    @PatchMapping("/notifications/{notificationId}/read")
    @NonNull
    public NotificationResponse markNotificationAsRead(
            @PathVariable @NonNull Long notificationId,
            Principal principal
    ) {
        return userDashboardService.markNotificationAsRead(principal.getName(), notificationId);
    }

    @PutMapping("/notifications/read-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAllNotificationsAsRead(Principal principal) {
        userDashboardService.markAllNotificationsAsRead(principal.getName());
    }

    @GetMapping("/courses")
    @PreAuthorize("hasRole('STUDENT') or hasRole('PROF')")
    @NonNull
    public List<CourseResponse> getMyCourses(Principal principal) {
        return userDashboardService.getCourses(principal.getName());
    }
}