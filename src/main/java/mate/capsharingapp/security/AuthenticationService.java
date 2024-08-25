package mate.capsharingapp.security;

import lombok.RequiredArgsConstructor;
import mate.capsharingapp.dto.user.UserLoginRequestDto;
import mate.capsharingapp.dto.user.UserLoginResponseDto;
import mate.capsharingapp.messages.ExceptionMessages;
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
                        String.format(ExceptionMessages.NOT_FOUND_USER_BY_EMAIL_EXCEPTION,
                                requestDto.getEmail()))
        );
        String jwt = jwtUtil.generateToken(user);
        return new UserLoginResponseDto(jwt);
    }
}
