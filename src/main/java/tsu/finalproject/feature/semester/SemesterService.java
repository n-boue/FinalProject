package tsu.finalproject.feature.semester;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import tsu.finalproject.feature.semester.dto.SemesterRequest;
import tsu.finalproject.feature.semester.dto.SemesterResponse;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SemesterService {

    private final SemesterRepository semesterRepository;
    private final SemesterMapper semesterMapper;
    private final Clock clock;

    @CacheEvict(value = {"semesters", "active-semesters"}, allEntries = true)
    @Transactional
    @NonNull
    public SemesterResponse createSemester(@NonNull SemesterRequest request) {
        Assert.isTrue(!semesterRepository.existsByNameIgnoreCase(request.name()),
                "A semester with this name already exists.");
        Assert.isTrue(!request.startDate().isAfter(request.endDate()),
                "Start date must be before the end date.");
        Assert.isTrue(!request.enrollmentStartDate().isAfter(request.enrollmentEndDate()),
                "Enrollment start date must be before the enrollment end date.");

        LocalDate today = LocalDate.now(clock);
        boolean isCurrent = !today.isBefore(request.startDate()) && !today.isAfter(request.endDate());

        Semester semester = Semester.builder()
                                    .name(request.name())
                                    .startDate(request.startDate())
                                    .endDate(request.endDate())
                                    .enrollmentStartDate(request.enrollmentStartDate())
                                    .enrollmentEndDate(request.enrollmentEndDate())
                                    .isCurrent(isCurrent)
                                    .build();

        return semesterMapper.toResponse(semesterRepository.save(semester));
    }

    @Cacheable(value = "semesters", key = "#id")
    @Transactional(readOnly = true)
    @NonNull
    public SemesterResponse getSemesterById(@NonNull Long id) {
        Semester semester = semesterRepository.findById(id)
                                    .orElseThrow(() -> new EntityNotFoundException("Semester not found with ID: " + id));

        return semesterMapper.toResponse(semester);
    }

    @Transactional(readOnly = true)
    @NonNull
    public Page<SemesterResponse> getAllSemesters(Pageable pageable) {
        return semesterRepository.findAll(pageable)
                       .map(semesterMapper::toResponse);
    }

    @Cacheable(value = "active-semesters", key = "#today.toString()")
    @Transactional(readOnly = true)
    @NonNull
    public List<SemesterResponse> getActiveSemesters(LocalDate today) {
        return semesterRepository.findActiveSemesters(today).stream()
                       .map(semesterMapper::toResponse)
                       .toList();
    }

    @CacheEvict(value = {"semesters", "active-semesters"}, allEntries = true)
    @Transactional
    @NonNull
    public SemesterResponse updateSemester(@NonNull Long id, @NonNull SemesterRequest request) {
        Semester semester = semesterRepository.findById(id)
                                    .orElseThrow(() -> new EntityNotFoundException("Semester not found with ID: " + id));

        if (!semester.getName().equalsIgnoreCase(request.name())) {
            Assert.isTrue(!semesterRepository.existsByNameIgnoreCase(request.name()),
                    "A semester with this name already exists.");
        }

        Assert.isTrue(!request.startDate().isAfter(request.endDate()),
                "Start date must be before the end date.");
        Assert.isTrue(!request.enrollmentStartDate().isAfter(request.enrollmentEndDate()),
                "Enrollment start date must be before the enrollment end date.");

        semester.setName(request.name());
        semester.setStartDate(request.startDate());
        semester.setEndDate(request.endDate());
        semester.setEnrollmentStartDate(request.enrollmentStartDate());
        semester.setEnrollmentEndDate(request.enrollmentEndDate());

        LocalDate today = LocalDate.now(clock);
        semester.setCurrent(!today.isBefore(request.startDate()) && !today.isAfter(request.endDate()));

        return semesterMapper.toResponse(semesterRepository.save(semester));
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    @CacheEvict(value = {"semesters", "active-semesters"}, allEntries = true)
    public void runDailySemesterSync() {
        semesterRepository.syncAllSemesterStatuses(LocalDate.now(clock));
    }
}