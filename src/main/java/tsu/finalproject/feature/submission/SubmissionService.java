package tsu.finalproject.feature.submission;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import tsu.finalproject.common.manager.DomainLookupService;
import tsu.finalproject.feature.course.security.CourseSecurityManager;
import tsu.finalproject.feature.feed.entity.*;
import tsu.finalproject.feature.storage.AttachmentManager;
import tsu.finalproject.feature.submission.dto.*;
import tsu.finalproject.feature.submission.entity.QuizAnswer;
import tsu.finalproject.feature.submission.entity.Submission;
import tsu.finalproject.feature.submission.repository.QuizAnswerRepository;
import tsu.finalproject.feature.submission.repository.SubmissionRepository;
import tsu.finalproject.feature.user.entity.Student;
import tsu.finalproject.feature.user.entity.User;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final QuizAnswerRepository quizAnswerRepository;
    private final DomainLookupService domainLookupService;
    private final CourseSecurityManager securityManager;
    private final AttachmentManager attachmentManager;
    private final QuizGradingService quizGradingService;
    private final SubmissionMapper submissionMapper;
    private final Clock clock;

    @Transactional
    @NonNull
    public SubmissionResponse submitAssignment(
            @NonNull String studentEmail, @NonNull Long courseId, @NonNull Long postId, @NonNull AssignmentSubmissionRequest request) {

        Student student = domainLookupService.getStudentByEmail(studentEmail);
        Post post = getValidatedPostForSubmission(student, courseId, postId);

        Assert.isTrue(post instanceof Assignment, "Target post is not an assignment.");

        Submission submission = Submission.builder()
                                        .student(student)
                                        .post(post)
                                        .attachments(attachmentManager.fetchAttachments(request.attachmentKeys()))
                                        .build();

        return submissionMapper.toResponse(submissionRepository.save(submission));
    }

    @Transactional
    @NonNull
    public SubmissionResponse submitQuiz(
            @NonNull String studentEmail, @NonNull Long courseId, @NonNull Long postId, @NonNull QuizSubmissionRequest request) {

        Student student = domainLookupService.getStudentByEmail(studentEmail);
        Post post = getValidatedPostForSubmission(student, courseId, postId);

        Assert.isTrue(post instanceof Quiz, "Target post is not a quiz.");
        Quiz quiz = (Quiz) post;

        Submission submission = Submission.builder()
                                        .student(student)
                                        .post(quiz)
                                        .build();

        Submission savedSubmission = submissionRepository.save(submission);

        QuizGradingService.GradingResult result = quizGradingService.evaluateAndSaveAnswers(quiz, savedSubmission, request);

        if (!result.requiresManualGrading()) {
            savedSubmission.setScore(result.totalScore());
            savedSubmission.setGradedAt(LocalDateTime.now(clock));
            savedSubmission = submissionRepository.save(savedSubmission);
        }

        return submissionMapper.toResponse(savedSubmission, result.answers());
    }

    @Transactional
    @NonNull
    public SubmissionResponse gradeSubmission(
            @NonNull String professorEmail, @NonNull Long courseId, @NonNull Long submissionId, @NonNull GradeSubmissionRequest request) {

        User professor = domainLookupService.getAuthor(professorEmail);
        Submission submission = submissionRepository.findById(submissionId)
                                        .orElseThrow(() -> new EntityNotFoundException("Submission not found"));

        securityManager.verifyPostBelongsToCourse(submission.getPost(), courseId);
        securityManager.verifyProfessorAuthorization(professor, submission.getPost().getCourse());

        submission.setScore(request.score());
        submission.setProfessorFeedback(request.professorFeedback());
        submission.setGradedAt(LocalDateTime.now(clock));

        Submission savedSubmission = submissionRepository.save(submission);

        if (savedSubmission.getPost() instanceof Quiz) {
            List<QuizAnswer> answers = quizAnswerRepository.findBySubmissionId(savedSubmission.getId());
            return submissionMapper.toResponse(savedSubmission, answers);
        }

        return submissionMapper.toResponse(savedSubmission);
    }

    @Transactional(readOnly = true)
    @NonNull
    public SubmissionResponse getMySubmission(@NonNull String studentEmail, @NonNull Long courseId, @NonNull Long postId) {
        Student student = domainLookupService.getStudentByEmail(studentEmail);
        Submission submission = submissionRepository.findByStudentIdAndPostId(student.getId(), postId)
                                        .orElseThrow(() -> new EntityNotFoundException("No submission found for this post."));

        securityManager.verifyPostBelongsToCourse(submission.getPost(), courseId);

        if (submission.getPost() instanceof Quiz) {
            List<QuizAnswer> answers = quizAnswerRepository.findBySubmissionId(submission.getId());
            return submissionMapper.toResponse(submission, answers);
        }

        return submissionMapper.toResponse(submission);
    }

    @Transactional(readOnly = true)
    @NonNull
    public Page<SubmissionResponse> getSubmissionsForPost(
            @NonNull String professorEmail, @NonNull Long courseId, @NonNull Long postId,
            Boolean needsGrading, @NonNull Pageable pageable) {

        User professor = domainLookupService.getAuthor(professorEmail);
        Post post = domainLookupService.getPost(postId);

        securityManager.verifyPostBelongsToCourse(post, courseId);
        securityManager.verifyProfessorAuthorization(professor, post.getCourse());

        Page<Submission> submissions;

        if (Boolean.TRUE.equals(needsGrading)) {
            // Fetch only submissions awaiting manual grading
            submissions = submissionRepository.findByPostIdAndScoreIsNull(postId, pageable);
        } else {
            submissions = submissionRepository.findByPostId(postId, pageable);
        }

        return submissions.map(submissionMapper::toResponse);
    }

    private Post getValidatedPostForSubmission(Student student, Long courseId, Long postId) {
        Post post = domainLookupService.getPost(postId);
        securityManager.verifyPostBelongsToCourse(post, courseId);

        CourseSecurityManager.AccessLevel access = securityManager.determineAccessLevel(student, post.getCourse());
        Assert.isTrue(access == CourseSecurityManager.AccessLevel.ENROLLED, "You must be enrolled to submit.");

        Assert.isTrue(submissionRepository.findByStudentIdAndPostId(student.getId(), postId).isEmpty(),
                "A submission for this post already exists.");

        return post;
    }
}