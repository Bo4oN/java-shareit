package ru.practicum.shareit.itemTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Sql(value = {"classpath:data_test.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"classpath:schema.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ItemIntegrationTest {

    @Autowired
    private ItemRepository repository;
    Pageable pageable = PageRequest.of(0, 10);

    @Test
    public void search() {
        List<Item> list = repository.search("tit", pageable);
        assertEquals(2, list.size());

        List<Item> list1 = repository.search("script", pageable);
        assertEquals(2, list1.size());

        List<Item> list2 = repository.search("empty", pageable);
        assertEquals(0, list2.size());
    }
}
