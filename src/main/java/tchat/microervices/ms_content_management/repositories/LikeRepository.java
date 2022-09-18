package tchat.microervices.ms_content_management.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import tchat.microervices.ms_content_management.beans.Like;
import tchat.microervices.ms_content_management.beans.Post;

import java.util.List;

public interface LikeRepository extends MongoRepository<Like, String> {
    List<Like> findLikesByPost(Post post);
    void deleteByPostAndUserId(Post post, Long userId);
    void deleteLikesByPost(Post post);

    Like findByUserIdAndPost(Long userId, Post post);
}
