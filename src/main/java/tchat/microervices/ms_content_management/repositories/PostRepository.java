package tchat.microervices.ms_content_management.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import tchat.microervices.ms_content_management.beans.Post;

import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {
    Page<Post> findPostsByUserIdIn(List<Long> userIds, Pageable pageable);

    List<Post> findPostsByUserId(Long userId);
}
