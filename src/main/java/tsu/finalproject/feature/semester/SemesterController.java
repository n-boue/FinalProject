package tsu.finalproject.feature.semester;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tsu.finalproject.feature.semester.dto.SemesterRequest;
import tsu.finalproject.feature.semester.dto.SemesterResponse;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/semesters")
@RequiredArgsConstructor
public class SemesterController {

    private final SemesterService semesterService;
    private final Clock clock;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @NonNull
    public SemesterResponse createSemester(@RequestBody @Valid SemesterRequest request) {
        return semesterService.createSemester(request);
    }

    @GetMapping
    @NonNull
    public Page<SemesterResponse> getAllSemesters(Pageable pageable) {
        return semesterService.getAllSemesters(pageable);
    }

    @GetMapping("/{id}")
    @NonNull
    public SemesterResponse getSemesterById(@PathVariable @NonNull Long id) {
        return semesterService.getSemesterById(id);
    }

    @GetMapping("/active")
    @NonNull
    public List<SemesterResponse> getActiveSemesters() {
        return semesterService.getActiveSemesters(LocalDate.now(clock));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @NonNull
    public SemesterResponse updateSemester(
            @PathVariable @NonNull Long id,
            @RequestBody @Valid SemesterRequest request
    ) {
        return semesterService.updateSemester(id, request);
    }
}
