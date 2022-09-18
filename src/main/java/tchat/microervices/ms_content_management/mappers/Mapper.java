package tchat.microervices.ms_content_management.mappers;

import tchat.microervices.ms_content_management.beans.Comment;
import tchat.microervices.ms_content_management.beans.Like;
import tchat.microervices.ms_content_management.beans.Post;
import tchat.microervices.ms_content_management.beans.Report;
import tchat.microervices.ms_content_management.dtos.comment.CommentDTO;
import tchat.microervices.ms_content_management.dtos.comment.CommentResponseDTO;
import tchat.microervices.ms_content_management.dtos.like.LikeDTO;
import tchat.microervices.ms_content_management.dtos.like.LikeResponseDTO;
import tchat.microervices.ms_content_management.dtos.post.PostResponseDTO;
import tchat.microervices.ms_content_management.dtos.report.ReportDTO;
import tchat.microervices.ms_content_management.dtos.report.ReportResponseDTO;
import tchat.microervices.ms_content_management.vos.User;

public interface Mapper {


    Like toLike(LikeDTO likeDTO, Post post);

    Comment toComment(CommentDTO commentDTO, Post post);
    PostResponseDTO toPostResponse(Post post, User user, User userShared);

    Report toReport(ReportDTO reportDTO);

    CommentResponseDTO toCommentResponse(Comment comment, User user);

    ReportResponseDTO toReportResponse(Report report, User user);

    LikeResponseDTO toLikeResponse(Like like);
}
