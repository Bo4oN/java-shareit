package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemForOutRequest {
    private int id;
    private String name;
    private String description;
    private int requestId;
    private boolean available;
}
