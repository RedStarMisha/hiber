package ru.get.hiber.service.buisnes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.get.hiber.model.Item;
import ru.get.hiber.repository.ItemRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {
    private final ItemRepository itemRepository;
    public Item addItem(Item item) {
        Item savedItem = itemRepository.save(item);
        log.info("Add new item {}", savedItem);
        return savedItem;
    }
}
