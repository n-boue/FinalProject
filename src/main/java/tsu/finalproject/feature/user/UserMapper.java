package tsu.finalproject.feature.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.beans.factory.annotation.Autowired;
import tsu.finalproject.feature.storage.FileStorageService;
import tsu.finalproject.feature.user.dto.UserResponse;
import tsu.finalproject.feature.user.entity.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class UserMapper {

    @Autowired
    protected FileStorageService fileStorageService;

    @Mapping(target = "profilePictureUrl", expression = "java(generateProfilePictureUrl(user))")
    public abstract UserResponse toResponse(User user);

    protected String generateProfilePictureUrl(User user) {
        if (user.getProfilePictureKey() == null) {
            return null;
        }
        return fileStorageService.getFileUrl(user.getProfilePictureKey());
    }
}