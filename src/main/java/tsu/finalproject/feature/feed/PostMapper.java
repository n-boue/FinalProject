package tsu.finalproject.feature.feed;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.beans.factory.annotation.Autowired;
import tsu.finalproject.feature.feed.dto.PostResponse;
import tsu.finalproject.feature.feed.dto.QuizQuestionRequest;
import tsu.finalproject.feature.feed.entity.Post;
import tsu.finalproject.feature.feed.entity.QuizQuestion;
import tsu.finalproject.feature.storage.Attachment;
import tsu.finalproject.feature.storage.AttachmentManager;
import tsu.finalproject.feature.storage.dto.AttachmentResponse;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class PostMapper {

    @Autowired
    protected AttachmentManager attachmentManager;

    @Mapping(target = "authorName", expression = "java(post.getAuthor().getFirstName() + ' ' + post.getAuthor().getLastName())")
    public abstract PostResponse toResponse(Post post);

    public abstract QuizQuestion toEntity(QuizQuestionRequest request);

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