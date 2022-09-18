package tchat.microervices.ms_content_management.services;

import org.springframework.http.ResponseEntity;
import tchat.microervices.ms_content_management.beans.Comment;
import tchat.microervices.ms_content_management.beans.Post;
import tchat.microervices.ms_content_management.dtos.report.ReportDTO;

public interface ReportService {

    ResponseEntity<String> reportPostOrComment(ReportDTO reportDTO);

    ResponseEntity<Object> findReportsPostsOrComments(int page, int size, String commentOrPost);

    ResponseEntity<String> setReportOnReview(String reportId, Long adminId);

    ResponseEntity<Object> getReportsOfAdmin(int page, int size, Long adminId);

    ResponseEntity<String> skipReport(String id, String commentOrPost);

    ResponseEntity<String> deleteComment(String commentId);
    ResponseEntity<String> deletePost(String postId);

    ResponseEntity<String> lockUser(Long userId);

    void deleteReportsByPost(Post post);

    void deleteReportsByComment(Comment comment);
}
