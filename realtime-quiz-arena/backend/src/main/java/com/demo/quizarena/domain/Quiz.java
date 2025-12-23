package com.demo.quizarena.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "quizzes")
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private boolean isPublic = false;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public Quiz() {}

    public Quiz(String title, boolean isPublic) {
        this.title = title;
        this.isPublic = isPublic;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean isPublic) { this.isPublic = isPublic; }
    public Instant getCreatedAt() { return createdAt; }
}
