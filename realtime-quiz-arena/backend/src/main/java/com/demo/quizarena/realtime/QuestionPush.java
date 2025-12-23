package com.demo.quizarena.realtime;

import java.util.List;

public class QuestionPush {
    public Long questionId;
    public String stem;
    public List<String> options;
    public long openedAtEpochMs;
    public long closedAtEpochMs;

    public QuestionPush() {}
}
