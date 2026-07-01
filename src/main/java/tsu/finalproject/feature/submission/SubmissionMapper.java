package tsu.finalproject.feature.submission;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import tsu.finalproject.feature.submission.dto.QuizAnswerResponse;
import tsu.finalproject.feature.submission.dto.SubmissionResponse;
import tsu.finalproject.feature.submission.entity.QuizAnswer;
import tsu.finalproject.feature.submission.entity.Submission;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SubmissionMapper {

    @Mapping(target = "studentId", source = "submission.student.id")
    @Mapping(target = "postId", source = "submission.post.id")
    SubmissionResponse toResponse(Submission submission);

    @Mapping(target = "studentId", source = "submission.student.id")
    @Mapping(target = "postId", source = "submission.post.id")
    @Mapping(target = "quizAnswers", source = "answers")
    SubmissionResponse toResponse(Submission submission, List<QuizAnswer> answers);

    @Mapping(target = "questionId", source = "question.id")
    QuizAnswerResponse toQuizAnswerResponse(QuizAnswer quizAnswer);
}