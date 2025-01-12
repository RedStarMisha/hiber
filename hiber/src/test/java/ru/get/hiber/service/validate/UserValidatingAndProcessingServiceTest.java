package ru.get.hiber.service.validate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.get.hiber.model.User;
import ru.get.hiber.model.dto.UserDto;
import ru.get.hiber.service.buisnes.UserService;
import ru.get.hiber.service.validate.UserValidatingAndProcessingService;
import ru.get.hiber.util.UserMapper;
import ru.get.hiber.validation.AddGroup;


import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserValidatingAndProcessingServiceTest {

    @InjectMocks
    UserValidatingAndProcessingService userValidatingAndProcessingService;

    @Mock
    UserService userService;

    @Mock
    UserMapper userMapper;

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    /*
    Вот так можно инициализировать все поля отмеченные аннотациями Mockito(@Mock, @InjectMocks, @Spy и т.д.) вместо @ExtendWith(MockitoExtension.class)
     */
//    @BeforeEach
//    public void setup() {
//
//        MockitoAnnotations.openMocks(this);
//    }


    @Test
    void callAddUserMethodFromServiceTest() {
        UserDto userDto = UserDto.builder().build();

        Mockito.when(userMapper.mapToUser(any(UserDto.class))).thenReturn(User.builder().build());
        userValidatingAndProcessingService.addUser(userDto);

        verify(userService, times(1)).addUser(any(User.class));
    }
    @Test
    void addValidUserForAddTest() {
        UserDto userDto = UserDto.builder()
                .username("asdd")
                .firstname(null)
                .lastname(null)
                .email("asd@asd.ru")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, AddGroup.class);

        assertTrue(violations.isEmpty());
    }
    @Test
    void addUserWithNullFieldsForAddTest() {
        UserDto userDto = UserDto.builder()
                .username(null)
                .firstname(null)
                .lastname(null)
                .email(null)
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, AddGroup.class);

        assertThat(violations.size()).isEqualTo(4);
        violations.forEach(action -> {
            assertTrue(action.getPropertyPath().toString().equals("username") || action.getPropertyPath().toString().equals("email"));
        });
    }
    @Test
    void addUserWithEmptyFieldsForAddTest() {
        UserDto userDto = UserDto.builder()
                .username("")
                .firstname(null)
                .lastname(null)
                .email("")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, AddGroup.class);

        assertThat(violations.size()).isEqualTo(2);
        violations.forEach(action -> {
            assertTrue(action.getPropertyPath().toString().equals("username") || action.getPropertyPath().toString().equals("email"));
        });
    }
    @Test
    void addUserWithNotCorrectEmailFieldForAddTest() {
        UserDto userDto = UserDto.builder()
                .username("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                .firstname(null)
                .lastname(null)
                .email("asdsa")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, AddGroup.class);

        assertThat(violations.size()).isEqualTo(1);
        violations.forEach(action -> assertEquals(action.getPropertyPath().toString(), "email"));
    }
    @Test
    void addUserWith51LetterUsernameFieldForAddTest() {
        UserDto userDto = UserDto.builder()
                .username("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa1")
                .firstname(null)
                .lastname(null)
                .email("asdsa@asd.ru")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, AddGroup.class);

        assertThat(violations.size()).isEqualTo(1);
        violations.forEach(action -> assertEquals(action.getPropertyPath().toString(), "username"));
    }
}