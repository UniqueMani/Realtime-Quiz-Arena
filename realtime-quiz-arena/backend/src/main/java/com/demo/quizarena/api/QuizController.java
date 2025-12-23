package com.demo.quizarena.api;

import com.demo.quizarena.api.dto.CreateQuestionRequest;
import com.demo.quizarena.api.dto.CreateQuizRequest;
import com.demo.quizarena.domain.Question;
import com.demo.quizarena.domain.Quiz;
import com.demo.quizarena.repo.QuestionRepository;
import com.demo.quizarena.repo.QuizRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
@Tag(name = "Quiz")
public class QuizController {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;

    public QuizController(QuizRepository quizRepository, QuestionRepository questionRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
    }

    @PostMapping
    public Quiz createQuiz(@Valid @RequestBody CreateQuizRequest req) {
        Quiz quiz = new Quiz(req.title, req.isPublic);
        return quizRepository.save(quiz);
    }

    @GetMapping
    public List<Quiz> listQuizzes() {
        return quizRepository.findAll();
    }

    @PostMapping("/{quizId}/questions")
    public Question addQuestion(@PathVariable Long quizId, @Valid @RequestBody CreateQuestionRequest req) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow();
        String optionsJson = toJsonArray(req.options);

        Question q = new Question();
        q.setQuiz(quiz);
        q.setStem(req.stem);
        q.setOptionsJson(optionsJson);
        q.setCorrectAnswer(req.correctAnswer);
        q.setTimeLimitSec(req.timeLimitSec);
        q.setBasePoints(req.basePoints);
        return questionRepository.save(q);
    }

    @GetMapping("/{quizId}/questions")
    public List<Question> listQuestions(@PathVariable Long quizId) {
        return questionRepository.findByQuizIdOrderByIdAsc(quizId);
    }

    /**
     * Minimal JSON array encoder (demo).
     * For production, consider using Jackson's ObjectMapper.
     */
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
}
