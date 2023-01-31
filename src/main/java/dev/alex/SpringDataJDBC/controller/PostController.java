package dev.alex.SpringDataJDBC.controller;

import dev.alex.SpringDataJDBC.model.Post;
import dev.alex.SpringDataJDBC.repository.AuthorRepository;
import dev.alex.SpringDataJDBC.repository.PostRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostRepository posts;
    private final AuthorRepository authors;

    public PostController(PostRepository posts, AuthorRepository authors){
        this.posts = posts;
        this.authors = authors;
    }
    @GetMapping
    public Iterable<Post> findAll(){
        return posts.findAll();
    }
}
