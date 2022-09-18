package tchat.microervices.ms_content_management.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import tchat.microervices.ms_content_management.beans.Comment;
import tchat.microervices.ms_content_management.beans.Post;

import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, String> {

    Page<Comment> findCommentsByPost(Post post, Pageable pageable);

    void deleteCommentsByPost(Post post);

    List<Comment> findCommentsByUserId(Long userId);
}
