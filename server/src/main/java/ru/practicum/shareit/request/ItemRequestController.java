package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService service;

    @PostMapping
    @ResponseBody
    public ItemRequestDto addRequest(@RequestBody ItemRequestDto requestDto,
                                     @RequestHeader("X-Sharer-User-Id") int userId) {
        return service.addRequest(requestDto, userId);
    }

    @GetMapping
    @ResponseBody
    public List<ItemRequestDto> getYourRequests(@RequestHeader("X-Sharer-User-Id") int userId) {
        return service.getUserResponses(userId);
    }

    @GetMapping("/all")
    @ResponseBody
    public List<ItemRequestDto> getOtherRequests(@RequestParam(defaultValue = "0") int from,
                                                 @RequestParam(defaultValue = "10") int size,
                                                 @RequestHeader("X-Sharer-User-Id") int userId) {
        return service.getAllResponses(from, size, userId);
    }

    @GetMapping("/{requestId}")
    @ResponseBody
    public ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") int userId,
                                         @PathVariable int requestId) {
        return service.getResponseById(requestId, userId);
    }
}