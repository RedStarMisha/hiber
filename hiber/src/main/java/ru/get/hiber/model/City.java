package ru.get.hiber.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class City {
    @Column()
    protected String zipcode;

    @Column()
    protected String name;

    @Column()
    private String country;
}
