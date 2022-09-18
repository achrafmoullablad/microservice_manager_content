package tchat.microervices.ms_content_management.mappers.impl;

import org.springframework.stereotype.Component;
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
import tchat.microervices.ms_content_management.mappers.Mapper;
import tchat.microervices.ms_content_management.vos.User;

import java.util.Date;

@Component
public class MapperImpl implements Mapper {

    public PostResponseDTO toPostResponse(Post post, User userCreatedPost, User userSharedPost){
        PostResponseDTO postResponse = new PostResponseDTO();
        postResponse.setId(post.getId());
        postResponse.setNbrLikes(post.getNbrLikes());
        postResponse.setNbrComments(post.getNbrComments());
        postResponse.setNbrShares(post.getNbrShares());
        postResponse.setCreatedAt(post.getCreatedAt());
        postResponse.setUpdatedAt(post.getUpdatedAt());
        postResponse.setContent(post.getContent());
        postResponse.setPhotoOrVideo(post.getPhotoOrVideo());
        postResponse.setType(post.getType());
        postResponse.setTypeShare(post.isTypeShare());
        postResponse.setUserCreatedPost(userCreatedPost);
        if(post.isTypeShare()) {
            postResponse.setPost(post.getPost());
            postResponse.setUserSharedPost(userSharedPost);
        }
        return postResponse;
    }

    @Override
    public Report toReport(ReportDTO reportDTO) {

        Report report = new Report();
        report.setUserId(reportDTO.getUserId());
        report.setReason(reportDTO.getReason());
        return report;
    }

    @Override
    public CommentResponseDTO toCommentResponse(Comment comment, User user) {
        CommentResponseDTO commentResponse = new CommentResponseDTO();
        commentResponse.setId(comment.getId());
        commentResponse.setContent(comment.getContent());
        commentResponse.setCreatedAt(comment.getCreatedAt());
        commentResponse.setUpdatedAt(comment.getUpdatedAt());
        commentResponse.setUser(user);
        return commentResponse;
    }

    @Override
    public ReportResponseDTO toReportResponse(Report report, User user) {

        ReportResponseDTO reportResponse = new ReportResponseDTO();
        reportResponse.setId(report.getId());
        reportResponse.setComment(report.getComment());
        reportResponse.setPost(report.getPost());
        reportResponse.setUser(user);
        reportResponse.setReason(report.getReason());
        reportResponse.setSendedAt(report.getSendedAt());
        return reportResponse;

    }

    @Override
    public LikeResponseDTO toLikeResponse(Like like) {
        LikeResponseDTO likeResponse = new LikeResponseDTO();
        likeResponse.setType(like.getType());
        likeResponse.setUserId(like.getUserId());
        return likeResponse;
    }

    @Override
    public Like toLike(LikeDTO likeDTO, Post post) {

        Like like = new Like();
        like.setUserId(likeDTO.getUserId());
        like.setPost(post);
        like.setType(likeDTO.getType());
        return like;
    }

    @Override
    public Comment toComment(CommentDTO commentDTO, Post post) {

        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setUserId(commentDTO.getUserId());
        comment.setPost(post);
        comment.setCreatedAt(new Date());
        comment.setUpdatedAt(new Date());
        return comment;
    }
}
