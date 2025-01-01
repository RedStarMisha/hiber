package ru.get.hibertest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.get.hibertest.model.Item;
import ru.get.hibertest.model.dto.ItemDto;
import ru.get.hibertest.service.ItemService;

@Service
@RequiredArgsConstructor
public class PrepareAndProcessItemService {
    private final ItemService itemService;
    public ItemDto addItem(ItemDto itemDto) {
        Item item = ModelMapper.mapToEntity(itemDto);
        item = itemService.addItem(item);
        return ModelMapper.mapToDto(item);
    }
}
