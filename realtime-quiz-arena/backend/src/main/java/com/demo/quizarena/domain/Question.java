package com.demo.quizarena.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @Column(name = "stem", nullable = false, length = 1000)
    private String stem;

    @Column(name = "options_json", nullable = false, length = 2000)
    private String optionsJson; // e.g. ["A","B","C","D"]

    @Column(name = "correct_answer", nullable = false)
    private String correctAnswer; // e.g. "A"

    @Column(name = "explanation", length = 2000)
    private String explanation; // 题目解释

    @Column(name = "category", length = 100)
    private String category; // 题目分类：如"趣味知识"、"JAVA知识"等

    @Column(name = "time_limit_sec", nullable = false)
    private int timeLimitSec = 15;

    @Column(name = "base_points", nullable = false)
    private int basePoints = 1000;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

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
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
