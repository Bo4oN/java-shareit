package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    private int id;
    private String description;
    private LocalDateTime created;
    private List<ItemForOutRequest> items;

    public ItemRequestDto(String text) {
        this.description = text;
    }
}
