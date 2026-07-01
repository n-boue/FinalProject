package tsu.finalproject.feature.feed;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tsu.finalproject.feature.feed.dto.*;
import tsu.finalproject.feature.feed.enums.PostType;

import java.security.Principal;

@RestController
@RequestMapping("${api.prefix}/courses/{courseId}/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    @NonNull
    public Page<PostResponse> getAllPosts(
            @PathVariable @NonNull Long courseId,
            Pageable pageable,
            Principal principal
    ) {
        return postService.getCoursePosts(principal.getName(), courseId, pageable);
    }

    @GetMapping("/announcements")
    @NonNull
    public Page<PostResponse> getAnnouncements(
            @PathVariable @NonNull Long courseId,
            Pageable pageable,
            Principal principal
    ) {
        return postService.getCoursePostsByType(principal.getName(), courseId, PostType.ANNOUNCEMENT, pageable);
    }

    @PostMapping("/announcements")
    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @NonNull
    public PostResponse createAnnouncement(
            @PathVariable @NonNull Long courseId,
            @RequestBody @Valid AnnouncementRequest request,
            Principal principal
    ) {
        return postService.createAnnouncement(principal.getName(), courseId, request);
    }

    @GetMapping("/materials")
    @NonNull
    public Page<PostResponse> getMaterials(
            @PathVariable @NonNull Long courseId,
            Pageable pageable,
            Principal principal
    ) {
        return postService.getCoursePostsByType(principal.getName(), courseId, PostType.MATERIAL, pageable);
    }

    @PostMapping("/materials")
    @PreAuthorize("hasRole('PROFESSOR')")
    @ResponseStatus(HttpStatus.CREATED)
    @NonNull
    public PostResponse createMaterial(
            @PathVariable @NonNull Long courseId,
            @RequestBody @Valid MaterialRequest request,
            Principal principal
    ) {
        return postService.createMaterial(principal.getName(), courseId, request);
    }

    @GetMapping("/assignments")
    @NonNull
    public Page<PostResponse> getAssignments(
            @PathVariable @NonNull Long courseId,
            Pageable pageable,
            Principal principal
    ) {
        return postService.getCoursePostsByType(principal.getName(), courseId, PostType.ASSIGNMENT, pageable);
    }

    @PostMapping("/assignments")
    @PreAuthorize("hasRole('PROFESSOR')")
    @ResponseStatus(HttpStatus.CREATED)
    @NonNull
    public PostResponse createAssignment(
            @PathVariable @NonNull Long courseId,
            @RequestBody @Valid AssignmentRequest request,
            Principal principal
    ) {
        return postService.createAssignment(principal.getName(), courseId, request);
    }

    @GetMapping("/quizzes")
    @NonNull
    public Page<PostResponse> getQuizzes(
            @PathVariable @NonNull Long courseId,
            Pageable pageable,
            Principal principal
    ) {
        return postService.getCoursePostsByType(principal.getName(), courseId, PostType.QUIZ, pageable);
    }

    @PostMapping("/quizzes")
    @PreAuthorize("hasRole('PROFESSOR')")
    @ResponseStatus(HttpStatus.CREATED)
    @NonNull
    public PostResponse createQuiz(
            @PathVariable @NonNull Long courseId,
            @RequestBody @Valid QuizRequest request,
            Principal principal
    ) {
        return postService.createQuiz(principal.getName(), courseId, request);
    }


    @DeleteMapping("/{postId}")
    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(
            @PathVariable @NonNull Long courseId,
            @PathVariable @NonNull Long postId,
            Principal principal
    ) {
        postService.deletePost(principal.getName(), courseId, postId);
    }

    @PutMapping("/{postId}/announcements")
    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @NonNull
    public PostResponse updateAnnouncement(
            @PathVariable @NonNull Long courseId,
            @PathVariable @NonNull Long postId,
            @RequestBody @Valid AnnouncementRequest request,
            Principal principal
    ) {
        return postService.updateAnnouncement(principal.getName(), courseId, postId, request);
    }

    @PutMapping("/{postId}/materials")
    @PreAuthorize("hasRole('PROFESSOR')")
    @NonNull
    public PostResponse updateMaterial(
            @PathVariable @NonNull Long courseId,
            @PathVariable @NonNull Long postId,
            @RequestBody @Valid MaterialRequest request,
            Principal principal
    ) {
        return postService.updateMaterial(principal.getName(), courseId, postId, request);
    }

    @PutMapping("/{postId}/assignments")
    @PreAuthorize("hasRole('PROFESSOR')")
    @NonNull
    public PostResponse updateAssignment(
            @PathVariable @NonNull Long courseId,
            @PathVariable @NonNull Long postId,
            @RequestBody @Valid AssignmentRequest request,
            Principal principal
    ) {
        return postService.updateAssignment(principal.getName(), courseId, postId, request);
    }

    @PutMapping("/{postId}/quizzes")
    @PreAuthorize("hasRole('PROFESSOR')")
    @NonNull
    public PostResponse updateQuiz(
            @PathVariable @NonNull Long courseId,
            @PathVariable @NonNull Long postId,
            @RequestBody @Valid QuizRequest request,
            Principal principal
    ) {
        return postService.updateQuiz(principal.getName(), courseId, postId, request);
    }


}