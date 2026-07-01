package tsu.finalproject.feature.feed.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tsu.finalproject.feature.feed.entity.Post;
import tsu.finalproject.feature.feed.enums.PostType;
import tsu.finalproject.feature.feed.enums.PostVisibility;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByCourseIdOrderByCreatedAtDesc(Long courseId, Pageable pageable);

    Page<Post> findByCourseIdAndVisibilityNotOrderByCreatedAtDesc(Long courseId, PostVisibility visibility, Pageable pageable);

    Page<Post> findByCourseIdAndVisibilityOrderByCreatedAtDesc(Long courseId, PostVisibility visibility, Pageable pageable);

    Page<Post> findByCourseIdAndTypeOrderByCreatedAtDesc(Long courseId, PostType type, Pageable pageable);

    Page<Post> findByCourseIdAndVisibilityNotAndTypeOrderByCreatedAtDesc(Long courseId, PostVisibility visibility, PostType type, Pageable pageable);

    Page<Post> findByCourseIdAndVisibilityAndTypeOrderByCreatedAtDesc(Long courseId, PostVisibility visibility, PostType type, Pageable pageable);

    // base post has no due_date. since we are using single table inheritance, we can use a native query to get around this.
    @Query(value = """
                SELECT p.* FROM posts p
                INNER JOIN enrollments e ON p.course_id = e.course_id
                WHERE e.student_id = :studentId
                  AND e.status = 'ENROLLED'
                  AND p.post_type IN ('ASSIGNMENT', 'QUIZ')
                  AND p.visibility IN ('PUBLIC', 'ENROLLED_ONLY')
                  AND p.due_date > :now
                  AND NOT EXISTS (
                      SELECT 1 FROM submissions s
                      WHERE s.post_id = p.id AND s.student_id = :studentId
                  )
                ORDER BY p.due_date
            """, nativeQuery = true)
    List<Post> findPendingTasksForStudent(@Param("studentId") Long studentId, @Param("now") java.time.LocalDateTime now);
}
