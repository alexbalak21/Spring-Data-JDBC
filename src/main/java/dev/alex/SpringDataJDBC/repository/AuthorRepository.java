package dev.alex.SpringDataJDBC.repository;

import dev.alex.SpringDataJDBC.model.Author;
import org.springframework.data.repository.CrudRepository;

public interface AuthorRepository extends CrudRepository<Author, Integer> {
}
