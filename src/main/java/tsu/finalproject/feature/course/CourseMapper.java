package tsu.finalproject.feature.course;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import tsu.finalproject.feature.course.dto.*;
import tsu.finalproject.feature.course.entity.Course;
import tsu.finalproject.feature.course.entity.CourseSection;
import tsu.finalproject.feature.course.entity.CourseSession;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CourseMapper {

    @Mapping(target = "semesterId", source = "semester.id")
    @Mapping(target = "semesterName", source = "semester.name")
    @Mapping(target = "professorId", source = "leadProfessor.id")
    @Mapping(target = "professorName", expression = "java(course.getLeadProfessor().getFirstName() + ' ' + course.getLeadProfessor().getLastName())")
    CourseResponse toResponse(Course course);

    @Mapping(target = "semesterId", source = "semester.id")
    @Mapping(target = "semesterName", source = "semester.name")
    @Mapping(target = "professorId", source = "leadProfessor.id")
    @Mapping(target = "professorName", expression = "java(course.getLeadProfessor().getFirstName() + ' ' + course.getLeadProfessor().getLastName())")
    CourseDetailsResponse toDetailsResponse(Course course);

    @Mapping(target = "courseId", source = "course.id")
    CourseSectionResponse toSectionResponse(CourseSection section);

    CourseSectionDetails toSectionDetails(CourseSection section);

    @Mapping(target = "sectionId", source = "section.id")
    @Mapping(target = "professorId", source = "professor.id")
    @Mapping(target = "professorName", expression = "java(session.getProfessor().getFirstName() + ' ' + session.getProfessor().getLastName())")
    CourseSessionResponse toSessionResponse(CourseSession session);
}