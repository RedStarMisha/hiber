package ru.hiber.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.get.hiber.service.validate.UserValidatingAndProcessingService;
import ru.get.hiber.model.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


// Следует указывать controllers т.к он ограничиев контекст только указанными и остальные контроллеры не создает
@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserValidatingAndProcessingService userService;

    @Test
    void addUserAndReturn200Test() throws Exception {
        UserDto userDto = UserDto.builder().build();

        mockMvc.perform(post("/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());
    }
    @Test
    void addNullBodyAndReturn200Test() throws Exception {

        mockMvc.perform(post("/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new byte[0]))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addUserAndCallServiceMethodTest() throws Exception {
        UserDto userDto = UserDto.builder()
                .username("sda")
                .email("asd@sas.ru")
                .build();

        mockMvc.perform(post("/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());
        // Этот класс-контейнер предназначен для перехватывания параметра. В дальнейшем его можно оттуда достать и проанализировать
        ArgumentCaptor<UserDto> userCaptor = ArgumentCaptor.forClass(UserDto.class);
        Mockito.verify(userService, Mockito.times(1)).addUser(userCaptor.capture());
        assertEquals(userCaptor.getValue(), userDto);
    }
}