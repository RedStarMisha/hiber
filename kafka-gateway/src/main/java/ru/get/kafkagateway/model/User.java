package ru.get.kafkagateway.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class User {
    @JsonProperty("phone_number")
    private Long phoneNumber;
    @JsonProperty("balance")
    private Double balance;
}
