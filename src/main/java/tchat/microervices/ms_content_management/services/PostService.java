package tchat.microervices.ms_content_management.services;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import tchat.microervices.ms_content_management.beans.Post;
import tchat.microervices.ms_content_management.dtos.post.PostResponseDTO;
import tchat.microervices.ms_content_management.dtos.post.SharedPostDTO;

import java.util.List;
import java.util.Optional;

public interface PostService {
    ResponseEntity<Object> insertPost(Optional<MultipartFile> photoOrVideo, Optional<String> content, String userId);

    ResponseEntity<String> deletePost(String id);

    ResponseEntity<Object> updatePost(Optional<MultipartFile> photoOrVideo, Optional<String> content,
                                      String postId, boolean deletePhotoOrVideo);


    List<PostResponseDTO> findPostsByUserId(int page, int size);

    Optional<Post> findById(String postId);

    ResponseEntity<Object> createNewSharedPost(SharedPostDTO sharedPost);

    void updateNbrLikes(Post post, String type);

    void updateNbrComments(Post post, String type);

    List<Post> findPostsByUserId(Long userId);
}
