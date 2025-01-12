package ru.get.hiber.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonetaryAmountDto {
    private BigDecimal value;
    private String currencyCode;
}
