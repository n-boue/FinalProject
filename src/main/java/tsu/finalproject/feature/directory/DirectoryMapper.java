package tsu.finalproject.feature.directory;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.beans.factory.annotation.Autowired;
import tsu.finalproject.feature.directory.dto.ProfessorDirectoryResponse;
import tsu.finalproject.feature.storage.FileStorageService;
import tsu.finalproject.feature.user.entity.Professor;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class DirectoryMapper {

    @Autowired
    protected FileStorageService fileStorageService;

    @Mapping(target = "profilePictureUrl", expression = "java(generateProfilePictureUrl(professor))")
    public abstract ProfessorDirectoryResponse toProfessorResponse(Professor professor);

    protected String generateProfilePictureUrl(Professor professor) {
        if (professor.getProfilePictureKey() == null) {
            return null;
        }
        return fileStorageService.getFileUrl(professor.getProfilePictureKey());
    }
}