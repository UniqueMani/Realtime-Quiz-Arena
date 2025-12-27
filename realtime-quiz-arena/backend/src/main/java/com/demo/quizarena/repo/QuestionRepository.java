package com.demo.quizarena.repo;

import com.demo.quizarena.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByQuizIdOrderByIdAsc(Long quizId);
    
    // PostgreSQL 和 H2 (PostgreSQL mode) 都支持 RANDOM() 函数
    // 使用 ?1 作为位置参数，兼容性更好
    @Query(value = "SELECT * FROM questions ORDER BY RANDOM() LIMIT ?1", nativeQuery = true)
    List<Question> findRandomQuestions(int count);
    
    List<Question> findByCategory(String category);
}
