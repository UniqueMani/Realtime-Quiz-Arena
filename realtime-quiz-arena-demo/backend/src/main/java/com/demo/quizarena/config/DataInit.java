package com.demo.quizarena.config;

import com.demo.quizarena.domain.Question;
import com.demo.quizarena.domain.Quiz;
import com.demo.quizarena.repo.QuestionRepository;
import com.demo.quizarena.repo.QuizRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInit {

    @Bean
    CommandLineRunner init(QuizRepository quizRepository, QuestionRepository questionRepository) {
        return args -> {
            if (quizRepository.count() == 0) {
                Quiz quiz = quizRepository.save(new Quiz("Demo Quiz", true));
                Question q = new Question();
                q.setQuiz(quiz);
                q.setStem("Demo: What is 2 + 2 ?");
                q.setOptionsJson("[\"3\",\"4\",\"5\",\"22\"]");
                q.setCorrectAnswer("4");
                q.setTimeLimitSec(15);
                q.setBasePoints(1000);
                questionRepository.save(q);
            }
        };
    }
}
