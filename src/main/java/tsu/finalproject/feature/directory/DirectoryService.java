package tsu.finalproject.feature.directory;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tsu.finalproject.common.manager.DomainLookupService;
import tsu.finalproject.feature.course.CourseMapper;
import tsu.finalproject.feature.course.dto.CourseResponse;
import tsu.finalproject.feature.course.repository.CourseRepository;
import tsu.finalproject.feature.directory.dto.ProfessorDirectoryResponse;
import tsu.finalproject.feature.user.UserRepository;
import tsu.finalproject.feature.user.entity.Professor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DirectoryService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final DomainLookupService domainLookupService;
    private final DirectoryMapper directoryMapper;

    @NonNull
    public Page<ProfessorDirectoryResponse> searchProfessors(String search, @NonNull Pageable pageable) {
        String notNullSearch = search == null ? "" : search;
        return userRepository.searchActiveProfessors(notNullSearch, pageable)
                       .map(user -> directoryMapper.toProfessorResponse((Professor) user));
    }

    @NonNull
    public ProfessorDirectoryResponse getProfessorDetails(@NonNull Long professorId) {
        Professor professor = domainLookupService.getProfessor(professorId);

        if (professor.isDeactivated()) {
            throw new IllegalArgumentException("This professor profile is deactivated.");
        }

        return directoryMapper.toProfessorResponse(professor);
    }

    @NonNull
    public Page<CourseResponse> searchCourses(
            String search, Long semesterId, @NonNull Pageable pageable) {
        String notNullSearch = search == null ? "" : search;
        return courseRepository.searchActiveCourses(notNullSearch, semesterId, pageable)
                       .map(courseMapper::toResponse);
    }

    @NonNull
    public Page<CourseResponse> getProfessorCourses(@NonNull Long professorId, @NonNull Pageable pageable) {
        Professor professor = domainLookupService.getProfessor(professorId);

        if (professor.isDeactivated()) {
            throw new IllegalArgumentException("This professor profile is deactivated.");
        }

        return courseRepository.findCoursesByProfessorInvolvement(professorId, pageable)
                       .map(courseMapper::toResponse);
    }

}