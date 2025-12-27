package com.demo.quizarena.api;

import com.demo.quizarena.api.dto.CreateQuestionRequest;
import com.demo.quizarena.api.dto.QuestionResponse;
import com.demo.quizarena.api.dto.QuestionWithAnswerResponse;
import com.demo.quizarena.domain.Question;
import com.demo.quizarena.repo.QuestionRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/questions")
@Tag(name = "Question")
public class QuestionController {

    private final QuestionRepository questionRepository;

    public QuestionController(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @PostMapping
    public ResponseEntity<QuestionResponse> createQuestion(@Valid @RequestBody CreateQuestionRequest req) {
        String optionsJson = toJsonArray(req.options);
        
        Question q = new Question();
        q.setStem(req.stem);
        q.setOptionsJson(optionsJson);
        q.setCorrectAnswer(req.correctAnswer);
        q.setExplanation(req.explanation);
        q.setCategory(req.category);
        q.setTimeLimitSec(req.timeLimitSec);
        q.setBasePoints(req.basePoints);
        q.setUpdatedAt(Instant.now());
        
        Question saved = questionRepository.save(q);
        return ResponseEntity.status(HttpStatus.CREATED).body(toQuestionResponse(saved));
    }

    @GetMapping
    public ResponseEntity<List<QuestionResponse>> listQuestions(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<Question> questions;
        if (category != null && !category.isBlank()) {
            questions = questionRepository.findByCategory(category);
        } else {
            Pageable pageable = PageRequest.of(page, size);
            Page<Question> pageResult = questionRepository.findAll(pageable);
            questions = pageResult.getContent();
        }
        List<QuestionResponse> responses = questions.stream()
                .map(this::toQuestionResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponse> getQuestion(@PathVariable Long id) {
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found: " + id));
        return ResponseEntity.ok(toQuestionResponse(q));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody CreateQuestionRequest req) {
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found: " + id));
        
        String optionsJson = toJsonArray(req.options);
        q.setStem(req.stem);
        q.setOptionsJson(optionsJson);
        q.setCorrectAnswer(req.correctAnswer);
        q.setExplanation(req.explanation);
        q.setCategory(req.category);
        q.setTimeLimitSec(req.timeLimitSec);
        q.setBasePoints(req.basePoints);
        q.setUpdatedAt(Instant.now());
        
        Question saved = questionRepository.save(q);
        return ResponseEntity.ok(toQuestionResponse(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        if (!questionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        questionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/random")
    public ResponseEntity<List<QuestionResponse>> getRandomQuestions(
            @RequestParam(defaultValue = "20") int count) {
        List<Question> questions = questionRepository.findRandomQuestions(count);
        List<QuestionResponse> responses = questions.stream()
                .map(this::toQuestionResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    private QuestionResponse toQuestionResponse(Question q) {
        List<String> options = parseOptions(q.getOptionsJson());
        return new QuestionResponse(
                q.getId(),
                q.getStem(),
                options,
                q.getExplanation(),
                q.getCategory(),
                q.getCreatedAt(),
                q.getUpdatedAt()
        );
    }

    private String toJsonArray(List<String> options) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < options.size(); i++) {
            String opt = options.get(i)
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"");
            sb.append("\"").append(opt).append("\"");
            if (i < options.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private List<String> parseOptions(String jsonArrayLike) {
        String s = jsonArrayLike.trim();
        if (s.startsWith("[")) s = s.substring(1);
        if (s.endsWith("]")) s = s.substring(0, s.length() - 1);
        if (s.isBlank()) return List.of();
        return Arrays.stream(s.split(","))
                .map(String::trim)
                .map(x -> x.replaceAll("^\"|\"$", ""))
                .collect(Collectors.toList());
    }
}

