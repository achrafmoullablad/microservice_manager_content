package tchat.microervices.ms_content_management.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import tchat.microervices.ms_content_management.beans.Report;
import tchat.microervices.ms_content_management.dtos.report.ReportDTO;
import tchat.microervices.ms_content_management.dtos.report.ReportResponseDTO;
import tchat.microervices.ms_content_management.mappers.Mapper;
import tchat.microervices.ms_content_management.repositories.ReportRepositoy;
import tchat.microervices.ms_content_management.services.CommentService;
import tchat.microervices.ms_content_management.services.PostService;
import tchat.microervices.ms_content_management.services.ReportService;
import tchat.microervices.ms_content_management.vos.User;

import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class ReportServiceImpl implements ReportService {

    private final ReportRepositoy reportRepositoy;

    private final PostService postService;

    private final CommentService commentService;

    private final RestTemplate restTemplate;

    private final Mapper mapper;

    @Value("${microservices.authentification}")
    private String authentificationMsUrl;


    @Override
    public ResponseEntity<String> reportPostOrComment(ReportDTO reportDTO) {
        log.info("ReportService : report a post");

        Report report = mapper.toReport(reportDTO);

        report.setSendedAt(new Date());
        report.setState("Added");

        if(reportDTO.getPostId() != null){
            Optional<Post> optionalPost = postService.findById(reportDTO.getPostId());
            if(optionalPost.isPresent()){
                Post post = optionalPost.get();
                report.setPost(post);
            }else
                return new ResponseEntity<>("Post not found", HttpStatus.valueOf(404));
        }else {
            Optional<Comment> optionalComment = commentService.findCommentById(reportDTO.getCommentId());
            if(optionalComment.isPresent()){
                Comment comment = optionalComment.get();
                report.setComment(comment);
            }else
                return new ResponseEntity<>("Comment not found", HttpStatus.valueOf(404));
        }

        reportRepositoy.insert(report);

        return new ResponseEntity<>("report saved", HttpStatus.valueOf(201));

    }

    @Override
    public ResponseEntity<Object> findReportsPostsOrComments(int page, int size, String commentOrPost) {
        log.info("ReportService : reports posts or commments");

        if(commentOrPost.equals("posts") || commentOrPost.equals("comments")){
            Page<Report> reportPage;

            if(commentOrPost.equals("posts")){
                reportPage = reportRepositoy.findByStateAndPostNotNull(
                        "Added",
                        PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "sendedAt")));
            }else {
                reportPage = reportRepositoy.findByStateAndCommentNotNull(
                        "Added",
                        PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "sendedAt")));
            }

            List<Report> reports = reportPage.getContent();

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<List<Long>> request = new HttpEntity<>(reports.stream().map(Report::getUserId).toList(), headers);
            User[] users = restTemplate.postForObject(authentificationMsUrl + "/users", request, User[].class);

            List<ReportResponseDTO> reportResponse = new ArrayList<>();
            for (Report r : reports){
                for (User u : Objects.requireNonNull(users)){
                    if(u.getId().equals(r.getUserId()))
                        reportResponse.add(mapper.toReportResponse(r, u));
                }
            }

            return new ResponseEntity<>(reportResponse, HttpStatus.valueOf(200));
        }
        return new ResponseEntity<>("Data not valid", HttpStatus.valueOf(400));
    }

    @Override
    public ResponseEntity<String> setReportOnReview(String reportId, Long adminId) {
        log.info("ReportService : set report on review");

        Optional<Report> optionalReport = reportRepositoy.findById(reportId);
        if(optionalReport.isPresent()){
            Report report = optionalReport.get();
            report.setState("On review");
            report.setAdminId(adminId);
            reportRepositoy.save(report);
            return new ResponseEntity<>("State edited", HttpStatus.valueOf(200));
        }
        return new ResponseEntity<>("Report not found", HttpStatus.valueOf(404));
    }

    @Override
    public ResponseEntity<Object> getReportsOfAdmin(int page, int size, Long adminId) {
        log.info("ReportService : get Reports Of Admin");

        Page<Report> reportPage = reportRepositoy.findByStateAndAdminId(
                "On review",
                adminId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "sendedAt")));
        List<Report> reports = reportPage.getContent();

        User user = restTemplate.getForObject(authentificationMsUrl + "/user/id/" + adminId, User.class);
        List<ReportResponseDTO> reportResponse =  reports.stream().map(report -> mapper.toReportResponse(report, user)).toList();

        return new ResponseEntity<>(reportResponse, HttpStatus.valueOf(200));
    }

    @Override
    public ResponseEntity<String> skipReport(String id, String commentOrPost) {
        log.info("ReportService : skip a report");
        if (commentOrPost.equals("comment") || commentOrPost.equals("post") ){
            List<Report> reports;
            if(commentOrPost.equals("comment")){
                Comment comment = commentService.findCommentById(id).get();
                reports = reportRepositoy.findReportsByComment(comment);
            }
            else{
                Post post = postService.findById(id).get();
                reports = reportRepositoy.findReportsByPost(post);
            }
            reports.forEach(report -> {
                report.setResolvedAt(new Date());
                report.setState("Solved");
                report.setDecision("Skip");
            });
            reportRepositoy.saveAll(reports);
            return new ResponseEntity<>("Reports updated", HttpStatus.valueOf(200));
        }
        return new ResponseEntity<>("comment or post field not valid", HttpStatus.valueOf(400));
    }

    @Override
    @Transactional
    public ResponseEntity<String> deleteComment(String commentId) {
        log.info("ReportService : delete the comment");
        Optional<Comment> comment = commentService.findCommentById(commentId);
        if (comment.isPresent()){
            List<Report> reports = reportRepositoy.findReportsByComment(comment.get());
            reports.forEach(report -> {
                report.setResolvedAt(new Date());
                report.setState("Solved");
                report.setDecision("Delete comment");
            });
            reportRepositoy.saveAll(reports);
            commentService.deleteComment(commentId);
            return new ResponseEntity<>("comment deleted", HttpStatus.valueOf(200));
        }
        return new ResponseEntity<>("Comment not found", HttpStatus.valueOf(404));
    }

    @Override
    @Transactional
    public ResponseEntity<String> deletePost(String postId) {
        log.info("ReportService : delete the post");
        Optional<Post> optionalPost = postService.findById(postId);
        if (optionalPost.isPresent()){
            Post post = optionalPost.get();
            List<Report> reports = reportRepositoy.findReportsByPost(post);
            reports.forEach(report -> {
                report.setResolvedAt(new Date());
                report.setState("Solved");
                report.setDecision("Delete post");
            });
            reportRepositoy.saveAll(reports);
            postService.deletePost(postId);
            return new ResponseEntity<>("Post deleted", HttpStatus.valueOf(200));
        }
        return new ResponseEntity<>("Post not found", HttpStatus.valueOf(404));
    }

    @Override
    @Transactional
    public ResponseEntity<String> lockUser(Long userId) {
        log.info("ReportService : lock the user");
        restTemplate.put(authentificationMsUrl + "/user/lock/" + userId, String.class);
        List<Post> posts = postService.findPostsByUserId(userId);
        List<Comment> comments = commentService.findCommentsByUserId(userId);
        List<Report> reports = reportRepositoy.findReportsByPostInOrCommentIn(posts, comments);
        reports.forEach(report -> {
            report.setResolvedAt(new Date());
            report.setState("Solved");
            report.setDecision("Lock user");
        });
        reportRepositoy.saveAll(reports);
        return new ResponseEntity<>("User locked", HttpStatus.valueOf(200));
    }

    @Override
    public void deleteReportsByPost(Post post) {
        reportRepositoy.deleteReportsByPost(post);
    }

    @Override
    public void deleteReportsByComment(Comment comment) {
        reportRepositoy.deleteReportsByComment(comment);
    }


}
