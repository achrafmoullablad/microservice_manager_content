package tchat.microervices.ms_content_management.services.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tchat.microervices.ms_content_management.beans.Like;
import tchat.microervices.ms_content_management.beans.Post;
import tchat.microervices.ms_content_management.dtos.like.LikeDTO;
import tchat.microervices.ms_content_management.dtos.like.LikeResponseDTO;
import tchat.microervices.ms_content_management.mappers.Mapper;
import tchat.microervices.ms_content_management.repositories.LikeRepository;
import tchat.microervices.ms_content_management.services.LikeService;
import tchat.microervices.ms_content_management.services.PostService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class LikeServiceImpl implements LikeService {

    private LikeRepository likeRepository;
    private PostService postService;

    private Mapper mapper;

    @Override
    @Transactional
    public ResponseEntity<Object> saveLike(LikeDTO likeDTO) {

        log.info("LikeService: Save like");

        Optional<Post> optionalPost = postService.findById(likeDTO.getPostId());

        if(optionalPost.isPresent()){
            Post post = optionalPost.get();
            Like like = mapper.toLike(likeDTO, post);
            Like savedLike = likeRepository.insert(like);
            postService.updateNbrLikes(post, "increment");
            return new ResponseEntity<>(savedLike, HttpStatus.valueOf(201));
        }

        return new ResponseEntity<>("Post not found", HttpStatus.valueOf(404));
    }

    @Override
    @Transactional
    public ResponseEntity<String> deleteLike(LikeDTO likeDTO) {

        log.info("LikeRest: Delete like");

        Optional<Post> optionalPost = postService.findById(likeDTO.getPostId());

        if(optionalPost.isPresent()){
            Post post = optionalPost.get();
            likeRepository.deleteByPostAndUserId(post , likeDTO.getUserId());
            postService.updateNbrLikes(post, "decrement");
            return new ResponseEntity<>("Like deleted", HttpStatus.valueOf(200));
        }

        return new ResponseEntity<>("Post not found", HttpStatus.valueOf(404));
    }

   @Override
    public ResponseEntity<Object> findLikesByPost(String postId) {

        log.info("LikeService: Retrieve likes of post");

        Optional<Post> optionalPost = postService.findById(postId);
        if(optionalPost.isPresent()){
            List<Like> likes = likeRepository.findLikesByPost(optionalPost.get());
            int nbrLike = 0, nbrDislike = 0,  nbrLove = 0, nbrHaha = 0, nbrSad = 0,
                    nbrAngry = 0, nbrInsightful = 0, nbrCelebrate = 0;

            for (Like l : likes){
                switch (l.getType()) {
                    case "Like" -> ++nbrLike;
                    case "Dislike" -> ++nbrDislike;
                    case "Love" -> ++nbrLove;
                    case "Haha" -> ++nbrHaha;
                    case "Sad" -> ++nbrSad;
                    case "Angry" -> ++nbrAngry;
                    case "Insightful" -> ++nbrInsightful;
                    case "Celebrate" -> ++nbrCelebrate;
                }
            }
            Map<String, Integer> response = new HashMap<>();
            response.put("Like", nbrLike);
            response.put("Dislike", nbrDislike);
            response.put("Love", nbrLove);
            response.put("Haha", nbrHaha);
            response.put("Sad", nbrSad);
            response.put("Angry", nbrAngry);
            response.put("Insightful", nbrInsightful);
            response.put("Celebrate", nbrCelebrate);
            return new ResponseEntity<>(response, HttpStatus.valueOf(200));
        }

        return new ResponseEntity<>("Post not found", HttpStatus.valueOf(404));
    }

    public List<LikeResponseDTO> getUsersLikedPost(String postId) {

        log.info("LikeService: Retrieve users liked post with type");

        Optional<Post> optionalPost = postService.findById(postId);

        if (optionalPost.isPresent()) {
            List<Like> likes = likeRepository.findLikesByPost(optionalPost.get());
            return likes.stream().map(like -> mapper.toLikeResponse(like)).toList();
        }
        return null;

    }

    @Override
    public ResponseEntity<String> updateLike(LikeDTO likeDTO) {
        log.info("LikeService: Update like");

        Optional<Post> optionalPost = postService.findById(likeDTO.getPostId());

        if(optionalPost.isPresent()){
            Like like = likeRepository.findByUserIdAndPost(likeDTO.getUserId(), optionalPost.get());
            like.setType(likeDTO.getType());
            likeRepository.save(like);
            return new ResponseEntity<>("Like updated", HttpStatus.valueOf(200));
        }
        return new ResponseEntity<>("Post not found", HttpStatus.valueOf(404));
    }

    @Override
    public void deleteLikesByPost(Post post) {
        likeRepository.deleteLikesByPost(post);
    }

}
