package tchat.microervices.ms_content_management.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import tchat.microervices.ms_content_management.beans.Comment;
import tchat.microervices.ms_content_management.beans.Post;
import tchat.microervices.ms_content_management.dtos.comment.CommentDTO;
import tchat.microervices.ms_content_management.dtos.comment.CommentResponseDTO;
import tchat.microervices.ms_content_management.dtos.comment.CommentUpdateDTO;
import tchat.microervices.ms_content_management.mappers.Mapper;
import tchat.microervices.ms_content_management.repositories.CommentRepository;
import tchat.microervices.ms_content_management.services.CommentService;
import tchat.microervices.ms_content_management.services.PostService;
import tchat.microervices.ms_content_management.services.ReportService;
import tchat.microervices.ms_content_management.vos.User;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private ReportService reportService;

    private final Mapper mapper;

    private final RestTemplate restTemplate;

    @Value("${microservices.authentification}")
    private String authentificationMsUrl;

    @Override
    @Transactional
    public ResponseEntity<Object> saveComment(CommentDTO commentDTO) {

        log.info("CommentService: save comment");

        Optional<Post> optionalPost = postService.findById(commentDTO.getPostId());
        if(optionalPost.isPresent()){
            Post post = optionalPost.get();
            Comment comment = mapper.toComment(commentDTO, post);
            Comment savedComment = commentRepository.insert(comment);
            postService.updateNbrComments(post, "increment");
            User user = restTemplate.getForObject(authentificationMsUrl + "/user/id/" + savedComment.getUserId(), User.class);
            CommentResponseDTO commentResponse = mapper.toCommentResponse(savedComment, user);
            return new ResponseEntity<>(commentResponse, HttpStatus.valueOf(201));
        }

        return new ResponseEntity<>("Post not found", HttpStatus.valueOf(404));
    }

    @Override
    @Transactional
    public ResponseEntity<String> deleteComment(String commentId) {

        log.info("CommentService: delete comment");

        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if(optionalComment.isPresent()){
            commentRepository.deleteById(commentId);
            reportService.deleteReportsByComment(optionalComment.get());
            Post post = optionalComment.get().getPost();
            postService.updateNbrComments(post, "decrement");
            return new ResponseEntity<>("Comment deleted", HttpStatus.valueOf(200));
        }

        return new ResponseEntity<>("Comment not  found", HttpStatus.valueOf(404));
    }

    @Override
    public ResponseEntity<Object> updateComent(CommentUpdateDTO commentUpdateDTO) {

        log.info("CommentService: update comment");

        Optional<Comment> commentOptional = commentRepository.findById(commentUpdateDTO.getId());

        if (commentOptional.isPresent()){
            Comment comment = commentOptional.get();
            comment.setContent(commentUpdateDTO.getContent());
            comment.setUpdatedAt(new Date());
            Comment updatedComment = commentRepository.save(comment);
            return new ResponseEntity<>(updatedComment, HttpStatus.valueOf(200));
        }

        return new ResponseEntity<>("Comment not found", HttpStatus.valueOf(404));

    }

    @Override
    public ResponseEntity<Object> findCommentsByPost(String postId, int page, int size) {

        log.info("CommentService: retrieve comments");

        Optional<Post> optionalPost = postService.findById(postId);

        if(optionalPost.isPresent()){
            Post post = optionalPost.get();
            Page<Comment> comments = commentRepository.findCommentsByPost(
                    post,
                    PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt")));
            List<Long> usersId = comments.getContent().stream().map(Comment::getUserId).toList();

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<List<Long>> request = new HttpEntity<>(usersId, headers);
            User[] users = restTemplate.postForObject(authentificationMsUrl + "/users", request, User[].class);

            List<CommentResponseDTO> commentsResponse = new ArrayList<>();

            for(Comment c : comments.getContent()){
                assert users != null;
                for (User u : users){
                    if(u.getId().equals(c.getUserId())){
                        commentsResponse.add(mapper.toCommentResponse(c, u));
                        break;
                    }
                }
            }

            return new ResponseEntity<>(commentsResponse, HttpStatus.valueOf(200));
        }

        return new ResponseEntity<>("Comment not found", HttpStatus.valueOf(404));

    }

    @Override
    public void deleteCommentsByPost(Post post) {
        commentRepository.deleteCommentsByPost(post);
    }

    @Override
    public Optional<Comment> findCommentById(String id) {
        return commentRepository.findById(id);
    }

    @Override
    public List<Comment> findCommentsByUserId(Long userId) {
        return commentRepository.findCommentsByUserId(userId);
    }
}
