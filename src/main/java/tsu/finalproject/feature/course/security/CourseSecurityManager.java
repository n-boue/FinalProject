package tsu.finalproject.feature.course.security;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import tsu.finalproject.feature.course.entity.Course;
import tsu.finalproject.feature.enrollment.EnrollmentRepository;
import tsu.finalproject.feature.enrollment.enums.EnrollmentStatus;
import tsu.finalproject.feature.feed.entity.Comment;
import tsu.finalproject.feature.feed.entity.Post;
import tsu.finalproject.feature.submission.entity.Submission;
import tsu.finalproject.feature.user.entity.Professor;
import tsu.finalproject.feature.user.entity.Student;
import tsu.finalproject.feature.user.entity.User;
import tsu.finalproject.feature.user.enums.Role;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseSecurityManager {

    private final EnrollmentRepository enrollmentRepository;

    public void verifyProfessorAuthorization(@NonNull User user, @NonNull Course course) {
        if (user.getRole() == Role.ROLE_ADMIN) {
            return;
        }

        if (!(user instanceof Professor professor)) {
            throw new AccessDeniedException("Only professors can manage course posts.");
        }

        if (course.getLeadProfessor().getId().equals(professor.getId())) {
            return;
        }

        boolean isTeachingSession = course.getSections().stream()
                                            .flatMap(section -> section.getSessions().stream())
                                            .anyMatch(session -> session.getProfessor().getId().equals(professor.getId()));

        if (!isTeachingSession) {
            throw new AccessDeniedException("You are not assigned to this course and cannot modify its feed.");
        }
    }

    @NonNull
    public AccessLevel determineAccessLevel(@NonNull User user, @NonNull Course course) {
        if (user.getRole() == Role.ROLE_ADMIN) {
            return AccessLevel.FACULTY;
        }

        if (user instanceof Professor professor) {
            boolean isLead = course.getLeadProfessor().getId().equals(professor.getId());
            boolean isTeaching = course.getSections().stream()
                                         .flatMap(section -> section.getSessions().stream())
                                         .anyMatch(session -> session.getProfessor().getId().equals(professor.getId()));

            return (isLead || isTeaching) ? AccessLevel.FACULTY : AccessLevel.GUEST;
        }

        if (user instanceof Student student) {
            boolean isEnrolled = enrollmentRepository.findByStudentIdAndCourseId(student.getId(), course.getId())
                                         .map(e -> e.getStatus() == EnrollmentStatus.ENROLLED)
                                         .orElse(false);

            return isEnrolled ? AccessLevel.ENROLLED : AccessLevel.GUEST;
        }

        return AccessLevel.GUEST;
    }

    public void verifyPostModificationAccess(@NonNull User requestor, @NonNull Post post) {
        if (requestor.getRole() != Role.ROLE_ADMIN) {
            if (!post.getAuthor().getId().equals(requestor.getId())) {
                throw new AccessDeniedException("Access denied: Only the original author can modify this post.");
            }
        }
    }

    public void verifySubmissionModificationAccess(@NonNull User requestor, @NonNull Submission submission) {
        if (requestor.getRole() != Role.ROLE_ADMIN) {
            if (!submission.getStudent().getId().equals(requestor.getId())) {
                throw new AccessDeniedException("Access denied: Only the submitting student can modify this submission.");
            }
        }
    }

    public void verifyPostBelongsToCourse(@NonNull Post post, @NonNull Long courseId) {
        Assert.isTrue(post.getCourse().getId().equals(courseId),
                "This post does not belong to the specified course.");
    }

    public void verifySubmissionBelongsToPost(@NonNull Post post, @NonNull Submission submission) {
        Assert.isTrue(submission.getPost().equals(post),
                "This submission does not belong to the given post.");
    }

    public void verifyCommentDeletionAccess(@NonNull User requestor, @NonNull Comment comment, @NonNull Course course) {
        if (requestor.getRole() == Role.ROLE_ADMIN) return;

        if (comment.getAuthor().getId().equals(requestor.getId())) return;

        verifyProfessorAuthorization(requestor, course);
    }

    public enum AccessLevel {
        FACULTY, ENROLLED, GUEST
    }
}