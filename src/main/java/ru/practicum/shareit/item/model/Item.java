package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

/**
 * TODO Sprint add-controllers.
 */

@Data
public class Item {
    private int id;
    private String name;
    private String description;
    private boolean available;

    public Item(String name, String description, boolean available, int owner) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
    }

    private int owner;
    private ItemRequest request;
}
