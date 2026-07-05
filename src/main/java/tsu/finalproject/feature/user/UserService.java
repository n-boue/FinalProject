package tsu.finalproject.feature.user;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import tsu.finalproject.common.manager.DomainLookupService;
import tsu.finalproject.feature.storage.FileStorageService;
import tsu.finalproject.feature.storage.event.FileDeletionEvent;
import tsu.finalproject.feature.user.dto.*;
import tsu.finalproject.feature.user.entity.Professor;
import tsu.finalproject.feature.user.entity.Student;
import tsu.finalproject.feature.user.entity.User;
import tsu.finalproject.feature.user.enums.Role;
import tsu.finalproject.security.jwt.TokenBlacklistService;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final DomainLookupService domainLookupService;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;
    private final UserFactory userFactory;
    private final UserMapper userMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final TokenBlacklistService tokenBlacklistService;

    @Transactional
    @NonNull
    public UserResponse createUser(@NonNull AdminCreateUserRequest request) {
        Assert.isTrue(userRepository.findByEmail(request.email()).isEmpty(),
                "User with this email already exists.");

        String encodedPassword = passwordEncoder.encode(request.password());
        Assert.notNull(encodedPassword, "Password encoding failed; encoded password cannot be null");

        User userToSave = userFactory.createUserEntity(request, encodedPassword);

        return userMapper.toResponse(userRepository.save(userToSave));
    }

    @Transactional(readOnly = true)
    @NonNull
    public UserResponse getUserById(@NonNull Long id) {
        return userMapper.toResponse(domainLookupService.getUser(id));
    }

    @Transactional(readOnly = true)
    @NonNull
    public Page<UserResponse> getAllUsers(Role role, String search, @NonNull Pageable pageable) {
        String notNullSearch = search == null ? "" : search;
        return userRepository.findWithFilters(role, notNullSearch, pageable)
                       .map(userMapper::toResponse);
    }

    @Transactional(readOnly = true)
    @NonNull
    public UserResponse getUserByEmail(@NonNull String email) {
        return userMapper.toResponse(domainLookupService.getUser(email));
    }

    @Transactional
    @NonNull
    public UserResponse updateUserById(@NonNull Long id, @NonNull UpdateUserProfileRequest request) {
        User user = domainLookupService.getUser(id);
        return applyUpdates(user, request);
    }

    @Transactional
    @NonNull
    public UserResponse updateUserByEmail(@NonNull String email, @NonNull UpdateUserProfileRequest request) {
        User user = domainLookupService.getUser(email);
        return applyUpdates(user, request);
    }

    @Transactional
    @NonNull
    public UserResponse uploadProfilePicture(@NonNull String email, @NonNull MultipartFile file) {
        User user = domainLookupService.getUser(email);

        if (user.getProfilePictureKey() != null) {
            eventPublisher.publishEvent(new FileDeletionEvent(user.getProfilePictureKey()));
        }

        String fileKey = fileStorageService.uploadFile(file, "profile-pictures");
        user.setProfilePictureKey(fileKey);

        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    @NonNull
    public UserResponse updateUserStatus(@NonNull Long id, @NonNull UserStatusRequest request) {
        User user = domainLookupService.getUser(id);

        if (user.isDeactivated() == request.deactivated()) {
            return userMapper.toResponse(user); // No-op if status matches
        }

        user.setDeactivated(request.deactivated());

        if (request.deactivated()) {
            tokenBlacklistService.blacklistUser(user.getEmail());
        }

        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public void changePassword(@NonNull String email, @NonNull ChangePasswordRequest request) {
        User user = domainLookupService.getUser(email);

        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Incorrect current password.");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void resetPasswordByAdmin(@NonNull Long userId, @NonNull AdminResetPasswordRequest request) {
        User user = domainLookupService.getUser(userId);

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

    }

    private UserResponse applyUpdates(User user, UpdateUserProfileRequest request) {
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        if (request.phone() != null) user.setPhone(request.phone());

        if (user instanceof Student student) {
            if (request.address() != null) student.setAddress(request.address());
            if (request.faculty() != null) student.setFaculty(request.faculty());
            if (request.program() != null) student.setProgram(request.program());
            if (request.yearOfStudy() != null) student.setYearOfStudy(request.yearOfStudy());
        } else if (user instanceof Professor professor) {
            if (request.department() != null) professor.setDepartment(request.department());
            if (request.office() != null) professor.setOffice(request.office());
            if (request.title() != null) professor.setTitle(request.title());
        }

        return userMapper.toResponse(userRepository.save(user));
    }
}