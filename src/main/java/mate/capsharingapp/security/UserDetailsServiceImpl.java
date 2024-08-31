package mate.capsharingapp.security;

import lombok.RequiredArgsConstructor;
import mate.capsharingapp.messages.ExceptionMessages;
import mate.capsharingapp.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException(
                        String.format(ExceptionMessages.NOT_FOUND_USER_BY_EMAIL_EXCEPTION, email)
                )
        );
    }
}
