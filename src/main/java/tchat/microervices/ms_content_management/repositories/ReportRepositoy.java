package tchat.microervices.ms_content_management.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import tchat.microervices.ms_content_management.beans.Comment;
import tchat.microervices.ms_content_management.beans.Post;
import tchat.microervices.ms_content_management.beans.Report;

import java.util.List;


public interface ReportRepositoy extends MongoRepository<Report, String> {

    Page<Report> findByStateAndPostNotNull(String state, Pageable pageable);
    Page<Report> findByStateAndCommentNotNull(String state, Pageable pageable);
    Page<Report> findByStateAndAdminId(String state, Long adminId, Pageable pageable);

    List<Report> findReportsByComment(Comment comment);

    List<Report> findReportsByPost(Post post);

    //List<Report> findReportsByUserId(Long userId);

    List<Report> findReportsByPostInOrCommentIn(List<Post> posts, List<Comment> comments);

    void deleteReportsByPost(Post post);

    void deleteReportsByComment(Comment comment);
}
