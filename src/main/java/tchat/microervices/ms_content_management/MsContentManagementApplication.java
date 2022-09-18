package tchat.microervices.ms_content_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
/*@Slf4j*/
public class MsContentManagementApplication {

	//private static final RestTemplate restTemplate = new RestTemplate();

	@Bean
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}


	public static void main(String[] args) {
		SpringApplication.run(MsContentManagementApplication.class, args);
	}

	/*@Bean
	CommandLineRunner lineRunner(PostRepository postRepository,
								 LikeRepository likeRepository,
								 CommentRepository commentRepository,
								 ReportRepositoy reportRepositoy){

		String url = "http://localhost:8080/api/v1/authentication/user/";

		return args -> {
			//retrieve users from authentication microservice
			User user1 = restTemplate.getForObject(url + "/username1", User.class);
			User user2 = restTemplate.getForObject(url + "/username2", User.class);
			log.info(user1 + " || " + user2);

			//insert a post
			Post post = new Post( "This is my first post", "Public", new Date(), new Date(), user1);
			postRepository.insert(post);

			//inset a comment
			Comment comment = new Comment("This is my frist comment", new Date(), new Date(), post, user2);
			commentRepository.insert(comment);

			//insert a like
			Like like = new Like(post, user1);
			likeRepository.insert(like);

			//insert a report
			Report report = new Report("Violence", "Inciting violence or is a threat", new Date(), post, user2);
			reportRepositoy.insert(report);
		};
	}*/
}
