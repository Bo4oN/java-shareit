package ru.practicum.shareit.requestTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @MockBean
    private ItemRequestServiceImpl service;

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    private ItemRequestDto request;

    private User user = new User(1, "name", "test@ya.ru");

    @BeforeEach
    void setUp() {
        int id = 1;
        String text = "text";
        LocalDateTime createdTime = LocalDateTime.now();
        request = new ItemRequestDto(id, text, createdTime, Collections.emptyList());
    }

    @Test
    void addRequest() throws Exception {
        when(service.addRequest(request, user.getId())).thenReturn(request);

        String responseString = mvc.perform(post("/requests")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(responseString, mapper.writeValueAsString(request));
        verify(service, times(1)).addRequest(request, user.getId());
    }

    @Test
    void getYourRequests() throws Exception {

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk());

        verify(service, times(1)).getUserResponses(user.getId());
    }

    @Test
    void getOtherRequests() throws Exception {

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk());

        verify(service, times(1)).getAllResponses(0, 10, user.getId());
    }

    @Test
    void getRequestById() throws Exception {

        mvc.perform(get("/requests/{requestId}", request.getId())
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk());

        verify(service).getResponseById(request.getId(), user.getId());
    }
}