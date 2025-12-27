package com.demo.quizarena.realtime;

import java.util.List;

public class QuestionPush {
    public Long questionId;
    public String stem;
    public List<String> options;
    public long openedAtEpochMs;
    public long closedAtEpochMs;
    public int currentIndex; // 当前题目序号（如 1/20）
    public int totalCount; // 总题目数（20）

    public QuestionPush() {}
}
