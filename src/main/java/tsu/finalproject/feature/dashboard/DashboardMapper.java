package tsu.finalproject.feature.dashboard;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import tsu.finalproject.feature.course.entity.CourseSession;
import tsu.finalproject.feature.dashboard.dto.ScheduleEventResponse;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DashboardMapper {

    @Mapping(target = "courseId", source = "section.course.id")
    @Mapping(target = "courseTitle", source = "section.course.title")
    @Mapping(target = "eventTitle", source = "name")
    @Mapping(target = "eventType", expression = "java(tsu.finalproject.feature.dashboard.enums.ScheduleEventType.valueOf(session.getSection().getType().name()))")
    @Mapping(target = "exactDateTime", ignore = true)
    ScheduleEventResponse toScheduleEvent(CourseSession session);
}