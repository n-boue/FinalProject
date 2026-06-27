package tsu.finalproject.entity.feed;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("ASSIGNMENT")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Assignment extends Post {

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Min(0)
    private Integer maxPoints;
}