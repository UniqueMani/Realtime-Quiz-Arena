package com.demo.quizarena.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Quiz quiz;

    @Column(nullable = false, length = 1000)
    private String stem;

    @Column(nullable = false, length = 2000)
    private String optionsJson; // e.g. ["A","B","C","D"]

    @Column(nullable = false)
    private String correctAnswer; // e.g. "A"

    @Column(nullable = false)
    private int timeLimitSec = 15;

    @Column(nullable = false)
    private int basePoints = 1000;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public Question() {}

    public Question(Quiz quiz, String stem, String optionsJson, String correctAnswer) {
        this.quiz = quiz;
        this.stem = stem;
        this.optionsJson = optionsJson;
        this.correctAnswer = correctAnswer;
    }

    public Long getId() { return id; }
    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }
    public String getStem() { return stem; }
    public void setStem(String stem) { this.stem = stem; }
    public String getOptionsJson() { return optionsJson; }
    public void setOptionsJson(String optionsJson) { this.optionsJson = optionsJson; }
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    public int getTimeLimitSec() { return timeLimitSec; }
    public void setTimeLimitSec(int timeLimitSec) { this.timeLimitSec = timeLimitSec; }
    public int getBasePoints() { return basePoints; }
    public void setBasePoints(int basePoints) { this.basePoints = basePoints; }
    public Instant getCreatedAt() { return createdAt; }
}
