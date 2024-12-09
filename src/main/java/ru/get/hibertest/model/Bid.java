package ru.get.hibertest.model;


import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
@Data
public class Bid {
    private BigDecimal amount;
    private Date createdOn;
    private Item item;
}
