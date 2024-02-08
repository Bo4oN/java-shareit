package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") int userId,
                                          @RequestBody @Valid ItemDto itemDto) {
        log.info("Add item - {}, userId={}", itemDto, userId);
        return itemClient.addItem(itemDto, userId);
    }

    @PatchMapping("/itemId")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") int userId,
                                             @RequestBody @Valid ItemDto itemDto,
                                             @PathVariable int itemId) {
        log.info("Update item, itemId={}, userId={}, content - {}", itemId, userId, itemDto);
        return itemClient.updateItem(itemDto, userId, itemId);
    }

    @GetMapping("/itemId")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") int userId,
                                              @PathVariable int itemId) {
        log.info("Get item by id={}, userId={}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader("X-Sharer-User-Id") int userId,
                                               @RequestParam(
                                                       value = "from", defaultValue = "0") @PositiveOrZero int from,
                                               @RequestParam(
                                                       value = "size", defaultValue = "10") @Positive int size) {
        log.info("Get all user Items, userId={}, from={}, size={}", userId, from, size);
        return itemClient.getUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                                              @RequestHeader("X-Sharer-User-Id") int ownerId,
                                              @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int from,
                                              @RequestParam(value = "size", defaultValue = "10") @Positive int size) {
        log.info("Search items. Text - {}, userId={}, from={}, size={}", text, ownerId, from, size);
        return itemClient.searchItem(ownerId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable int itemId,
                                             @RequestBody @Valid CommentDto commentDto,
                                             @RequestHeader("X-Sharer-User-Id") int authorId) {
        log.info("Add new comment - {}, itemId={}, userId={}", commentDto, itemId, authorId);
        return itemClient.addComment(commentDto, itemId, authorId);
    }
}
