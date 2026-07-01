package tsu.finalproject.feature.dashboard;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tsu.finalproject.feature.dashboard.dto.AllowedDomainsRequest;
import tsu.finalproject.feature.dashboard.dto.SystemStatsResponse;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    @NonNull
    public SystemStatsResponse getSystemStats() {
        return adminDashboardService.getSystemStats();
    }

    @GetMapping("/config/domains")
    @PreAuthorize("hasRole('ADMIN')")
    @NonNull
    public List<String> getAllowedDomains() {
        return adminDashboardService.getAllowedStudentDomains();
    }

    @PutMapping("/config/domains")
    @PreAuthorize("hasRole('ADMIN')")
    @NonNull
    public List<String> updateAllowedDomains(@RequestBody @Valid AllowedDomainsRequest request) {
        return adminDashboardService.updateAllowedStudentDomains(request);
    }
}