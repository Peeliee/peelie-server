package com.peelie.questionnaire.infra.question;

import com.peelie.common.exception.BaseException;
import com.peelie.common.exception.ErrorCode;
import com.peelie.questionnaire.domain.question.Question;
import com.peelie.questionnaire.domain.question.QuestionOption;
import com.peelie.questionnaire.domain.question.QuestionReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuestionReaderImpl implements QuestionReader {

    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;

    @Override
    public Question getQuestionById(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new BaseException("질문을 찾을 수 없습니다. ID: " + questionId, ErrorCode.NOT_FOUND));
    }

    @Override
    public QuestionOption getQuestionOptionById(Long optionId) {
        return questionOptionRepository.findById(optionId)
                .orElseThrow(() -> new BaseException("선택지를 찾을 수 없습니다. ID: " + optionId, ErrorCode.NOT_FOUND));
    }
}

