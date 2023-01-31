package dev.alex.SpringDataJDBC;

import dev.alex.SpringDataJDBC.model.Post;
import dev.alex.SpringDataJDBC.repository.PostRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringDataJdbcApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringDataJdbcApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(PostRepository posts){
		return args -> {
			posts.save(new Post("Hello World", "Welcome"));
		};
	}
}

