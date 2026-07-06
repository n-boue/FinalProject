package tsu.finalproject.feature.submission;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.beans.factory.annotation.Autowired;
import tsu.finalproject.feature.storage.Attachment;
import tsu.finalproject.feature.storage.AttachmentManager;
import tsu.finalproject.feature.storage.dto.AttachmentResponse;
import tsu.finalproject.feature.submission.dto.QuizAnswerResponse;
import tsu.finalproject.feature.submission.dto.SubmissionResponse;
import tsu.finalproject.feature.submission.entity.QuizAnswer;
import tsu.finalproject.feature.submission.entity.Submission;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class SubmissionMapper {

    @Autowired
    protected AttachmentManager attachmentManager;

    @Mapping(target = "studentId", source = "submission.student.id")
    @Mapping(target = "postId", source = "submission.post.id")
    public abstract SubmissionResponse toResponse(Submission submission);

    @Mapping(target = "studentId", source = "submission.student.id")
    @Mapping(target = "postId", source = "submission.post.id")
    @Mapping(target = "quizAnswers", source = "answers")
    public abstract SubmissionResponse toResponse(Submission submission, List<QuizAnswer> answers);

    @Mapping(target = "questionId", source = "question.id")
    public abstract QuizAnswerResponse toQuizAnswerResponse(QuizAnswer quizAnswer);

    protected AttachmentResponse toAttachmentResponse(Attachment attachment) {
        if (attachment == null) {
            return null;
        }
        return new AttachmentResponse(
                attachment.getId(),
                attachment.getOriginalFileName(),
                attachmentManager.getAttachmentUrl(attachment),
                attachment.getSizeBytes()
        );
    }
}