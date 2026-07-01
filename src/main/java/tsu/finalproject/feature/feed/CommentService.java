package tsu.finalproject.feature.feed;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tsu.finalproject.common.manager.DomainLookupService;
import tsu.finalproject.feature.course.security.CourseSecurityManager;
import tsu.finalproject.feature.feed.dto.CommentRequest;
import tsu.finalproject.feature.feed.dto.CommentResponse;
import tsu.finalproject.feature.feed.entity.Comment;
import tsu.finalproject.feature.feed.entity.Post;
import tsu.finalproject.feature.feed.repository.CommentRepository;
import tsu.finalproject.feature.notification.NotificationDispatcher;
import tsu.finalproject.feature.user.entity.User;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final DomainLookupService domainLookupService;
    private final CourseSecurityManager securityManager;
    private final CommentMapper commentMapper;
    private final NotificationDispatcher notificationDispatcher;

    @Transactional
    @NonNull
    public CommentResponse addComment(
            @NonNull String requestorEmail, @NonNull Long courseId, @NonNull Long postId, @NonNull CommentRequest request) {

        User requestor = domainLookupService.getAuthor(requestorEmail);
        Post post = domainLookupService.getPost(postId);

        securityManager.verifyPostBelongsToCourse(post, courseId);

        CourseSecurityManager.AccessLevel access = securityManager.determineAccessLevel(requestor, post.getCourse());
        if (access == CourseSecurityManager.AccessLevel.GUEST) {
            throw new AccessDeniedException("You do not have access to comment on this post.");
        }

        Comment comment = Comment.builder()
                                  .post(post)
                                  .author(requestor)
                                  .body(request.body())
                                  .build();

        comment = commentRepository.save(comment);

        if (!post.getAuthor().getId().equals(requestor.getId())) {
            String targetUrl = String.format("/courses/%d/posts/%d", courseId, postId);
            notificationDispatcher.dispatchCourseNotification(
                    post.getAuthor().getId(),
                    post.getCourse().getTitle(),
                    requestor.getFirstName() + " " + requestor.getLastName() + " commented on your post: '" + post.getTitle() + "'",
                    targetUrl
            );
        }

        return commentMapper.toResponse(comment);
    }

    @Transactional(readOnly = true)
    @NonNull
    public Page<CommentResponse> getComments(
            @NonNull String requestorEmail, @NonNull Long courseId, @NonNull Long postId, @NonNull Pageable pageable) {

        User requestor = domainLookupService.getAuthor(requestorEmail);
        Post post = domainLookupService.getPost(postId);

        securityManager.verifyPostBelongsToCourse(post, courseId);

        CourseSecurityManager.AccessLevel access = securityManager.determineAccessLevel(requestor, post.getCourse());
        if (access == CourseSecurityManager.AccessLevel.GUEST) {
            throw new AccessDeniedException("You do not have access to view comments on this post.");
        }

        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId, pageable)
                       .map(commentMapper::toResponse);
    }

    @Transactional
    @NonNull
    public CommentResponse updateComment(
            @NonNull String requestorEmail, @NonNull Long courseId, @NonNull Long postId, @NonNull Long commentId, @NonNull CommentRequest request) {

        User requestor = domainLookupService.getAuthor(requestorEmail);
        Comment comment = commentRepository.findById(commentId)
                                  .orElseThrow(() -> new EntityNotFoundException("Comment not found."));

        securityManager.verifyPostBelongsToCourse(comment.getPost(), courseId);

        if (!comment.getPost().getId().equals(postId)) {
            throw new IllegalArgumentException("Comment does not belong to the specified post.");
        }

        if (!comment.getAuthor().getId().equals(requestor.getId())) {
            throw new AccessDeniedException("Only the comment author can modify it.");
        }

        comment.setBody(request.body());
        return commentMapper.toResponse(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(
            @NonNull String requestorEmail, @NonNull Long courseId, @NonNull Long postId, @NonNull Long commentId) {

        User requestor = domainLookupService.getAuthor(requestorEmail);
        Comment comment = commentRepository.findById(commentId)
                                  .orElseThrow(() -> new EntityNotFoundException("Comment not found."));

        securityManager.verifyPostBelongsToCourse(comment.getPost(), courseId);

        if (!comment.getPost().getId().equals(postId)) {
            throw new IllegalArgumentException("Comment does not belong to the specified post.");
        }

        securityManager.verifyCommentDeletionAccess(requestor, comment, comment.getPost().getCourse());

        commentRepository.delete(comment);
    }
}