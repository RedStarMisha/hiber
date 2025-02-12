package ru.get.hiber.model;


import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
@Data
public class Bid {
    private Integer id;
    private BigDecimal amount;
    private Date createdOn;
    private Item item;
}
