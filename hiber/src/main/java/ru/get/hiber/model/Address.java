package ru.get.hiber.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Embeddable
public class Address {
    @Column
    protected String street;

    @AttributeOverrides(
            @AttributeOverride(name = "name", column = @Column(name = "city"))
    )
    private City city;


}
