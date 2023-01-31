package dev.alex.SpringDataJDBC;

import dev.alex.SpringDataJDBC.model.Author;
import dev.alex.SpringDataJDBC.model.Comment;
import dev.alex.SpringDataJDBC.model.Post;
import dev.alex.SpringDataJDBC.repository.AuthorRepository;
import dev.alex.SpringDataJDBC.repository.PostRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

@SpringBootApplication
public class SpringDataJdbcApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringDataJdbcApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(PostRepository posts, AuthorRepository authors){
		return args -> {
			AggregateReference<Author, Integer> alex = AggregateReference.to(authors.save(new Author(null,"Alex", "black", "ale@email.com", "alexblack")).id());
			Post post = new Post("Hello World", "Welcome", alex);
			post.addComment(new Comment("Alex", "First Comment"));
			posts.save(post);

		};
	}
}

