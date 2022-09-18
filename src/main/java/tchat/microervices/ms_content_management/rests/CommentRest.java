package tchat.microervices.ms_content_management.rests;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tchat.microervices.ms_content_management.dtos.comment.CommentDTO;
import tchat.microervices.ms_content_management.dtos.comment.CommentUpdateDTO;
import tchat.microervices.ms_content_management.services.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/v1/content_management/comments")
@Slf4j
@AllArgsConstructor
@Validated
public class CommentRest {

    private CommentService commentService;

    @PostMapping("/save")
    public ResponseEntity<Object> saveComment(@RequestBody @Valid CommentDTO commentDTO) {
        log.info("CommentService: save comment " + commentDTO);
        return commentService.saveComment(commentDTO);
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable @NotEmpty @NotNull String commentId) {
        log.info("CommentService: delete comment " + commentId);
        return commentService.deleteComment(commentId);
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateComent(@RequestBody @Valid CommentUpdateDTO commentUpdateDTO) {
        log.info("CommentService: update comment " + commentUpdateDTO.getId());
        return commentService.updateComent(commentUpdateDTO);
    }

    @GetMapping("/{postId}/{page}/{size}")
    public ResponseEntity<Object> findCommentsByPost(@PathVariable @NotEmpty @NotNull String postId,
                                                     @PathVariable int page,
                                                     @PathVariable int size) {
        log.info("CommentService: retrieve comments of post " + postId);
        return commentService.findCommentsByPost(postId, page, size);
    }
}
