package tsu.finalproject.feature.enrollment;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import tsu.finalproject.feature.course.entity.Course;
import tsu.finalproject.feature.course.entity.CourseSection;
import tsu.finalproject.feature.course.entity.CourseSession;
import tsu.finalproject.feature.semester.Semester;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CourseEnrollmentValidator {

    private final Clock clock;

    public void validateEnrollment(@NonNull Course course, @NonNull List<CourseSession> requestedSessions) {
        if (course.isDeactivated()) {
            throw new IllegalArgumentException("Cannot enroll in a deactivated course.");
        }

        validateEnrollmentPeriod(course);
        validateSessionIntegrity(course, requestedSessions);
    }

    private void validateEnrollmentPeriod(Course course) {
        LocalDate today = LocalDate.now(clock);
        Semester semester = course.getSemester();

        Assert.notNull(semester.getEnrollmentStartDate(), "Enrollment start date cannot be null.");
        Assert.notNull(semester.getEnrollmentEndDate(), "Enrollment end date cannot be null.");

        Assert.isTrue(!today.isBefore(semester.getEnrollmentStartDate()),
                "We are currently outside the allowed enrollment period for this semester.");
        Assert.isTrue(!today.isAfter(semester.getEnrollmentEndDate()),
                "We are currently outside the allowed enrollment period for this semester.");
    }

    private void validateSessionIntegrity(Course course, List<CourseSession> requestedSessions) {
        Set<Long> requiredSectionIds = course.getSections().stream()
                                               .map(CourseSection::getId)
                                               .collect(Collectors.toSet());

        Assert.notEmpty(requestedSessions,
                "This course has no sections defined yet. Cannot enroll.");

        Map<Long, List<CourseSession>> sessionsBySection = requestedSessions.stream()
                                                                   .collect(Collectors.groupingBy(session -> session.getSection().getId()));

        for (Long providedSectionId : sessionsBySection.keySet()) {
            if (!requiredSectionIds.contains(providedSectionId)) {
                throw new IllegalArgumentException("Session provided does not belong to this course.");
            }
        }

        for (Long sectionId : requiredSectionIds) {
            List<CourseSession> sessionsForThisSection = sessionsBySection.get(sectionId);
            if (sessionsForThisSection == null || sessionsForThisSection.size() != 1) {
                throw new IllegalArgumentException("You must select exactly ONE session for each section in the course.");
            }
        }
    }
}