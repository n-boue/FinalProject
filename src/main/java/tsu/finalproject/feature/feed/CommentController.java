package tsu.finalproject.feature.feed;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tsu.finalproject.feature.feed.dto.CommentRequest;
import tsu.finalproject.feature.feed.dto.CommentResponse;

import java.security.Principal;

@RestController
@RequestMapping("${api.prefix}/courses/{courseId}/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @NonNull
    public CommentResponse addComment(
            @PathVariable @NonNull Long courseId,
            @PathVariable @NonNull Long postId,
            @RequestBody @Valid CommentRequest request,
            Principal principal
    ) {
        return commentService.addComment(principal.getName(), courseId, postId, request);
    }

    @GetMapping
    @NonNull
    public Page<CommentResponse> getComments(
            @PathVariable @NonNull Long courseId,
            @PathVariable @NonNull Long postId,
            Pageable pageable,
            Principal principal
    ) {
        return commentService.getComments(principal.getName(), courseId, postId, pageable);
    }

    @PutMapping("/{commentId}")
    @NonNull
    public CommentResponse updateComment(
            @PathVariable @NonNull Long courseId,
            @PathVariable @NonNull Long postId,
            @PathVariable @NonNull Long commentId,
            @RequestBody @Valid CommentRequest request,
            Principal principal
    ) {
        return commentService.updateComment(principal.getName(), courseId, postId, commentId, request);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @PathVariable @NonNull Long courseId,
            @PathVariable @NonNull Long postId,
            @PathVariable @NonNull Long commentId,
            Principal principal
    ) {
        commentService.deleteComment(principal.getName(), courseId, postId, commentId);
    }
}