package tchat.microervices.ms_content_management.rests;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tchat.microervices.ms_content_management.dtos.like.LikeDTO;
import tchat.microervices.ms_content_management.dtos.like.LikeResponseDTO;
import tchat.microervices.ms_content_management.services.LikeService;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/api/v1/content_management/likes")
@AllArgsConstructor
@Slf4j
@Validated
public class LikeRest {

    private LikeService likeService;

    @PostMapping("/save")
    public ResponseEntity<Object> saveLike(@RequestBody @Valid LikeDTO likeDTO) {
        log.info("LikeRest: Save like " + likeDTO);
        return likeService.saveLike(likeDTO);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteLike(@RequestBody @Valid LikeDTO likeDTO) {
        log.info("LikeRest: Delete like to post " + likeDTO.getPostId());
        return likeService.deleteLike(likeDTO);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Object> findLikesByPost(@PathVariable @NotNull @NotEmpty String postId) {
        log.info("LikeRest: Retrieve likes of post with id " + postId);
        return likeService.findLikesByPost(postId);
    }

    @GetMapping("/users/{postId}")
    public List<LikeResponseDTO> getUsersLikedPost(@PathVariable @NotNull @NotEmpty String postId) {
        log.info("LikeRest: Retrieve users who like the post " + postId);
        return likeService.getUsersLikedPost(postId);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateLike(@RequestBody @Valid LikeDTO likeDTO) {
        log.info("LikeRest: update like of the post " + likeDTO.getPostId());
        return likeService.updateLike(likeDTO);
    }
}
