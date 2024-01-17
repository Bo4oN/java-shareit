package ru.practicum.shareit.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

@Data
@Entity
@Table(name = "requests", schema = "public")
@NoArgsConstructor
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "description")
    private String text;
    @JoinColumn(name = "requestor_id")
    @ManyToOne
    private User requestor;
    @Column(name = "created_date")
    private LocalDateTime created;

    public ItemRequest(String text, User requestor, LocalDateTime created) {
        this.text = text;
        this.requestor = requestor;
        this.created = created;
    }
}
