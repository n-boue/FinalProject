package tsu.finalproject.feature.course;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import tsu.finalproject.common.manager.DomainLookupService;
import tsu.finalproject.feature.course.dto.*;
import tsu.finalproject.feature.course.entity.Course;
import tsu.finalproject.feature.course.entity.CourseSection;
import tsu.finalproject.feature.course.entity.CourseSession;
import tsu.finalproject.feature.course.repository.CourseRepository;
import tsu.finalproject.feature.course.repository.CourseSectionRepository;
import tsu.finalproject.feature.course.repository.CourseSessionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseSectionRepository courseSectionRepository;
    private final CourseSessionRepository courseSessionRepository;
    private final DomainLookupService domainLookupService;
    private final CourseMapper courseMapper;
    private final CacheManager cacheManager;

    @Transactional
    @NonNull
    public CourseResponse createCourse(@NonNull CourseRequest request) {
        Course course = Course.builder()
                                .title(request.title())
                                .faculty(request.faculty())
                                .description(request.description())
                                .credits(request.credits())
                                .semester(domainLookupService.getSemester(request.semesterId()))
                                .leadProfessor(domainLookupService.getProfessor(request.professorId()))
                                .deactivated(false)
                                .build();

        return courseMapper.toResponse(courseRepository.save(course));
    }

    @CacheEvict(value = {"courses", "course-details"}, key = "#id")
    @Transactional
    @NonNull
    public CourseResponse deactivateCourse(@NonNull Long id) {
        Course course = domainLookupService.getCourse(id);
        course.setDeactivated(true);
        return courseMapper.toResponse(courseRepository.save(course));
    }

    @Cacheable(value = "courses", key = "#id")
    @Transactional(readOnly = true)
    @NonNull
    public CourseResponse getCourseById(@NonNull Long id) {
        return courseMapper.toResponse(domainLookupService.getCourse(id));
    }

    @Transactional(readOnly = true)
    @NonNull
    public Page<CourseResponse> getCoursesBySemester(@NonNull Long semesterId, @NonNull Pageable pageable) {
        return courseRepository.findBySemesterIdAndDeactivatedFalse(semesterId, pageable)
                       .map(courseMapper::toResponse);
    }

    @Transactional(readOnly = true)
    @NonNull
    public Page<CourseResponse> getCoursesByProfessor(@NonNull Long professorId, @NonNull Long semesterId, @NonNull Pageable pageable) {
        return courseRepository.findByLeadProfessorIdAndSemesterId(professorId, semesterId, pageable)
                       .map(courseMapper::toResponse);
    }

    @Cacheable(value = "professor-sessions", key = "#professorId")
    @Transactional(readOnly = true)
    @NonNull
    public List<CourseSessionResponse> getTeachingSessionsByProfessorId(@NonNull Long professorId) {
        return courseSessionRepository.findByProfessorId(professorId).stream()
                       .map(courseMapper::toSessionResponse)
                       .toList();
    }

    @CacheEvict(value = {"courses", "course-details"}, key = "#id")
    @Transactional
    @NonNull
    public CourseResponse updateCourse(@NonNull Long id, @NonNull CourseRequest request) {
        Course course = domainLookupService.getCourse(id);

        course.setTitle(request.title());
        course.setFaculty(request.faculty());
        course.setDescription(request.description());
        course.setCredits(request.credits());
        course.setSemester(domainLookupService.getSemester(request.semesterId()));
        course.setLeadProfessor(domainLookupService.getProfessor(request.professorId()));

        return courseMapper.toResponse(courseRepository.save(course));
    }

    @Transactional
    @NonNull
    public CourseSectionResponse updateCourseSection(@NonNull Long sectionId, @NonNull CourseSectionRequest request) {
        CourseSection section = courseSectionRepository.findById(sectionId)
                                        .orElseThrow(() -> new EntityNotFoundException("Course Section not found with ID: " + sectionId));

        section.setType(request.type());
        CourseSection updatedSection = courseSectionRepository.save(section);

        Cache courseCache = cacheManager.getCache("course-details");
        if (courseCache != null) courseCache.evict(section.getCourse().getId());

        return courseMapper.toSectionResponse(updatedSection);
    }

    @Transactional
    public void deleteCourseSection(@NonNull Long sectionId) {
        CourseSection section = courseSectionRepository.findById(sectionId)
                                        .orElseThrow(() -> new EntityNotFoundException("Course Section not found with ID: " + sectionId));

        Assert.isTrue(section.getSessions().isEmpty(),
                "Cannot delete a section that contains active sessions. Delete sessions first.");

        Long parentCourseId = section.getCourse().getId();
        courseSectionRepository.delete(section);

        Cache courseCache = cacheManager.getCache("course-details");
        if (courseCache != null) courseCache.evict(parentCourseId);
    }

    @CacheEvict(value = {"courses", "course-details"}, key = "#courseId")
    @Transactional
    @NonNull
    public CourseSectionResponse addSectionToCourse(@NonNull Long courseId, @NonNull CourseSectionRequest request) {
        CourseSection section = CourseSection.builder()
                                        .course(domainLookupService.getCourse(courseId))
                                        .type(request.type())
                                        .build();

        return courseMapper.toSectionResponse(courseSectionRepository.save(section));
    }

    @Transactional
    @NonNull
    public CourseSessionResponse addSessionToSection(@NonNull Long sectionId, @NonNull CourseSessionRequest request) {
        validateTimeRange(request);

        CourseSection section = courseSectionRepository.findById(sectionId)
                                        .orElseThrow(() -> new EntityNotFoundException("Course Section not found with ID: " + sectionId));

        Long parentCourseId = section.getCourse().getId();

        CourseSession session = CourseSession.builder()
                                        .section(section)
                                        .name(request.name())
                                        .maxCapacity(request.maxCapacity())
                                        .professor(domainLookupService.getProfessor(request.professorId()))
                                        .dayOfWeek(request.dayOfWeek())
                                        .startTime(request.startTime())
                                        .endTime(request.endTime())
                                        .room(request.room())
                                        .build();

        CourseSession savedSession = courseSessionRepository.save(session);
        evictProfessorSessionsCache(request.professorId());
        Cache courseCache = cacheManager.getCache("course-details");
        if (courseCache != null) courseCache.evict(parentCourseId);

        return courseMapper.toSessionResponse(savedSession);
    }

    @Transactional
    @NonNull
    public CourseSessionResponse updateCourseSession(@NonNull Long sessionId, @NonNull CourseSessionRequest request) {
        validateTimeRange(request);

        CourseSession session = courseSessionRepository.findById(sessionId)
                                        .orElseThrow(() -> new EntityNotFoundException("Course Session not found with ID: " + sessionId));

        Long originalProfessorId = session.getProfessor().getId();
        Long parentCourseId = session.getSection().getCourse().getId();

        session.setName(request.name());
        session.setMaxCapacity(request.maxCapacity());
        session.setProfessor(domainLookupService.getProfessor(request.professorId()));
        session.setDayOfWeek(request.dayOfWeek());
        session.setStartTime(request.startTime());
        session.setEndTime(request.endTime());
        session.setRoom(request.room());

        CourseSession updatedSession = courseSessionRepository.save(session);

        Cache courseCache = cacheManager.getCache("course-details");
        if (courseCache != null) courseCache.evict(parentCourseId);

        evictProfessorSessionsCache(originalProfessorId);
        if (!originalProfessorId.equals(request.professorId())) {
            evictProfessorSessionsCache(request.professorId());
        }

        return courseMapper.toSessionResponse(updatedSession);
    }

    @Transactional
    public void deleteCourseSession(@NonNull Long sessionId) {
        CourseSession session = courseSessionRepository.findById(sessionId)
                                        .orElseThrow(() -> new EntityNotFoundException("Course Session not found with ID: " + sessionId));

        Long professorId = session.getProfessor().getId();
        Long parentCourseId = session.getSection().getCourse().getId();

        courseSessionRepository.delete(session);

        Cache courseCache = cacheManager.getCache("course-details");
        if (courseCache != null) courseCache.evict(parentCourseId);
        evictProfessorSessionsCache(professorId);
    }


    @Cacheable(value = "course-details", key = "#id")
    @Transactional(readOnly = true)
    @NonNull
    public CourseDetailsResponse getCourseDetailsById(@NonNull Long id) {
        Course course = courseRepository.findCourseDetailsById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + id));

        return courseMapper.toDetailsResponse(course);
    }


    private void validateTimeRange(CourseSessionRequest request) {
        Assert.isTrue(request.startTime().isBefore(request.endTime()),
                "Start time must be before end time.");
    }

    private void evictProfessorSessionsCache(Long professorId) {
        Cache profCache = cacheManager.getCache("professor-sessions");
        if (profCache != null) {
            profCache.evict(professorId);
        }
    }
}