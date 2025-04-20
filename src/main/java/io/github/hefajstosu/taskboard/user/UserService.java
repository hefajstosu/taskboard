package io.github.hefajstosu.taskboard.user;

import io.github.hefajstosu.taskboard.common.BusinessException;
import io.github.hefajstosu.taskboard.common.NotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service; 
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    } 

    public User register(String email, String displayName, String rawPassword) {
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmailIgnoreCase(normalized)) {
            throw new BusinessException("Email already registered"); 
		} 
        return userRepository.save(new User(normalized, displayName, passwordEncoder.encode(rawPassword)));
    } 

    @Transactional(readOnly = true)
    public Optional<User> authenticate(String email, String rawPassword) {
        return userRepository.findByEmailIgnoreCase(email.trim())
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPasswordHash()));
    } 

    @Transactional(readOnly = true)
    public User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User", id));
    } 
} 
