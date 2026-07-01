package tsu.finalproject.feature.dashboard.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "system_configuration")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemConfiguration {

    // singleton
    @Id
    @Builder.Default
    private Long id = 1L;

    @Column(name = "allowed_student_domains", columnDefinition = "TEXT", nullable = false)
    private String allowedStudentDomainsCsv;

    public List<String> getAllowedStudentDomains() {
        if (allowedStudentDomainsCsv == null || allowedStudentDomainsCsv.isBlank()) {
            return List.of();
        }
        return Arrays.asList(allowedStudentDomainsCsv.split(","));
    }

    public void setAllowedStudentDomains(List<String> domains) {
        this.allowedStudentDomainsCsv = domains == null ? "" : String.join(",", domains);
    }
}