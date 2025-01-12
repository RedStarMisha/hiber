package ru.get.hiber.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.get.hiber.model.Item;
import ru.get.hiber.model.MonetaryAmount;
import ru.get.hiber.model.dto.ItemDto;
import ru.get.hiber.model.dto.MonetaryAmountDto;

import java.util.Currency;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ItemMapper {
    @Mapping(target = "metricWeight", source = "itemDto.weight")
    public abstract Item mapToItem(ItemDto itemDto);
    @Mapping(target = "weight", source = "item.metricWeight")
    public abstract ItemDto mapToItemDto(Item item);

    public MonetaryAmount mapToMonetaryAmount(MonetaryAmountDto monetaryAmountDto) {
        return new MonetaryAmount(monetaryAmountDto.getValue(), Currency.getInstance(monetaryAmountDto.getCurrencyCode()));
    }
    public MonetaryAmountDto mapToMonetaryAmountDto(MonetaryAmount monetaryAmount) {
        return new MonetaryAmountDto(monetaryAmount.getValue(), monetaryAmount.getCurrency().getCurrencyCode());
    }
}
