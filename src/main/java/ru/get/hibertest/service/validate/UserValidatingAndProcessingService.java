package ru.get.hibertest.service.validate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.get.hibertest.ModelMapper;
import ru.get.hibertest.model.User;
import ru.get.hibertest.model.dto.UserDto;
import ru.get.hibertest.service.buisnes.UserService;
import ru.get.hibertest.validation.AddGroup;

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
