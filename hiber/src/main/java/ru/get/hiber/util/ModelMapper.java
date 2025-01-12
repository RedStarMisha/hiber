package ru.get.hiber.util;

import lombok.experimental.UtilityClass;
import ru.get.hiber.model.Item;
import ru.get.hiber.model.User;
import ru.get.hiber.model.dto.ItemDto;
import ru.get.hiber.model.dto.UserDto;

@UtilityClass
public class ModelMapper {
    public Item mapToEntity(ItemDto itemDto) {
        return null;
    }

    public ItemDto mapToDto(Item item) {
        return null;
    }

    public User mapToDto(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .build();
    }

    public UserDto mapToDto(User user) {
        return null;
    }
}
