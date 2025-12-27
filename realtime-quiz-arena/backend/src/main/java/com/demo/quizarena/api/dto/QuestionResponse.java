package com.demo.quizarena.api.dto;

import java.time.Instant;
import java.util.List;

public class QuestionResponse {
    public Long id;
    public String stem;
    public List<String> options;
    public String explanation;
    public String category;
    public Instant createdAt;
    public Instant updatedAt;

    public QuestionResponse() {}

    public QuestionResponse(Long id, String stem, List<String> options, String explanation, String category, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.stem = stem;
        this.options = options;
        this.explanation = explanation;
        this.category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}

