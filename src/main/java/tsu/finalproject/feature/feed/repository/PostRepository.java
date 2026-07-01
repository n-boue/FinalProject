package tsu.finalproject.feature.feed.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tsu.finalproject.feature.feed.entity.Post;
import tsu.finalproject.feature.feed.enums.PostType;
import tsu.finalproject.feature.feed.enums.PostVisibility;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByCourseIdOrderByCreatedAtDesc(Long courseId, Pageable pageable);

    Page<Post> findByCourseIdAndVisibilityNotOrderByCreatedAtDesc(Long courseId, PostVisibility visibility, Pageable pageable);

    Page<Post> findByCourseIdAndVisibilityOrderByCreatedAtDesc(Long courseId, PostVisibility visibility, Pageable pageable);

    Page<Post> findByCourseIdAndTypeOrderByCreatedAtDesc(Long courseId, PostType type, Pageable pageable);

    Page<Post> findByCourseIdAndVisibilityNotAndTypeOrderByCreatedAtDesc(Long courseId, PostVisibility visibility, PostType type, Pageable pageable);

    Page<Post> findByCourseIdAndVisibilityAndTypeOrderByCreatedAtDesc(Long courseId, PostVisibility visibility, PostType type, Pageable pageable);
}
