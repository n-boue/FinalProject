package tsu.finalproject.feature.enrollment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import tsu.finalproject.feature.course.entity.CourseSession;
import tsu.finalproject.feature.enrollment.dto.CourseRosterResponse;
import tsu.finalproject.feature.enrollment.dto.EnrollmentResponse;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EnrollmentMapper {

    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseTitle", source = "course.title")
    @Mapping(target = "sessionIds", source = "selectedSessions", qualifiedByName = "mapSessionsToIds")
    EnrollmentResponse toResponse(Enrollment enrollment);

    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentFirstName", source = "student.firstName")
    @Mapping(target = "studentLastName", source = "student.lastName")
    @Mapping(target = "studentEmail", source = "student.email")
    CourseRosterResponse toRosterResponse(Enrollment enrollment);

    @Named("mapSessionsToIds")
    default Set<Long> mapSessionsToIds(Set<CourseSession> sessions) {
        if (sessions == null) return Set.of();
        return sessions.stream()
                       .map(CourseSession::getId)
                       .collect(Collectors.toSet());
    }
}