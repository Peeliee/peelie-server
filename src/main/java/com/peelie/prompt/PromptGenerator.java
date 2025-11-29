package com.peelie.prompt;

import org.springframework.stereotype.Component;

@Component
public class PromptGenerator {

    public String generatePrompt(UserAnswer userAnswer) {

        StringBuilder prompt = new StringBuilder();

        prompt.append("\n\n=== 사용자 프로필 ===\n\n");
        prompt.append("User ID: ").append(userAnswer.getUserId()).append("\n");
        prompt.append("사용자 이름: ").append(userAnswer.getUserName()).append("\n\n");
        prompt.append("다음은 사용자가 온보딩 과정에서 선택한 관심사와 답변입니다.\n\n");
        prompt.append("사용자를 언급할 때는 '").append(userAnswer.getUserName()).append("' 이름을 사용해주세요.\n\n");

        // 각 카테고리별 정보 추가
        for (int i = 0; i < userAnswer.getAnswers().size(); i++) {
            UserAnswer.SubCategoryAnswer subCategoryAnswer = userAnswer.getAnswers().get(i);

            prompt.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            prompt.append("관심사 #").append(i + 1).append(": ")
                    .append(subCategoryAnswer.getCategoryName())
                    .append(" > ")
                    .append(subCategoryAnswer.getSubCategoryName())
                    .append("\n");
            prompt.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");

            // L0 정보 추가
            prompt.append("  [L0] ").append(subCategoryAnswer.getCategoryQuestion()).append("\n");
            prompt.append("  ➤ ").append(subCategoryAnswer.getSubCategoryName()).append("\n\n");

            // 각 질문과 답변 추가 (L1~L4)
            for (UserAnswer.QuestionAnswer qa : subCategoryAnswer.getQuestionAnswers()) {
                prompt.append("  [").append(qa.getLevel()).append("] ");
                prompt.append(qa.getQuestion()).append("\n");
                prompt.append("  ➤ ").append(qa.getAnswer()).append("\n\n");
            }
        }

        prompt.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        return prompt.toString();
    }
}