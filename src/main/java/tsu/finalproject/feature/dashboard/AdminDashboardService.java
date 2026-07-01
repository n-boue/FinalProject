package tsu.finalproject.feature.dashboard;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tsu.finalproject.feature.course.repository.CourseRepository;
import tsu.finalproject.feature.dashboard.dto.AllowedDomainsRequest;
import tsu.finalproject.feature.dashboard.dto.SystemStatsResponse;
import tsu.finalproject.feature.dashboard.entity.SystemConfiguration;
import tsu.finalproject.feature.dashboard.repository.SystemConfigurationRepository;
import tsu.finalproject.feature.semester.SemesterRepository;
import tsu.finalproject.feature.user.UserRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final SystemConfigurationRepository configRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final SemesterRepository semesterRepository;
    private final Clock clock;

    @Cacheable(value = "system-config", key = "'allowed-domains'")
    @Transactional(readOnly = true)
    @NonNull
    public List<String> getAllowedStudentDomains() {
        return getConfigurationSingleton().getAllowedStudentDomains();
    }

    @CacheEvict(value = "system-config", key = "'allowed-domains'")
    @Transactional
    @NonNull
    public List<String> updateAllowedStudentDomains(@NonNull AllowedDomainsRequest request) {
        SystemConfiguration config = getConfigurationSingleton();
        config.setAllowedStudentDomains(request.domains());
        return configRepository.save(config).getAllowedStudentDomains();
    }

    @Transactional(readOnly = true)
    @NonNull
    public SystemStatsResponse getSystemStats() {
        return new SystemStatsResponse(
                userRepository.count(),
                courseRepository.count(),
                (long) semesterRepository.findActiveSemesters(LocalDate.now(clock)).size()
        );
    }

    private SystemConfiguration getConfigurationSingleton() {
        return configRepository.findById(1L).orElseGet(() -> {
            SystemConfiguration defaultConfig = SystemConfiguration.builder()
                                                        .allowedStudentDomainsCsv("@tsu.ge,@ens.tsu.edu.ge") // Default fallback
                                                        .build();
            return configRepository.save(defaultConfig);
        });
    }
}