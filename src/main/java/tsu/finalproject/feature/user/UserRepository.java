package tsu.finalproject.feature.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tsu.finalproject.feature.user.entity.User;
import tsu.finalproject.feature.user.enums.Role;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUniversityId(String universityId);

    @Query("SELECT u FROM User u WHERE " +
                   "(:role IS NULL OR u.role = :role) AND " +
                   "(:search IS NULL OR " +
                   "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                   "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                   "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                   "LOWER(u.universityId) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> findWithFilters(@Param("role") Role role, @Param("search") String search, Pageable pageable);
}
