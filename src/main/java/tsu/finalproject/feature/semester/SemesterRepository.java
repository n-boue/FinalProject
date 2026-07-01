package tsu.finalproject.feature.semester;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {

    @Query("SELECT s FROM Semester s WHERE :today BETWEEN s.startDate AND s.endDate")
    List<Semester> findActiveSemesters(@Param("today") @NonNull LocalDate today);

    boolean existsByNameIgnoreCase(String name);
}
