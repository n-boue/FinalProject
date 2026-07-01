package tsu.finalproject.feature.feed;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tsu.finalproject.common.manager.DomainLookupService;
import tsu.finalproject.feature.course.security.CourseSecurityManager;
import tsu.finalproject.feature.feed.dto.*;
import tsu.finalproject.feature.feed.entity.*;
import tsu.finalproject.feature.feed.enums.PostType;
import tsu.finalproject.feature.feed.enums.PostVisibility;
import tsu.finalproject.feature.feed.repository.PostRepository;
import tsu.finalproject.feature.storage.AttachmentManager;
import tsu.finalproject.feature.storage.event.FileDeletionEvent;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final DomainLookupService domainLookupService;
    private final CourseSecurityManager securityManager;
    private final AttachmentManager attachmentManager;
    private final PostMapper postMapper;

    
    @Transactional
    @NonNull
    public PostResponse createAnnouncement(@NonNull String authorEmail, @NonNull Long courseId, @NonNull AnnouncementRequest request) {
        var course = domainLookupService.getCourse(courseId);
        var author = domainLookupService.getAuthor(authorEmail);

        securityManager.verifyProfessorAuthorization(author, course);

        var announcement = Material.builder()
                                   .course(course)
                                   .author(author)
                                   .title(request.title())
                                   .body(request.body())
                                   .week(request.week())
                                   .visibility(request.visibility())
                                   .attachments(attachmentManager.fetchAttachments(request.attachmentKeys()))
                                   .build();

        return postMapper.toResponse(postRepository.save(announcement));
    }

    
    @Transactional
    @NonNull
    public PostResponse createMaterial(@NonNull String authorEmail, @NonNull Long courseId, @NonNull MaterialRequest request) {
        var course = domainLookupService.getCourse(courseId);
        var author = domainLookupService.getAuthor(authorEmail);

        securityManager.verifyProfessorAuthorization(author, course);

        var material = Material.builder()
                               .course(course)
                               .author(author)
                               .title(request.title())
                               .body(request.body())
                               .week(request.week())
                               .visibility(request.visibility())
                               .attachments(attachmentManager.fetchAttachments(request.attachmentKeys()))
                               .build();

        return postMapper.toResponse(postRepository.save(material));
    }

    
    @Transactional
    @NonNull
    public PostResponse createAssignment(@NonNull String authorEmail, @NonNull Long courseId, @NonNull AssignmentRequest request) {
        var course = domainLookupService.getCourse(courseId);
        var author = domainLookupService.getAuthor(authorEmail);

        securityManager.verifyProfessorAuthorization(author, course);

        var assignment = Assignment.builder()
                                 .course(course)
                                 .author(author)
                                 .title(request.title())
                                 .body(request.body())
                                 .week(request.week())
                                 .visibility(request.visibility())
                                 .attachments(attachmentManager.fetchAttachments(request.attachmentKeys()))
                                 .dueDate(request.dueDate())
                                 .maxPoints(request.maxPoints())
                                 .build();

        return postMapper.toResponse(postRepository.save(assignment));
    }
    
    @Transactional
    @NonNull
    public PostResponse createQuiz(@NonNull String authorEmail, @NonNull Long courseId, @NonNull QuizRequest request) {
        var course = domainLookupService.getCourse(courseId);
        var author = domainLookupService.getAuthor(authorEmail);

        securityManager.verifyProfessorAuthorization(author, course);

        Quiz quiz = Quiz.builder()
                            .course(course)
                            .author(author)
                            .title(request.title())
                            .body(request.body())
                            .week(request.week())
                            .visibility(request.visibility())
                            .attachments(attachmentManager.fetchAttachments(request.attachmentKeys()))
                            .dueDate(request.dueDate())
                            .timeLimitMinutes(request.timeLimitMinutes())
                            .passingScore(request.passingScore())
                            .shuffleQuestions(request.shuffleQuestions())
                            .build();

        List<QuizQuestion> quizQuestions = request.questions().stream()
                                                   .map(qReq -> {
                                                       QuizQuestion question = postMapper.toEntity(qReq);
                                                       question.setQuiz(quiz);
                                                       return question;
                                                   }).toList();

        quiz.setQuestions(quizQuestions);

        return postMapper.toResponse(postRepository.save(quiz));
    }
    
    @Transactional(readOnly = true)
    @NonNull
    public Page<PostResponse> getCoursePosts(@NonNull String requestorEmail, @NonNull Long courseId, @NonNull Pageable pageable) {
        var course = domainLookupService.getCourse(courseId);
        var requestor = domainLookupService.getAuthor(requestorEmail);

        CourseSecurityManager.AccessLevel access = securityManager.determineAccessLevel(requestor, course);

        Page<Post> postPage = switch (access) {
            case FACULTY -> postRepository.findByCourseIdOrderByCreatedAtDesc(courseId, pageable);
            case ENROLLED -> postRepository.findByCourseIdAndVisibilityNotOrderByCreatedAtDesc(
                    courseId, PostVisibility.FACULTY_ONLY, pageable);
            case GUEST -> postRepository.findByCourseIdAndVisibilityOrderByCreatedAtDesc(
                    courseId, PostVisibility.PUBLIC, pageable);
        };

        return postPage.map(postMapper::toResponse);
    }

    @Transactional(readOnly = true)
    @NonNull
    public Page<PostResponse> getCoursePostsByType(
            @NonNull String requestorEmail, @NonNull Long courseId, @NonNull PostType type, @NonNull Pageable pageable) {
        var course = domainLookupService.getCourse(courseId);
        var requestor = domainLookupService.getAuthor(requestorEmail);

        CourseSecurityManager.AccessLevel access = securityManager.determineAccessLevel(requestor, course);

        Page<Post> postPage = switch (access) {
            case FACULTY -> postRepository.findByCourseIdAndTypeOrderByCreatedAtDesc(courseId, type, pageable);
            case ENROLLED -> postRepository.findByCourseIdAndVisibilityNotAndTypeOrderByCreatedAtDesc(
                    courseId, PostVisibility.FACULTY_ONLY, type, pageable);
            case GUEST -> postRepository.findByCourseIdAndVisibilityAndTypeOrderByCreatedAtDesc(
                    courseId, PostVisibility.PUBLIC, type, pageable);
        };

        return postPage.map(postMapper::toResponse);
    }

    
    @Transactional
    public void deletePost(@NonNull String requestorEmail, @NonNull Long courseId, @NonNull Long postId) {
        Post post = domainLookupService.getPost(postId);
        securityManager.verifyPostBelongsToCourse(post, courseId);

        var requestor = domainLookupService.getAuthor(requestorEmail);
        securityManager.verifyProfessorAuthorization(requestor, post.getCourse());

        if (post.getAttachments() != null) {
            post.getAttachments().forEach(attachment ->
                                                  eventPublisher.publishEvent(new FileDeletionEvent(attachment.getObjectKey()))
            );
        }

        postRepository.delete(post);
    }

    
    @Transactional
    @NonNull
    public PostResponse updateAnnouncement(
            @NonNull String requestorEmail, @NonNull Long courseId, @NonNull Long postId, @NonNull AnnouncementRequest request) {
        Post post = getVerifiedPost(requestorEmail, courseId, postId);

        if (!(post instanceof Announcement announcement)) {
            throw new IllegalArgumentException("This post is not an announcement.");
        }

        announcement.setTitle(request.title());
        announcement.setBody(request.body());
        announcement.setWeek(request.week());
        announcement.setVisibility(request.visibility());

        attachmentManager.syncAttachments(announcement, request.attachmentKeys());

        return postMapper.toResponse(postRepository.save(announcement));
    }

    
    @Transactional
    @NonNull
    public PostResponse updateMaterial(
            @NonNull String requestorEmail, @NonNull Long courseId, @NonNull Long postId, @NonNull MaterialRequest request) {
        Post post = getVerifiedPost(requestorEmail, courseId, postId);

        if (!(post instanceof Material material)) {
            throw new IllegalArgumentException("This post is not course material.");
        }

        material.setTitle(request.title());
        material.setBody(request.body());
        material.setWeek(request.week());
        material.setVisibility(request.visibility());

        attachmentManager.syncAttachments(material, request.attachmentKeys());

        return postMapper.toResponse(postRepository.save(material));
    }

    
    @Transactional
    @NonNull
    public PostResponse updateAssignment(
            @NonNull String requestorEmail, @NonNull Long courseId, @NonNull Long postId, @NonNull AssignmentRequest request) {
        Post post = getVerifiedPost(requestorEmail, courseId, postId);

        if (!(post instanceof Assignment assignment)) {
            throw new IllegalArgumentException("This post is not an assignment.");
        }

        assignment.setTitle(request.title());
        assignment.setBody(request.body());
        assignment.setWeek(request.week());
        assignment.setVisibility(request.visibility());
        assignment.setDueDate(request.dueDate());
        assignment.setMaxPoints(request.maxPoints());

        attachmentManager.syncAttachments(assignment, request.attachmentKeys());

        return postMapper.toResponse(postRepository.save(assignment));
    }

    
    @Transactional
    @NonNull
    public PostResponse updateQuiz(
            @NonNull String requestorEmail, @NonNull Long courseId, @NonNull Long postId, @NonNull QuizRequest request) {
        Post post = getVerifiedPost(requestorEmail, courseId, postId);

        if (!(post instanceof Quiz quiz)) {
            throw new IllegalArgumentException("This post is not a quiz.");
        }

        quiz.setTitle(request.title());
        quiz.setBody(request.body());
        quiz.setWeek(request.week());
        quiz.setVisibility(request.visibility());
        quiz.setDueDate(request.dueDate());
        quiz.setTimeLimitMinutes(request.timeLimitMinutes());
        quiz.setPassingScore(request.passingScore());
        quiz.setShuffleQuestions(request.shuffleQuestions());

        attachmentManager.syncAttachments(quiz, request.attachmentKeys());

        quiz.getQuestions().clear();
        List<QuizQuestion> newQuestions = request.questions().stream()
                                                  .map(qReq -> {
                                                      QuizQuestion question = postMapper.toEntity(qReq);
                                                      question.setQuiz(quiz);
                                                      return question;
                                                  }).toList();

        quiz.getQuestions().addAll(newQuestions);

        return postMapper.toResponse(postRepository.save(quiz));
    }

    private Post getVerifiedPost(String requestorEmail, Long courseId, Long postId) {
        Post post = domainLookupService.getPost(postId);
        securityManager.verifyPostBelongsToCourse(post, courseId);

        var requestor = domainLookupService.getAuthor(requestorEmail);
        securityManager.verifyPostModificationAccess(requestor, post);

        return post;
    }
}