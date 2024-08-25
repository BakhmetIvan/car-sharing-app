package mate.capsharingapp.security;

import lombok.RequiredArgsConstructor;
import mate.capsharingapp.dto.user.UserLoginRequestDto;
import mate.capsharingapp.dto.user.UserLoginResponseDto;
import mate.capsharingapp.exception.EntityNotFoundException;
import mate.capsharingapp.exception.LoginException;
import mate.capsharingapp.messages.ExceptionMessages;
import mate.capsharingapp.model.Role;
import mate.capsharingapp.model.User;
import mate.capsharingapp.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public UserLoginResponseDto authenticate(UserLoginRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail()).orElseThrow(
                () -> new EntityNotFoundException(
                        ExceptionMessages.USER_DO_NOT_REGISTERED_EXCEPTION)
        );
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestDto.getEmail(),
                            requestDto.getPassword()));
        } catch (BadCredentialsException e) {
            throw new LoginException(ExceptionMessages.INVALID_PASSWORD_EXCEPTION, e);
        } catch (AuthenticationException e) {
            throw new LoginException(ExceptionMessages.AUTHENTICATION_FAILURE_EXCEPTION, e);
        }
        String jwt = jwtUtil.generateToken(user);
        return new UserLoginResponseDto(jwt);
    }

    public boolean telegramAdminsAuthenticate(String email, String password, Long chatId)
            throws AuthenticationException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        User user = (User) authentication.getPrincipal();
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName() == Role.RoleName.ROLE_MANAGER);
        if (isAdmin) {
            user.setTgChatId(chatId);
            userRepository.save(user);
        }
        return isAdmin;
    }
}
