package tsu.finalproject.feature.feed;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import tsu.finalproject.feature.feed.dto.PostResponse;
import tsu.finalproject.feature.feed.dto.QuizQuestionRequest;
import tsu.finalproject.feature.feed.entity.Post;
import tsu.finalproject.feature.feed.entity.QuizQuestion;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PostMapper {

    @Mapping(target = "authorName", expression = "java(post.getAuthor().getFirstName() + ' ' + post.getAuthor().getLastName())")
    PostResponse toResponse(Post post);

    QuizQuestion toEntity(QuizQuestionRequest request);
}