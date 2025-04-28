package ru.get.hiber.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.get.hiber.service.validate.ItemValidatingAndProcessingService;
import ru.get.hiber.model.dto.ItemDto;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping(path = "items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemValidatingAndProcessingService itemValidatingAndProcessingService;

    @PostMapping()
    public ItemDto addItem(@RequestBody ItemDto itemDto) {
        return itemValidatingAndProcessingService.addItem(itemDto);
    }
    @PostMapping("batch")
    public List<ItemDto> addItems(@RequestBody List<ItemDto> itemDtoList) {
        return itemValidatingAndProcessingService.addItems(itemDtoList);
    }

}
