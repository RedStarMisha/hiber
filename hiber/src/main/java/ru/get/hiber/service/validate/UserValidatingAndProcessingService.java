package ru.get.hiber.service.validate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.get.hiber.model.User;
import ru.get.hiber.model.dto.UserDto;
import ru.get.hiber.service.buisnes.UserService;
import ru.get.hiber.util.UserMapper;
import ru.get.hiber.validation.AddGroup;

@Service
@RequiredArgsConstructor
public class UserValidatingAndProcessingService {
    private final UserService userService;
    private final UserMapper userMapper;

    public UserDto addUser(@Validated(AddGroup.class) UserDto userDto) {
        User user = userMapper.mapToUser(userDto);
        user = userService.addUser(user);
        return userMapper.mapToUserDto(user);
    }
}
