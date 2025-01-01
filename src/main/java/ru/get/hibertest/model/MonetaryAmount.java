package ru.get.hibertest.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Currency;

@RequiredArgsConstructor
@Data
public class MonetaryAmount {
    private final BigDecimal value;
    private final Currency currency;

    @Override
    public String toString() {
        return value + " " + currency;
    }

    public static MonetaryAmount fromString(String s) {
        String[] data = s.split(" ");
        return new MonetaryAmount(new BigDecimal(data[0]), Currency.getInstance(data[1]));
    }
}
