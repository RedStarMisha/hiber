package ru.get.hibertest;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.get.hibertest.model.User;
import ru.get.hibertest.model.dto.UserDto;
import ru.get.hibertest.validation.AddGroup;

@Service
public class ValidatingAndProcessingUserService {
    public UserDto addUser(@Validated(AddGroup.class) UserDto userDto) {
        User user = ModelMapper.mapToDto(userDto);
        return null;
    }
}
