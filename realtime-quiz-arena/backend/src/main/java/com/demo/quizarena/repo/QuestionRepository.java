package com.demo.quizarena.repo;

import com.demo.quizarena.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByQuizIdOrderByIdAsc(Long quizId);
}
