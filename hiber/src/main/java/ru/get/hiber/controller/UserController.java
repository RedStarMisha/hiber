package ru.get.hiber.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.get.hiber.service.validate.UserValidatingAndProcessingService;
import ru.get.hiber.model.dto.UserDto;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/user")
public class UserController {
    private final UserValidatingAndProcessingService processUserService;
    @PostMapping("/add")
    public UserDto addUser(@RequestBody UserDto userDto) {
        return processUserService.addUser(userDto);
    }
}
