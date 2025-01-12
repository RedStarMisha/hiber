package ru.get.hiber.service.validate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.get.hiber.util.ModelMapper;
import ru.get.hiber.model.User;
import ru.get.hiber.model.dto.UserDto;
import ru.get.hiber.service.buisnes.UserService;
import ru.get.hiber.validation.AddGroup;

@Service
@RequiredArgsConstructor
public class UserValidatingAndProcessingService {
    private final UserService userService;

    public UserDto addUser(@Validated(AddGroup.class) UserDto userDto) {
        User user = ModelMapper.mapToDto(userDto);
        user = userService.addUser(user);
        return ModelMapper.mapToDto(user);
    }
}
