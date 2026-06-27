package tsu.finalproject.entity.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "students")
@DiscriminatorValue("ROLE_STUDENT")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Student extends User {

    private String yearOfStudy;

    private String address;

    private String faculty;

    private String program;

}
