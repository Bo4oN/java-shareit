package ru.practicum.shareit.userTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @MockBean
    private UserServiceImpl service;

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    private UserDto user;

    @BeforeEach
    public void setUp() {
        int id = 1;
        String name = "testName";
        String mail = "test@yandex.ru";
        user = new UserDto(id, name, mail);
    }

    @Test
    void addUser() throws Exception {
        when(service.addUser(user)).thenReturn(user);

        String responseString = mvc.perform(post("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(responseString, mapper.writeValueAsString(user));
        verify(service, times(1)).addUser(user);
    }

    @Test
    void updateUser() throws Exception {
        UserDto updateUser = new UserDto(1, "updateName", "update@yandex.ru");
        when(service.updateUser(user.getId(), updateUser)).thenReturn(updateUser);

        String responseString = mvc.perform(patch("/users/{userId}", user.getId())
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(updateUser)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(responseString, mapper.writeValueAsString(updateUser));
        verify(service, times(1)).updateUser(1, updateUser);
    }

    @Test
    void getUser() throws Exception {

        mvc.perform(get("/users/{userId}", user.getId()))
                .andDo(print())
                .andExpect(status().isOk());

        verify(service).getUser(user.getId());
    }

    @Test
    void getAllUsers() throws  Exception {
        List<UserDto> list = List.of(user);

        when(service.getAllUsers()).thenReturn(list);

        String responseString = mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(responseString, mapper.writeValueAsString(list));
    }

    @Test
    void deleteUser() throws Exception {
        when(service.deleteUser(user.getId())).thenReturn(user);

        mvc.perform(delete("/users/{userId}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) user.getId())))
                .andExpect(jsonPath("$.name", is("testName")))
                .andExpect(jsonPath("$.email", is("test@yandex.ru")));
    }
}
