package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemStorage {
    private final HashMap<Integer, Item> items;
    private int nextId = 1;

    public Item addItem(Item item) {
        item.setId(nextId++);
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    public void updateItem(Item item) {
        if (!items.containsKey(item.getId())) {
            throw new NotFoundException("Элемент не найден");
        }
        items.put(item.getId(), item);
    }

    public Item getItem(int id) {
        if (!items.containsKey(id)) {
            throw new NotFoundException("Элемент не найден");
        }
        return items.get(id);
    }

    public List<Item> getItemsListByOwner(int ownerId) {
        List<Item> itemList = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner() == ownerId) itemList.add(item);
        }
        if (itemList.isEmpty()) {
            throw new NotFoundException("Не найдено ни одного элемента");
        }
        return itemList;
    }

    public List<Item> searchItems(String text) {
        List<Item> itemList = new ArrayList<>();
        for (Item item : items.values()) {
            if ((item.getName().toLowerCase().contains(text.toLowerCase()) || (item.getDescription().toLowerCase()).contains(text.toLowerCase())) && item.isAvailable()) {
                itemList.add(item);
            }
        }
        if (itemList.isEmpty()) {
            throw new NotFoundException("Не найдено ни одного элемента");
        }
        return itemList;
    }
}
