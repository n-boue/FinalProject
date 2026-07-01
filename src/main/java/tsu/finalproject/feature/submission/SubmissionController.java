package tsu.finalproject.feature.submission;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tsu.finalproject.feature.storage.dto.AttachmentResponse;
import tsu.finalproject.feature.submission.dto.AssignmentSubmissionRequest;
import tsu.finalproject.feature.submission.dto.GradeSubmissionRequest;
import tsu.finalproject.feature.submission.dto.QuizSubmissionRequest;
import tsu.finalproject.feature.submission.dto.SubmissionResponse;

import java.security.Principal;

@RestController
@RequestMapping("${api.prefix}/courses/{courseId}/posts/{postId}/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping("/assignment")
    @PreAuthorize("hasRole('STUDENT')")
    @ResponseStatus(HttpStatus.CREATED)
    @NonNull
    public SubmissionResponse submitAssignment(
            @PathVariable @NonNull Long courseId,
            @PathVariable @NonNull Long postId,
            @RequestBody @Valid AssignmentSubmissionRequest request,
            Principal principal
    ) {
        return submissionService.submitAssignment(principal.getName(), courseId, postId, request);
    }

    @PostMapping("/quiz")
    @PreAuthorize("hasRole('STUDENT')")
    @ResponseStatus(HttpStatus.CREATED)
    @NonNull
    public SubmissionResponse submitQuiz(
            @PathVariable @NonNull Long courseId,
            @PathVariable @NonNull Long postId,
            @RequestBody @Valid QuizSubmissionRequest request,
            Principal principal
    ) {
        return submissionService.submitQuiz(principal.getName(), courseId, postId, request);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    @NonNull
    public SubmissionResponse getMySubmission(
            @PathVariable @NonNull Long courseId,
            @PathVariable @NonNull Long postId,
            Principal principal
    ) {
        return submissionService.getMySubmission(principal.getName(), courseId, postId);
    }

    @PutMapping("/{submissionId}/grade")
    @PreAuthorize("hasRole('PROF') or hasRole('ADMIN')")
    @NonNull
    public SubmissionResponse gradeSubmission(
            @PathVariable @NonNull Long courseId,
            @PathVariable @NonNull Long postId,
            @PathVariable @NonNull Long submissionId,
            @RequestBody @Valid GradeSubmissionRequest request,
            Principal principal
    ) {
        return submissionService.gradeSubmission(principal.getName(), courseId, submissionId, request);
    }

    @GetMapping
    @PreAuthorize("hasRole('PROF') or hasRole('ADMIN')")
    @NonNull
    public Page<SubmissionResponse> getSubmissionsForPost(
            @PathVariable @NonNull Long courseId,
            @PathVariable @NonNull Long postId,
            @RequestParam(required = false) Boolean needsGrading,
            Pageable pageable,
            Principal principal
    ) {
        return submissionService.getSubmissionsForPost(
                principal.getName(), courseId, postId, needsGrading, pageable);
    }

    @PostMapping(value = "/{submissionId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @NonNull
    public AttachmentResponse addAttachmentToSubmission(
            @PathVariable @NonNull Long courseId,
            @PathVariable @NonNull Long postId,
            @PathVariable @NonNull Long submissionId,
            @RequestParam("file") @NonNull MultipartFile file,
            Principal principal
    ) {
        return submissionService.addAttachmentToSubmission(principal.getName(), courseId, postId, submissionId, file);
    }

    @DeleteMapping("/{submissionId}/attachments/{attachmentId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAttachmentFromSubmission(
            @PathVariable @NonNull Long courseId,
            @PathVariable @NonNull Long postId,
            @PathVariable @NonNull Long submissionId,
            @PathVariable @NonNull Long attachmentId,
            Principal principal
    ) {
        submissionService.removeAttachmentFromSubmission(principal.getName(), courseId, postId, submissionId, attachmentId);
    }
}