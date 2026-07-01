package tsu.finalproject.feature.feed;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.beans.factory.annotation.Autowired;
import tsu.finalproject.feature.feed.dto.CommentResponse;
import tsu.finalproject.feature.feed.entity.Comment;
import tsu.finalproject.feature.storage.FileStorageService;
import tsu.finalproject.feature.user.entity.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class CommentMapper {

    @Autowired
    protected FileStorageService fileStorageService;

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorName", expression = "java(comment.getAuthor().getFirstName() + ' ' + comment.getAuthor().getLastName())")
    @Mapping(target = "authorProfilePictureUrl", expression = "java(generateProfilePictureUrl(comment.getAuthor()))")
    public abstract CommentResponse toResponse(Comment comment);

    protected String generateProfilePictureUrl(User user) {
        if (user.getProfilePictureKey() == null) {
            return null;
        }
        return fileStorageService.getFileUrl(user.getProfilePictureKey());
    }
}