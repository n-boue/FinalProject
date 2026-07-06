package tsu.finalproject.feature.feed;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.beans.factory.annotation.Autowired;
import tsu.finalproject.feature.feed.dto.PostResponse;
import tsu.finalproject.feature.feed.dto.QuizQuestionRequest;
import tsu.finalproject.feature.feed.dto.QuizQuestionResponse;
import tsu.finalproject.feature.feed.entity.Assignment;
import tsu.finalproject.feature.feed.entity.Post;
import tsu.finalproject.feature.feed.entity.Quiz;
import tsu.finalproject.feature.feed.entity.QuizQuestion;
import tsu.finalproject.feature.storage.Attachment;
import tsu.finalproject.feature.storage.AttachmentManager;
import tsu.finalproject.feature.storage.dto.AttachmentResponse;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class PostMapper {

    @Autowired
    protected AttachmentManager attachmentManager;

    public abstract QuizQuestion toEntity(QuizQuestionRequest request);

    public PostResponse toResponse(Post post) {
        if (post == null) {
            return null;
        }

        String authorName = post.getAuthor().getFirstName() + " " + post.getAuthor().getLastName();
        var attachments = post.getAttachments().stream().map(this::toAttachmentResponse).toList();

        if (post instanceof Assignment a) {
            return new PostResponse(
                    a.getId(), a.getType(), a.getTitle(), a.getBody(), a.getWeek(),
                    a.getVisibility(), authorName, a.getCreatedAt(), attachments,
                    a.getDueDate(), a.getMaxPoints(), null, null, null
            );
        } else if (post instanceof Quiz q) {
            var questions = q.getQuestions().stream().map(this::toQuizQuestionResponse).toList();
            return new PostResponse(
                    q.getId(), q.getType(), q.getTitle(), q.getBody(), q.getWeek(),
                    q.getVisibility(), authorName, q.getCreatedAt(), attachments,
                    q.getDueDate(), null, q.getTimeLimitMinutes(), q.getPassingScore(), questions
            );
        }

        return new PostResponse(
                post.getId(), post.getType(), post.getTitle(), post.getBody(), post.getWeek(),
                post.getVisibility(), authorName, post.getCreatedAt(), attachments,
                null, null, null, null, null
        );
    }

    protected AttachmentResponse toAttachmentResponse(Attachment attachment) {
        if (attachment == null) return null;
        return new AttachmentResponse(
                attachment.getId(),
                attachment.getOriginalFileName(),
                attachmentManager.getAttachmentUrl(attachment),
                attachment.getSizeBytes()
        );
    }

    protected QuizQuestionResponse toQuizQuestionResponse(QuizQuestion question) {
        if (question == null) return null;
        return new QuizQuestionResponse(
                question.getId(),
                question.getType(),
                question.getText(),
                question.getPoints(),
                question.getOptions(),
                question.getCorrectOptionIndices()
        );
    }
}