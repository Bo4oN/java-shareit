package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private int id;
    private String name;
    private String description;
    private Boolean available;
    List<CommentDto> commentsList;
    private Integer requestId;

    public ItemDto(int id, String name, String description, Boolean available, List<CommentDto> commentsList) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.commentsList = commentsList;
    }

    public ItemDto(int id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
