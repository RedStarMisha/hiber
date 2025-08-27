package ru.get.hiber.service.buisnes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.get.hiber.model.User;
import ru.get.hiber.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    protected String a;
    private final UserRepository userRepository;
    public User addUser(User user)  {
        User savedUser = userRepository.save(user);
        log.info("Add new user {}", savedUser);
        return savedUser;
    }
}
