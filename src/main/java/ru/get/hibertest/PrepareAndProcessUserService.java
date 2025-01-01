package ru.get.hibertest;

import org.springframework.stereotype.Service;
import ru.get.hibertest.model.User;
import ru.get.hibertest.model.dto.UserDto;

@Service
public class PrepareAndProcessUserService {
    public UserDto addUser(UserDto userDto) {
        User user = ModelMapper.mapToDto(userDto);
        return null;
    }
}
