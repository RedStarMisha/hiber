package ru.get.hiber.service.validate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.get.hiber.util.ItemMapper;
import ru.get.hiber.model.Item;
import ru.get.hiber.model.dto.ItemDto;
import ru.get.hiber.service.buisnes.ItemService;

@Service
@RequiredArgsConstructor
public class ItemValidatingAndProcessingService {
    private final ItemService itemService;
    private final ItemMapper itemMapper;
    public ItemDto addItem(ItemDto itemDto) {
        Item item = itemMapper.mapToItem(itemDto);
        item = itemService.addItem(item);
        return itemMapper.mapToItemDto(item);
    }
}
