package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemDtoResult {
    private int id;
    private String name;
    private String description;
    private Boolean available;
    List<CommentDto> comments;
    private BookingDto lastBooking = null;
    private BookingDto nextBooking = null;

    public ItemDtoResult(int id, String name, String description, Boolean available, List<CommentDto> commentsList) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.comments = commentsList;
    }
}