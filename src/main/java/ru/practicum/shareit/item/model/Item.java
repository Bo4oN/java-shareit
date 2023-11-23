package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Column(name = "owner_id")
    private int ownerId;

    public Item(String name, String description, boolean available, int ownerId) {
        this.name = name;
        this.description = description;
        this.isAvailable = available;
        this.ownerId = ownerId;
    }
}
