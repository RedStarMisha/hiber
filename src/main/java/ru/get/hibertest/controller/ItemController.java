package ru.get.hibertest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.get.hibertest.PrepareAndProcessItemService;
import ru.get.hibertest.model.dto.ItemDto;

@RestController
@RequestMapping(name = "/item")
@RequiredArgsConstructor
public class ItemController {

    private final PrepareAndProcessItemService transformationService;

    public ItemDto addItem(@RequestBody ItemDto itemDto) {
        return transformationService.addItem(itemDto);
    }

}
