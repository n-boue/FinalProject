package tsu.finalproject.feature.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import tsu.finalproject.feature.auth.dto.*;
import tsu.finalproject.feature.user.UserRepository;
import tsu.finalproject.feature.user.entity.Student;
import tsu.finalproject.feature.user.entity.User;
import tsu.finalproject.feature.user.enums.Role;
import tsu.finalproject.security.jwt.JwtService;
import tsu.finalproject.security.jwt.TokenBlacklistService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;

    @Value("${university.security.allowed-student-domains}")
    private List<String> allowedStudentDomains;

    public AuthenticationResponse register(RegisterRequest request) {
        boolean isDomainValid = allowedStudentDomains.stream()
                                        .anyMatch(domain -> request.email().toLowerCase().endsWith(domain));
        Assert.isTrue(isDomainValid, "Student registration requires a valid university email domain.");
        Assert.isTrue(userRepository.findByEmail(request.email()).isEmpty(), "User with this email already exists.");

        var student = Student.builder()
                              .firstName(request.firstName())
                              .lastName(request.lastName())
                              .email(request.email())
                              .passwordHash(passwordEncoder.encode(request.password()))
                              .universityId(request.universityId())
                              .role(Role.ROLE_STUDENT)
                              .deactivated(false)
                              .build();
        userRepository.save(student);

        var jwtToken = jwtService.generateToken(student);
        var refreshToken = refreshTokenService.createRefreshToken(student.getEmail());

        return new AuthenticationResponse(jwtToken, refreshToken);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        var user = userRepository.findByEmail(request.email()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        return new AuthenticationResponse(jwtToken, refreshToken);
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        String userEmail = refreshTokenService.validateAndRotateToken(request.refreshToken());

        User user = userRepository.findByEmail(userEmail)
                            .orElseThrow(() -> new IllegalArgumentException("User associated with token not found"));

        if (user.isDeactivated()) {
            throw new IllegalArgumentException("User account is deactivated");
        }

        String newJwt = jwtService.generateToken(user);
        String newRefreshToken = refreshTokenService.createRefreshToken(userEmail);

        return new AuthenticationResponse(newJwt, newRefreshToken);
    }

    public void logout(String jwt, LogoutRequest request) {
        tokenBlacklistService.blacklistToken(jwt);

        refreshTokenService.revokeToken(request.refreshToken());
    }
}