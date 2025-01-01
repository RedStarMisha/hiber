package ru.get.hibertest.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.get.hibertest.model.MonetaryAmount;

@Converter(autoApply = true)
public class MonetaryAmountConverter implements AttributeConverter<MonetaryAmount, String> {
    @Override
    public String convertToDatabaseColumn(MonetaryAmount monetaryAmount) {
        return monetaryAmount.toString();
    }

    @Override
    public MonetaryAmount convertToEntityAttribute(String s) {
        return MonetaryAmount.fromString(s);
    }
}
