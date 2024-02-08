package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/request")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addRequestItem(@RequestBody @Valid ItemRequestDto requestDto,
                                                 @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Create new requestItem, content - {}", requestDto.getContent());
        return requestClient.addRequestItem(requestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getYourRequests(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Get all your requests, userId={}", userId);
        return requestClient.getYourRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherRequest(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                  @RequestParam(defaultValue = "10") @Positive int size,
                                                  @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Get all requests, userId={}, from={}, size={}", userId, from, size);
        return requestClient.getOtherRequest(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable int requestId,
                                                 @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Get request by requestId={}, userId={}", requestId, userId);
        return requestClient.getRequestById(requestId, userId);
    }
}
