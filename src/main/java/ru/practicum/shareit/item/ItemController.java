package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService service;

    @ResponseBody
    @PostMapping
    public ItemDto addItem(@RequestBody @Valid ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") int ownerId) {
        return service.addItem(itemDto, ownerId);
    }

    @ResponseBody
    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable int itemId,
                              @RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") int ownerId) {
        return service.updateItem(itemId, itemDto, ownerId);
    }

    @ResponseBody
    @GetMapping("/{itemId}")
    public ItemDtoOut getItem(@PathVariable int itemId,
                              @RequestHeader("X-Sharer-User-Id") int ownerId) {
        return service.getItem(itemId, ownerId);
    }

    @ResponseBody
    @GetMapping
    public List<ItemDtoWithBooking> getUserItems(@RequestHeader("X-Sharer-User-Id") int ownerId) {
        return service.getItemsListByOwner(ownerId);
    }

    @ResponseBody
    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return service.searchItems(text);
    }

    @ResponseBody
    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable int itemId,
                                 @RequestBody @Valid Comment comment,
                                 @RequestHeader("X-Sharer-User-Id") int authorId) {
        return service.addComment(itemId, comment, authorId);
    }
}
