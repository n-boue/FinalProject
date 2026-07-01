package tsu.finalproject.feature.user;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import tsu.finalproject.feature.user.dto.AdminCreateUserRequest;
import tsu.finalproject.feature.user.entity.Admin;
import tsu.finalproject.feature.user.entity.Professor;
import tsu.finalproject.feature.user.entity.Student;
import tsu.finalproject.feature.user.entity.User;

@Component
public class UserFactory {

    @NonNull
    public User createUserEntity(@NonNull AdminCreateUserRequest request, @NonNull String encodedPassword) {
        return switch (request.role()) {
            case ROLE_STUDENT -> Student.builder()
                                         .firstName(request.firstName())
                                         .lastName(request.lastName())
                                         .email(request.email())
                                         .passwordHash(encodedPassword)
                                         .universityId(request.universityId())
                                         .role(request.role())
                                         .deactivated(false)
                                         .build();
            case ROLE_PROF -> Professor.builder()
                                      .firstName(request.firstName())
                                      .lastName(request.lastName())
                                      .email(request.email())
                                      .passwordHash(encodedPassword)
                                      .universityId(request.universityId())
                                      .role(request.role())
                                      .deactivated(false)
                                      .build();
            case ROLE_ADMIN -> Admin.builder()
                                       .firstName(request.firstName())
                                       .lastName(request.lastName())
                                       .email(request.email())
                                       .passwordHash(encodedPassword)
                                       .universityId(request.universityId())
                                       .role(request.role())
                                       .deactivated(false)
                                       .build();
        };
    }
}