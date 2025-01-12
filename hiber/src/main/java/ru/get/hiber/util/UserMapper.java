package ru.get.hiber.util;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.get.hiber.model.User;
import ru.get.hiber.model.dto.UserDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    User mapToUser(UserDto userDto);

    UserDto mapToUserDto(User user);
}
