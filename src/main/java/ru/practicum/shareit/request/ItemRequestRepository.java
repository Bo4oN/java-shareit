package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    List<ItemRequest> findByRequestorIdOrderByCreatedDesc(int requestorId);

    List<ItemRequest> findAllByRequestorIdIsNotOrderByCreated(int userId, Pageable pageable);
}
