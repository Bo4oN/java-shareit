package ru.practicum.shareit.itemTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @MockBean
    ItemServiceImpl service;

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    private ItemDto item;
    private User user = new User(1, "name", "test@ya.ru");
    private Comment comment = new Comment();

    @BeforeEach
    public void setUp() {
        int id = 1;
        String name = "testName";
        String description = "testDescription";
        item = new ItemDto(id, name, description, true);
    }

    @Test
    void addItem() throws Exception {
        when(service.addItem(item, user.getId())).thenReturn(item);

        String responseString = mvc.perform(post("/items")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(responseString, mapper.writeValueAsString(item));
        verify(service, times(1)).addItem(item, user.getId());
    }

    @Test
    void updateItem() throws Exception {
        ItemDto updatingItem = new ItemDto(1, "updateName", "updateDescription", false);
        when(service.updateItem(this.item.getId(), updatingItem, user.getId())).thenReturn(updatingItem);

        String responseString = mvc.perform(patch("/items/{itemId}", this.item.getId())
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(updatingItem)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(responseString, mapper.writeValueAsString(updatingItem));
        verify(service, times(1)).updateItem(item.getId(), updatingItem ,user.getId());
    }

    @Test
    void getItem() throws Exception {

        mvc.perform(get("/items/{itemId}", item.getId())
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk());

        verify(service).getItem(item.getId(), user.getId());
    }

    @Test
    void getUserItems() throws Exception {

        mvc.perform(get("/items")
                .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk());

        verify(service).getItemsListByOwner(user.getId(), 0, 10);
    }

    @Test
    void searchItems() throws Exception {

        mvc.perform(get("/items/search")
                .param("text", "text"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void addComment() throws Exception {
        comment.setId(1);
        comment.setText("text");
        comment.setAuthor(user);
        comment.setItem(ItemMapper.toItem(item, user));
        comment.setCreatedDate(LocalDateTime.now());

        CommentDto commentDto = CommentMapper.toCommentDto(comment);

        when(service.addComment(item.getId(), comment, user.getId())).thenReturn(commentDto);

        String responseString = mvc.perform(post("/items/{itemId}/comment", this.item.getId())
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(responseString, mapper.writeValueAsString(commentDto));
        verify(service, times(1)).addComment(item.getId(), comment, user.getId());
    }

}