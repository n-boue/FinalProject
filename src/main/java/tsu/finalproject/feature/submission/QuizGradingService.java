package tsu.finalproject.feature.submission;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import tsu.finalproject.feature.feed.entity.Quiz;
import tsu.finalproject.feature.feed.entity.QuizQuestion;
import tsu.finalproject.feature.feed.enums.QuestionType;
import tsu.finalproject.feature.submission.dto.QuizAnswerRequest;
import tsu.finalproject.feature.submission.dto.QuizSubmissionRequest;
import tsu.finalproject.feature.submission.entity.QuizAnswer;
import tsu.finalproject.feature.submission.entity.Submission;
import tsu.finalproject.feature.submission.repository.QuizAnswerRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizGradingService {

    private final QuizAnswerRepository quizAnswerRepository;

    @NonNull
    public GradingResult evaluateAndSaveAnswers(@NonNull Quiz quiz, @NonNull Submission submission, @NonNull QuizSubmissionRequest request) {
        Map<Long, QuizQuestion> questionMap = quiz.getQuestions().stream()
                                                      .collect(Collectors.toMap(QuizQuestion::getId, q -> q));

        List<QuizAnswer> answers = new ArrayList<>();
        BigDecimal totalScore = BigDecimal.ZERO;
        boolean requiresManualGrading = false;

        for (QuizAnswerRequest answerReq : request.answers()) {
            QuizQuestion question = questionMap.get(answerReq.questionId());
            Assert.notNull(question, "Question ID " + answerReq.questionId() + " is invalid for this quiz.");

            QuizAnswer answer = QuizAnswer.builder()
                                        .submission(submission)
                                        .question(question)
                                        .providedText(answerReq.providedText())
                                        .selectedOptionIndices(answerReq.selectedOptionIndices() != null ? answerReq.selectedOptionIndices() : new ArrayList<>())
                                        .build();

            if (question.getType() == QuestionType.MULTIPLE_CHOICE || question.getType() == QuestionType.TRUE_FALSE) {
                List<Integer> expected = question.getCorrectOptionIndices();
                List<Integer> provided = answer.getSelectedOptionIndices();

                if (expected != null && provided != null && expected.size() == provided.size() && new HashSet<>(expected).containsAll(provided)) {
                    answer.setAwardedPoints(question.getPoints());
                    totalScore = totalScore.add(BigDecimal.valueOf(question.getPoints()));
                } else {
                    answer.setAwardedPoints(0);
                }
            } else if (question.getType() == QuestionType.SHORT_ANSWER) {
                answer.setAwardedPoints(null);
                requiresManualGrading = true;
            }

            answers.add(answer);
        }

        quizAnswerRepository.saveAll(answers);

        return new GradingResult(answers, totalScore, requiresManualGrading);
    }

    public record GradingResult(List<QuizAnswer> answers, BigDecimal totalScore, boolean requiresManualGrading) {}
}