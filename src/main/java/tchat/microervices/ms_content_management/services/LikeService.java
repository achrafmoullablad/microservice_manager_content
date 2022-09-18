package tchat.microervices.ms_content_management.services;

import org.springframework.http.ResponseEntity;
import tchat.microervices.ms_content_management.beans.Post;
import tchat.microervices.ms_content_management.dtos.like.LikeDTO;
import tchat.microervices.ms_content_management.dtos.like.LikeResponseDTO;

import java.util.List;

public interface LikeService {

    ResponseEntity<Object> saveLike(LikeDTO likeDTO);

    ResponseEntity<String> deleteLike(LikeDTO likeDTO);

    ResponseEntity<Object> findLikesByPost(String postId);

    List<LikeResponseDTO> getUsersLikedPost(String postId);

    void deleteLikesByPost(Post post);

    ResponseEntity<String> updateLike(LikeDTO likeDTO);
}
