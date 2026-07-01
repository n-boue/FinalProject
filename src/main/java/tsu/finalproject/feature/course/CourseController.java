package tsu.finalproject.feature.course;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tsu.finalproject.common.manager.DomainLookupService;
import tsu.finalproject.feature.course.dto.*;
import tsu.finalproject.feature.user.entity.User;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final DomainLookupService domainLookupService;

    @PostMapping("/courses")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @NonNull
    public CourseResponse createCourse(@RequestBody @Valid CourseRequest request) {
        return courseService.createCourse(request);
    }

    @GetMapping("/courses/{id}")
    @NonNull
    public CourseResponse getCourseById(@PathVariable @NonNull Long id) {
        return courseService.getCourseById(id);
    }

    @GetMapping("/semesters/{semesterId}/courses")
    @NonNull
    public Page<CourseResponse> getCoursesBySemester(
            @PathVariable @NonNull Long semesterId,
            Pageable pageable
    ) {
        return courseService.getCoursesBySemester(semesterId, pageable);
    }

    @DeleteMapping("/courses/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @NonNull
    public CourseResponse deactivateCourse(@PathVariable @NonNull Long id) {
        return courseService.deactivateCourse(id);
    }


    @PostMapping("/courses/{courseId}/sections")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @NonNull
    public CourseSectionResponse addSection(
            @PathVariable @NonNull Long courseId,
            @RequestBody @Valid CourseSectionRequest request
    ) {
        return courseService.addSectionToCourse(courseId, request);
    }

    @PostMapping("/sections/{sectionId}/sessions")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @NonNull
    public CourseSessionResponse addSession(
            @PathVariable @NonNull Long sectionId,
            @RequestBody @Valid CourseSessionRequest request
    ) {
        return courseService.addSessionToSection(sectionId, request);
    }

    @GetMapping("/courses/{id}/details")
    @NonNull
    public CourseDetailsResponse getCourseDetailsById(@PathVariable @NonNull Long id) {
        return courseService.getCourseDetailsById(id);
    }

    @PutMapping("/sessions/{sessionId}")
    @PreAuthorize("hasRole('ADMIN')")
    @NonNull
    public CourseSessionResponse updateSession(
            @PathVariable @NonNull Long sessionId,
            @RequestBody @Valid CourseSessionRequest request
    ) {
        return courseService.updateCourseSession(sessionId, request);
    }

    @DeleteMapping("/sessions/{sessionId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSession(@PathVariable @NonNull Long sessionId) {
        courseService.deleteCourseSession(sessionId);
    }

    @GetMapping("/professors/{professorId}/semesters/{semesterId}/courses")
    @NonNull
    public Page<CourseResponse> getCoursesByProfessor(
            @PathVariable @NonNull Long professorId,
            @PathVariable @NonNull Long semesterId,
            Pageable pageable
    ) {
        return courseService.getCoursesByProfessor(professorId, semesterId, pageable);
    }

    @GetMapping("/users/me/sessions")
    @PreAuthorize("hasRole('PROF')")
    @NonNull
    public List<CourseSessionResponse> getMyTeachingSessions(Principal principal) {
        User user = domainLookupService.getUser(principal.getName());
        return courseService.getTeachingSessionsByProfessorId(user.getId());
    }

    @PutMapping("/courses/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @NonNull
    public CourseResponse updateCourse(
            @PathVariable @NonNull Long id,
            @RequestBody @Valid CourseRequest request
    ) {
        return courseService.updateCourse(id, request);
    }

    @PutMapping("/sections/{sectionId}")
    @PreAuthorize("hasRole('ADMIN')")
    @NonNull
    public CourseSectionResponse updateSection(
            @PathVariable @NonNull Long sectionId,
            @RequestBody @Valid CourseSectionRequest request
    ) {
        return courseService.updateCourseSection(sectionId, request);
    }

    @DeleteMapping("/sections/{sectionId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSection(@PathVariable @NonNull Long sectionId) {
        courseService.deleteCourseSection(sectionId);
    }
}