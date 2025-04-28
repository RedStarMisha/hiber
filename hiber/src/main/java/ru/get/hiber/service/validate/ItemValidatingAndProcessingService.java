package ru.get.hiber.service.validate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.get.hiber.util.ItemMapper;
import ru.get.hiber.model.Item;
import ru.get.hiber.model.dto.ItemDto;
import ru.get.hiber.service.buisnes.ItemService;

import java.util.List;

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

    public List<ItemDto> addItems(List<ItemDto> dtoList) {
        List<Item> items = dtoList.stream()
                .map(itemMapper::mapToItem)
                .toList();
        items = itemService.addItems(items);
        return items.stream()
                .map(itemMapper::mapToItemDto)
                .toList();
    }
}
