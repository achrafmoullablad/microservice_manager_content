package tchat.microervices.ms_content_management.rests;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tchat.microervices.ms_content_management.dtos.report.ReportDTO;
import tchat.microervices.ms_content_management.services.ReportService;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/v1/content_management/reports")
@AllArgsConstructor
@Slf4j
@Validated
public class ReportRest {

    private ReportService reportService;

    @PostMapping("/save")
    public ResponseEntity<String> reportPostOrComment(@RequestBody @Valid ReportDTO reportDTO) {
        log.info("ReportRest : report a post " + reportDTO.getPostId());
        return reportService.reportPostOrComment(reportDTO);
    }

    @GetMapping("/{page}/{size}/{commentOrPost}")
    public ResponseEntity<Object> findReportsPostsOrComments(
            @PathVariable int page,
            @PathVariable int size,
            @PathVariable @NotNull @NotEmpty String commentOrPost) {
        log.info("ReportRest : reports posts or commments, page : " + page + " and size : " + size);
        return reportService.findReportsPostsOrComments(page, size, commentOrPost);
    }

    @PutMapping("/on-review/{reportId}/{adminId}")
    public ResponseEntity<String> setReportOnReview(
            @PathVariable @NotNull @NotEmpty String reportId,
            @PathVariable Long adminId) {
        log.info("ReportRest : set report on review : " + reportId);
        return reportService.setReportOnReview(reportId, adminId);
    }

    @GetMapping("/admin/{page}/{size}/{adminId}")
    public ResponseEntity<Object> getReportsOfAdmin(
            @PathVariable int page,
            @PathVariable int size,
            @PathVariable Long adminId) {
        log.info("ReportRest : get reports of amin : " + adminId);
        return reportService.getReportsOfAdmin(page, size, adminId);
    }

    @PutMapping("/skip/{commentOrPost}/{id}")
    public ResponseEntity<String> skipReport(@PathVariable @NotNull @NotEmpty String commentOrPost,
                                             @PathVariable @NotNull @NotEmpty String id) {
        log.info("ReportRest : skip a report " + id);
        return reportService.skipReport(id, commentOrPost);
    }

    @DeleteMapping("/delete/comment/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable @NotNull @NotEmpty String commentId) {
        log.info("ReportRest : delete the comment " + commentId);
        return reportService.deleteComment(commentId);
    }

    @DeleteMapping("/delete/post/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable @NotNull @NotEmpty String postId) {
        log.info("ReportRest : delete the post " + postId);
        return reportService.deletePost(postId);
    }

    @PutMapping("/user/lock/{userId}")
    public ResponseEntity<String> lockUser(@PathVariable Long userId) {
        log.info("ReportRest : lock the user " + userId);
        return reportService.lockUser(userId);
    }
}
