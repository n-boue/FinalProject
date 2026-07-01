package tsu.finalproject.feature.submission;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import tsu.finalproject.common.manager.DomainLookupService;
import tsu.finalproject.feature.course.security.CourseSecurityManager;
import tsu.finalproject.feature.feed.entity.Assignment;
import tsu.finalproject.feature.feed.entity.Post;
import tsu.finalproject.feature.feed.entity.Quiz;
import tsu.finalproject.feature.notification.NotificationDispatcher;
import tsu.finalproject.feature.storage.Attachment;
import tsu.finalproject.feature.storage.AttachmentManager;
import tsu.finalproject.feature.storage.dto.AttachmentResponse;
import tsu.finalproject.feature.submission.dto.AssignmentSubmissionRequest;
import tsu.finalproject.feature.submission.dto.GradeSubmissionRequest;
import tsu.finalproject.feature.submission.dto.QuizSubmissionRequest;
import tsu.finalproject.feature.submission.dto.SubmissionResponse;
import tsu.finalproject.feature.submission.entity.QuizAnswer;
import tsu.finalproject.feature.submission.entity.Submission;
import tsu.finalproject.feature.submission.event.SubmissionGradedEvent;
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
    private final NotificationDispatcher notificationDispatcher;
    private final ApplicationEventPublisher eventPublisher;
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

            eventPublisher.publishEvent(new SubmissionGradedEvent(student.getId(), courseId));

            notificationDispatcher.dispatchCourseNotification(
                    student.getId(),
                    quiz.getCourse().getTitle(),
                    "Your quiz '" + quiz.getTitle() + "' was auto-graded. Score: " + savedSubmission.getScore(),
                    String.format("/courses/%d/posts/%d/submissions/me", courseId, postId)
            );
        }

        return submissionMapper.toResponse(savedSubmission, result.answers());
    }

    @Transactional
    @NonNull
    public SubmissionResponse gradeSubmission(
            @NonNull String professorEmail, @NonNull Long courseId, @NonNull Long submissionId, @NonNull GradeSubmissionRequest request) {

        User professor = domainLookupService.getAuthor(professorEmail);
        Submission submission = domainLookupService.getSubmission(submissionId);

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

        eventPublisher.publishEvent(new SubmissionGradedEvent(submission.getStudent().getId(), courseId));

        notificationDispatcher.dispatchCourseNotification(
                submission.getStudent().getId(),
                submission.getPost().getCourse().getTitle(),
                "Your submission for '" + submission.getPost().getTitle() + "' has been graded.",
                String.format("/courses/%d/posts/%d/submissions/me", courseId, submission.getPost().getId())
        );

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

    @Transactional
    @NonNull
    public AttachmentResponse addAttachmentToSubmission(
            @NonNull String studentEmail, @NonNull Long courseId, @NonNull Long postId, @NonNull Long submissionId, @NonNull MultipartFile file) {

        Submission submission = domainLookupService.getSubmission(submissionId);

        securityManager.verifyPostBelongsToCourse(submission.getPost(), courseId);
        Assert.isTrue(submission.getPost().getId().equals(postId), "Submission does not belong to the specified post.");

        User student = domainLookupService.getStudentByEmail(studentEmail);
        securityManager.verifySubmissionModificationAccess(student, submission);

        Attachment attachment = attachmentManager.uploadAndRegisterAttachment(file, "submissions/" + submissionId, student);

        submission.getAttachments().add(attachment);
        submissionRepository.save(submission);

        return new AttachmentResponse(
                attachment.getId(),
                attachment.getOriginalFileName(),
                attachmentManager.getAttachmentUrl(attachment),
                attachment.getSizeBytes()
        );
    }

    @Transactional
    public void removeAttachmentFromSubmission(
            @NonNull String studentEmail, @NonNull Long courseId, @NonNull Long postId, @NonNull Long submissionId, @NonNull Long attachmentId) {

        Submission submission = domainLookupService.getSubmission(submissionId);

        securityManager.verifyPostBelongsToCourse(submission.getPost(), courseId);
        Assert.isTrue(submission.getPost().getId().equals(postId), "Submission does not belong to the specified post.");

        User student = domainLookupService.getStudentByEmail(studentEmail);
        securityManager.verifySubmissionModificationAccess(student, submission);

        Attachment attachment = submission.getAttachments().stream()
                                        .filter(a -> a.getId().equals(attachmentId))
                                        .findFirst()
                                        .orElseThrow(() -> new EntityNotFoundException("Attachment not found on this submission."));

        submission.getAttachments().remove(attachment);
        attachmentManager.publishDeletionEvent(attachment);
    }
}