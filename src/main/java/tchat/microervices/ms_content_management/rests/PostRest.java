package tchat.microervices.ms_content_management.rests;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tchat.microervices.ms_content_management.dtos.post.PostResponseDTO;
import tchat.microervices.ms_content_management.dtos.post.SharedPostDTO;
import tchat.microervices.ms_content_management.services.PostService;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/content_management/posts")
@AllArgsConstructor
@Slf4j
@Validated
public class PostRest {

    private PostService postService;


    @PostMapping( value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> insertPost(@RequestParam Optional<MultipartFile> photoOrVideo,
                                         @RequestParam Optional<String> content,
                                         @RequestParam @NotNull @NotEmpty String userId)  {

        log.info("PostRest: Save post of the user with id " + userId);

        return postService.insertPost(photoOrVideo, content, userId);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePost(@PathVariable @NotNull @NotEmpty String id) {
        log.info("PostRest: Delete post with id " + id);
        return postService.deletePost(id);
    }

    @PutMapping( value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> updatePost(@RequestParam Optional<MultipartFile> photoOrVideo,
                                             @RequestParam Optional<String> content,
                                             @RequestParam @NotNull @NotEmpty String postId,
                                             @RequestParam boolean deletePhotoOrVideo) {
        log.info("PostRest: Update post " + postId);
        return postService.updatePost(photoOrVideo, content, postId, deletePhotoOrVideo);

    }

    @GetMapping("/{page}/{size}")
    public List<PostResponseDTO> findPostsByUserId(@PathVariable int page, @PathVariable int size) {
        log.info("PostRest: Retrieve posts with page " + page + " and size " + size);
        return postService.findPostsByUserId(page, size);
    }

    @PostMapping("/share")
    public ResponseEntity<Object> createNewSharedPost(@RequestBody @Valid SharedPostDTO sharedPost){
        log.info("PostRest: Create new shared post for post " + sharedPost.getPostId());
        return postService.createNewSharedPost(sharedPost);
    }
}
