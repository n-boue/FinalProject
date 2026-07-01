package tsu.finalproject.feature.semester;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.beans.factory.annotation.Autowired;
import tsu.finalproject.feature.semester.dto.SemesterResponse;

import java.time.Clock;
import java.time.LocalDate;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class SemesterMapper {

    @Autowired
    protected Clock clock;

    @Mapping(target = "isActive", expression = "java(isActive(semester))")
    public abstract SemesterResponse toResponse(Semester semester);

    protected boolean isActive(Semester semester) {
        LocalDate today = LocalDate.now(clock);
        return !today.isBefore(semester.getStartDate()) && !today.isAfter(semester.getEndDate());
    }
}