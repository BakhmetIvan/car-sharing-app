package mate.capsharingapp.security;

import lombok.RequiredArgsConstructor;
import mate.capsharingapp.dto.user.UserLoginRequestDto;
import mate.capsharingapp.dto.user.UserLoginResponseDto;
import mate.capsharingapp.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private static final String USER_NOT_FOUND_EXCEPTION = "Can't find user by email: %s";
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public UserLoginResponseDto authenticate(UserLoginRequestDto requestDto) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDto.getEmail(), requestDto.getPassword())
        );
        UserDetails user = userRepository.findByEmail(authentication.getName()).orElseThrow(
                () -> new UsernameNotFoundException(
                        String.format(USER_NOT_FOUND_EXCEPTION, requestDto.getEmail()))
        );
        String jwt = jwtUtil.generateToken(user);
        return new UserLoginResponseDto(jwt);
    }
}
