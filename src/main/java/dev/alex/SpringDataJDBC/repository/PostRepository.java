package dev.alex.SpringDataJDBC.repository;

import dev.alex.SpringDataJDBC.model.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository <Post, Integer>{
}
