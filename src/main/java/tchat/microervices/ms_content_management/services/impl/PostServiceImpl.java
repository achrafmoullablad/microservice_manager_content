package tchat.microervices.ms_content_management.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import tchat.microervices.ms_content_management.beans.Post;
import tchat.microervices.ms_content_management.dtos.post.PostResponseDTO;
import tchat.microervices.ms_content_management.dtos.post.SharedPostDTO;
import tchat.microervices.ms_content_management.mappers.Mapper;
import tchat.microervices.ms_content_management.repositories.PostRepository;
import tchat.microervices.ms_content_management.services.CommentService;
import tchat.microervices.ms_content_management.services.LikeService;
import tchat.microervices.ms_content_management.services.PostService;
import tchat.microervices.ms_content_management.services.ReportService;
import tchat.microervices.ms_content_management.utils.FileUtil;
import tchat.microervices.ms_content_management.vos.User;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Autowired
    private ReportService reportService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private CommentService commentService;

    private final Environment environment;

    private final RestTemplate restTemplate;

    private final Mapper mapper;

    private final FileUtil fileUtil;

    @Value("${microservices.authentification}")
    private String authentificationMsUrl;

    @Override
    public ResponseEntity<Object> insertPost(Optional<MultipartFile> photoOrVideo,
                                           Optional<String> content,
                                           String userId) {

        log.info("PostService: Save post");

        if((photoOrVideo.isPresent() && photoOrVideo.get().getSize() > 0)
                || (content.isPresent() && content.get().length() > 0)){
            Post post = new Post();
            post.setCreatedAt(new Date());
            post.setUpdatedAt(new Date());
            post.setUserId(Long.valueOf(userId));
            post.setTypeShare(false);

            content.ifPresent(post::setContent);

            Post insertedPost = photoOrVideo.map(multipartFile -> savePost(multipartFile, post)).orElseGet(() -> postRepository.insert(post));

            User user = getUserByUserId(Long.valueOf(userId));
            PostResponseDTO postResponse = mapper.toPostResponse(insertedPost, user, null);
            return new ResponseEntity<>(postResponse, HttpStatus.valueOf(200));
        }

        return new ResponseEntity<>("Post is empty", HttpStatus.valueOf(400));
    }



    @Override
    public ResponseEntity<Object> updatePost(Optional<MultipartFile> photoOrVideo,
                                             Optional<String> content,
                                             String postId,
                                             boolean deletePhotoOrVideo) {

        log.info("PostService: Update post");

        Optional<Post> optionalPost = postRepository.findById(postId);
        if(optionalPost.isPresent()){
            Post post = optionalPost.get();

            if (!post.isTypeShare()
                    && ((photoOrVideo.isEmpty() || photoOrVideo.get().getSize() == 0) && (content.isEmpty() || content.get().length() == 0))
                    && (post.getPhotoOrVideo() == null || deletePhotoOrVideo))
                return new ResponseEntity<>("Post is empty", HttpStatus.valueOf(400));

            else {
                post.setUpdatedAt(new Date());
                Post savedPost = null;

                if (content.isPresent())
                    post.setContent(content.get());
                else
                    post.setContent(null);

                if (!post.isTypeShare()) {
                    if (photoOrVideo.isPresent()) {
                        String oldPhotoOrVideoName = getPhotoNameFromPhotoUrl(post.getPhotoOrVideo());
                        if (oldPhotoOrVideoName != null)
                            deletePhotoVideoFile(environment.getProperty("posts.uploads"), oldPhotoOrVideoName);

                        savedPost = savePost(photoOrVideo.get(), post);
                        return new ResponseEntity<>(savedPost, HttpStatus.valueOf(200));
                    } else {
                        if (deletePhotoOrVideo) {
                            String uploadDir = environment.getProperty("posts.uploads");
                            String oldPhotoOrVideoName = getPhotoNameFromPhotoUrl(post.getPhotoOrVideo());
                            deletePhotoVideoFile(uploadDir, oldPhotoOrVideoName);
                            post.setPhotoOrVideo(null);
                            post.setType(null);
                        }
                    }
                }

                savedPost = postRepository.save(post);
                return new ResponseEntity<>(savedPost, HttpStatus.valueOf(200));
            }
        }

        return new ResponseEntity<>("Post not found", HttpStatus.valueOf(404));

    }

    @Override
    @Transactional
    public ResponseEntity<String> deletePost(String id) {

        log.info("PostService: Delete post");
        Optional<Post> optionalPost = postRepository.findById(id);

        if(optionalPost.isPresent()) {

            Post post = optionalPost.get();
            likeService.deleteLikesByPost(post);
            commentService.deleteCommentsByPost(post);
            reportService.deleteReportsByPost(post);
            postRepository.deleteById(id);

            if(post.isTypeShare()){
                Post p = post.getPost();
                Long nbrShares = p.getNbrShares();
                p.setNbrShares(--nbrShares);
                postRepository.save(p);
            }

            return new ResponseEntity<>("Post deleted", HttpStatus.valueOf(200));
        }

        return new ResponseEntity<>("Post not found!", HttpStatus.valueOf(404));
    }

    @Override
    public void updateNbrLikes(Post post, String type){
        Long nbrLikes = post.getNbrLikes();
        if(type.equals("increment"))
            post.setNbrLikes(++nbrLikes);
        else
            post.setNbrLikes(--nbrLikes);
        postRepository.save(post);
    }

    @Override
    public void updateNbrComments(Post post, String type){
        Long nbrComments = post.getNbrComments();
        if(type.equals("increment"))
            post.setNbrComments(++nbrComments);
        else
            post.setNbrComments(--nbrComments);
        postRepository.save(post);
    }

    @Override
    public List<Post> findPostsByUserId(Long userId) {
        return postRepository.findPostsByUserId(userId);
    }

    @Override
    public List<PostResponseDTO> findPostsByUserId(int page, int size) {
        User[] usersList = restTemplate.getForObject(authentificationMsUrl + "/users" , User[].class);//I should change the url

        if(usersList != null && usersList.length > 0 ){

            List<User> users = new ArrayList<>(List.of(usersList));
            users.removeIf(u -> u.isExpired() || u.isLocked());
            List<Long> userIds = users.stream().map(User::getId).toList();
            Page<Post> posts = postRepository.findPostsByUserIdIn(
                    userIds,
                    PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt")));
            List<PostResponseDTO> postDTOS = new ArrayList<>();

            for (Post p: posts.getContent()){
                for (User u : users){
                    if (p.getUserId().equals(u.getId())){
                        if(p.isTypeShare())
                            postDTOS.add(mapper.toPostResponse(p, getUserByUserId(p.getPost().getUserId()), u));
                        else
                            postDTOS.add(mapper.toPostResponse(p, u, null));
                        break;
                    }
                }
            }

            return postDTOS;
        }
        return null;
    }

    @Override
    @Transactional
    public ResponseEntity<Object> createNewSharedPost(SharedPostDTO sharedPost) {
        Optional<Post> optionalPost = findById(sharedPost.getPostId());

        if(optionalPost.isPresent()){
            Post newSharedPost = new Post();
            Post post = optionalPost.get();

            if(post.isTypeShare())
                newSharedPost.setPost(post.getPost());
            else
                newSharedPost.setPost(post);

            if(sharedPost.getContent() != null && !sharedPost.getContent().equals(""))
                newSharedPost.setContent(sharedPost.getContent());
            newSharedPost.setUserId(sharedPost.getUserId());
            newSharedPost.setCreatedAt(new Date());
            newSharedPost.setUpdatedAt(new Date());
            newSharedPost.setTypeShare(true);
            Post insertedSharedPost = postRepository.insert(newSharedPost);

            Long nbrShares = post.getNbrShares();
            post.setNbrShares(++nbrShares);
            postRepository.save(post);

            return new ResponseEntity<>(insertedSharedPost, HttpStatus.valueOf(200));
        }

        return new ResponseEntity<>("Post not found", HttpStatus.valueOf(404));
    }

    public Optional<Post> findById(String postId) {
        return postRepository.findById(postId);
    }

    private User getUserByUserId(Long id){
        return restTemplate.getForObject(authentificationMsUrl + "/user/id/" + id, User.class);
    }

    private void deletePhotoVideoFile(String uploadDir, String oldPhotoOrVideoName){
        try {
            fileUtil.deletePhotoOrVideo(uploadDir, oldPhotoOrVideoName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Post savePost(MultipartFile photoVideoFile, Post post){
        String uploadDir = environment.getProperty("posts.uploads");
        String newPhotoVideoName = fileUtil.namingFile(photoVideoFile);
        String newPhotoVideoPath = environment.getProperty("app.root.backend") +
                File.separator + "uploads" + File.separator + newPhotoVideoName;
        try {
            fileUtil.saveNewPhotoOrVideo(uploadDir, newPhotoVideoName, photoVideoFile);
        } catch (IOException e) {
            throw new RuntimeException();
        }
        post.setPhotoOrVideo(newPhotoVideoPath);
        post.setType(photoVideoFile.getContentType());
        return postRepository.save(post);
    }

    private String getPhotoNameFromPhotoUrl(String photoUrl) {
        if (photoUrl != null) {
            String stringToOmit = environment.getProperty("app.root.backend") + File.separator
                    + "uploads" + File.separator;
            return photoUrl.substring(stringToOmit.length());
        }
        return null;
    }
}
