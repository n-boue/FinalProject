package tsu.finalproject.feature.directory;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import tsu.finalproject.feature.course.dto.CourseResponse;
import tsu.finalproject.feature.directory.dto.ProfessorDirectoryResponse;

@RestController
@RequestMapping("${api.prefix}/directory")
@RequiredArgsConstructor
public class DirectoryController {

    private final DirectoryService directoryService;

    @GetMapping("/professors")
    @NonNull
    public Page<ProfessorDirectoryResponse> searchProfessors(
            @RequestParam(required = false) String search,
            Pageable pageable
    ) {
        return directoryService.searchProfessors(search, pageable);
    }

    @GetMapping("/professors/{id}")
    @NonNull
    public ProfessorDirectoryResponse getProfessorDetails(@PathVariable @NonNull Long id) {
        return directoryService.getProfessorDetails(id);
    }

    @GetMapping("/courses")
    @NonNull
    public Page<CourseResponse> searchCourses(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long semesterId,
            Pageable pageable
    ) {
        return directoryService.searchCourses(search, semesterId, pageable);
    }

    @GetMapping("/professors/{id}/courses")
    @NonNull
    public Page<CourseResponse> getProfessorCourses(
            @PathVariable @NonNull Long id,
            Pageable pageable
    ) {
        return directoryService.getProfessorCourses(id, pageable);
    }
}