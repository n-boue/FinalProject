package tsu.finalproject.feature.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tsu.finalproject.feature.user.dto.*;
import tsu.finalproject.feature.user.enums.Role;
import tsu.finalproject.security.jwt.TokenBlacklistService;

import java.security.Principal;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final TokenBlacklistService tokenBlacklistService;

    @GetMapping("/me")
    @NonNull
    public UserResponse getMyProfile(Principal principal) {
        return userService.getUserByEmail(principal.getName());
    }

    @PutMapping("/me")
    @NonNull
    public UserResponse updateMyProfile(
            @RequestBody @Valid UpdateUserProfileRequest request,
            Principal principal
    ) {
        return userService.updateUserByEmail(principal.getName(), request);
    }

    @PostMapping(value = "/me/profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @NonNull
    public UserResponse uploadMyProfilePicture(
            @RequestParam("file") @NonNull MultipartFile file,
            Principal principal
    ) {
        return userService.uploadProfilePicture(principal.getName(), file);
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @NonNull
    public UserResponse createUserByAdmin(
            @RequestBody @Valid AdminCreateUserRequest request
    ) {
        return userService.createUser(request);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @NonNull
    public Page<UserResponse> getAllUsers(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) String search,
            Pageable pageable
    ) {
        return userService.getAllUsers(role, search, pageable);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @NonNull
    public UserResponse updateUserStatus(
            @PathVariable @NonNull Long id,
            @RequestBody @Valid UserStatusRequest request
    ) {
        return userService.updateUserStatus(id, request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @NonNull
    public UserResponse getUserById(@PathVariable @NonNull Long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @NonNull
    public UserResponse updateUserByAdmin(
            @PathVariable @NonNull Long id,
            @RequestBody @Valid UpdateUserProfileRequest request
    ) {
        return userService.updateUserById(id, request);
    }

    @PutMapping("/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeMyPassword(
            HttpServletRequest servletRequest,
            @RequestBody @Valid ChangePasswordRequest request,
            Principal principal
    ) {
        userService.changePassword(principal.getName(), request);

        final String authHeader = servletRequest.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            tokenBlacklistService.blacklistToken(jwt);
        }
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetUserPassword(
            @PathVariable @NonNull Long id,
            @RequestBody @Valid AdminResetPasswordRequest request
    ) {
        userService.resetPasswordByAdmin(id, request);
    }
}

