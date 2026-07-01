package tsu.finalproject.common.manager;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tsu.finalproject.feature.course.entity.Course;
import tsu.finalproject.feature.course.entity.CourseSession;
import tsu.finalproject.feature.course.repository.CourseRepository;
import tsu.finalproject.feature.course.repository.CourseSessionRepository;
import tsu.finalproject.feature.feed.entity.Post;
import tsu.finalproject.feature.feed.repository.PostRepository;
import tsu.finalproject.feature.semester.Semester;
import tsu.finalproject.feature.semester.SemesterRepository;
import tsu.finalproject.feature.user.UserRepository;
import tsu.finalproject.feature.user.entity.Professor;
import tsu.finalproject.feature.user.entity.Student;
import tsu.finalproject.feature.user.entity.User;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DomainLookupService {

    private final CourseRepository courseRepository;
    private final CourseSessionRepository courseSessionRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final SemesterRepository semesterRepository;

    @NonNull
    public Course getCourse(@NonNull Long courseId) {
        return courseRepository.findById(courseId)
                       .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + courseId));
    }

    @NonNull
    public User getAuthor(@NonNull String email) {
        return userRepository.findByEmail(email)
                       .orElseThrow(() -> new EntityNotFoundException("Author not found for email: " + email));
    }

    @NonNull
    public Post getPost(@NonNull Long postId) {
        return postRepository.findById(postId)
                       .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));
    }

    @NonNull
    public Semester getSemester(@NonNull Long semesterId) {
        return semesterRepository.findById(semesterId)
                       .orElseThrow(() -> new EntityNotFoundException("Semester not found with ID: " + semesterId));
    }

    @NonNull
    public Professor getProfessor(@NonNull Long userId) {
        User user = userRepository.findById(userId)
                            .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        if (!(user instanceof Professor professor)) {
            throw new IllegalArgumentException("The assigned user is not a Professor.");
        }
        return professor;
    }

    @NonNull
    public Student getStudentByEmail(@NonNull String email) {
        User user = userRepository.findByEmail(email)
                            .orElseThrow(() -> new EntityNotFoundException("User not found for email: " + email));

        if (!(user instanceof Student student)) {
            throw new IllegalArgumentException("Only students can perform this action.");
        }
        return student;
    }

    @NonNull
    public List<CourseSession> getCourseSessions(@NonNull Set<Long> sessionIds) {
        List<CourseSession> sessions = courseSessionRepository.findAllById(sessionIds);
        if (sessions.size() != sessionIds.size()) {
            throw new IllegalArgumentException("One or more provided session IDs are invalid.");
        }
        return sessions;
    }

    @NonNull
    public User getUser(@NonNull Long id) {
        return userRepository.findById(id)
                       .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
    }

    @NonNull
    public User getUser(@NonNull String email) {
        return userRepository.findByEmail(email)
                       .orElseThrow(() -> new EntityNotFoundException("User not found for email: " + email));
    }
}