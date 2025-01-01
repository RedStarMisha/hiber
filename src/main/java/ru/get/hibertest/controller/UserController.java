package ru.get.hibertest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.get.hibertest.PrepareAndProcessUserService;
import ru.get.hibertest.model.dto.UserDto;

@RestController
@RequiredArgsConstructor
@RequestMapping(name = "/user")
public class UserController {
    private final PrepareAndProcessUserService processUserService;
    @PostMapping("/add")
    public UserDto addUser(@RequestBody UserDto userDto) {
        return processUserService.addUser(userDto);
    }
}
