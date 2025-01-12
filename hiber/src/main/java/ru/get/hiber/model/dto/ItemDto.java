package ru.get.hiber.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.get.hiber.model.AuctionType;

@Data
@Builder
public class ItemDto {
    private Integer id;
    @NotNull(message = "Name should not be null")
    @NotBlank(message = "Name should not blank")
    @Size(min = 2, max = 100, message = "Name size should from 2 to 100 char")
    private String name;
    @Size(min = 2, max = 255, message = "Description size should from 2 to 255 char")
    private String description;

    private Double weight;
    @NotNull
    private AuctionType auctionType;

    @NotNull
    private MonetaryAmountDto monetaryAmount;
}
