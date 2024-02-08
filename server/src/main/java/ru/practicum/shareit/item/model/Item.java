package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.*;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Entity
@Table(name = "items", schema = "public")
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String name;
    @Column
    private String description;
    @Column(name = "is_available", nullable = false)
    private boolean isAvailable;
    @JoinColumn(name = "owner_id")
    @ManyToOne
    private User owner;
    @JoinColumn(name = "request_id")
    @ManyToOne
    private ItemRequest itemRequest;

    public Item(String name, String description, boolean available, User owner) {
        this.name = name;
        this.description = description;
        this.isAvailable = available;
        this.owner = owner;
    }

    public Item(String name, String description, boolean isAvailable, User owner, ItemRequest itemRequest) {
        this.name = name;
        this.description = description;
        this.isAvailable = isAvailable;
        this.owner = owner;
        this.itemRequest = itemRequest;
    }
}
