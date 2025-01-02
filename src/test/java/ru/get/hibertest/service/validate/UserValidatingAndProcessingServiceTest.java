package ru.get.hibertest.service.validate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import ru.get.hibertest.ModelMapper;
import ru.get.hibertest.model.User;
import ru.get.hibertest.model.dto.UserDto;
import ru.get.hibertest.service.buisnes.UserService;
import ru.get.hibertest.service.validate.UserValidatingAndProcessingService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserValidatingAndProcessingServiceTest {

    @InjectMocks
    UserValidatingAndProcessingService userValidatingAndProcessingService;

    @Mock
    UserService userService;

    /*
    Вот так можно инициализировать все поля отмеченные аннотациями Mockito(@Mock, @InjectMocks, @Spy и т.д.) вместо @ExtendWith(MockitoExtension.class)
     */
//    @BeforeEach
//    public void setup() {
//
//        MockitoAnnotations.openMocks(this);
//    }


    @Test
    void addUser() {
        UserDto userDto = UserDto.builder().build();

        userValidatingAndProcessingService.addUser(userDto);

        verify(userService, times(1)).addUser(any(User.class));
    }
}