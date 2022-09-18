package tchat.microervices.ms_content_management.services;

import org.springframework.http.ResponseEntity;
import tchat.microervices.ms_content_management.beans.Comment;
import tchat.microervices.ms_content_management.beans.Post;
import tchat.microervices.ms_content_management.dtos.comment.CommentDTO;
import tchat.microervices.ms_content_management.dtos.comment.CommentUpdateDTO;

import java.util.List;
import java.util.Optional;

public interface CommentService {

    ResponseEntity<Object> saveComment(CommentDTO commentDTO);

    ResponseEntity<String> deleteComment(String commentId);

    ResponseEntity<Object> updateComent(CommentUpdateDTO commentUpdateDTO);

    ResponseEntity<Object> findCommentsByPost(String postId, int page, int size);

    void deleteCommentsByPost(Post post);

    Optional<Comment> findCommentById(String id);
    List<Comment> findCommentsByUserId(Long userId);
}
