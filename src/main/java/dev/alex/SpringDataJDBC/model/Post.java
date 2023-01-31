package dev.alex.SpringDataJDBC.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

import java.time.LocalDateTime;

public class Post {
    @Id
    private Integer id;
    private String title;
    private String content;
    private LocalDateTime publishedOn;
    private  LocalDateTime updatedOn;
    private AggregateReference<Author, Integer> author;

    public Post(String title, String content, AggregateReference<Author, Integer> author){
        this.title = title;
        this.content = content;
        this.publishedOn = LocalDateTime.now();
        this.updatedOn = null;
        this.author = author;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getPublishedOn() {
        return publishedOn;
    }

    public void setPublishedOn(LocalDateTime publishedOn) {
        this.publishedOn = publishedOn;
    }

    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", publishedOn=" + publishedOn +
                ", updatedOn=" + updatedOn +
                '}';
    }
}




