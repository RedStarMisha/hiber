package ru.get.hibertest.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Getter
    @Setter
    protected Address homeAddress;
    @Getter
    @Setter
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "billing_street")),
            @AttributeOverride(name = "city.zipcode", column = @Column(name = "billing_zipcode", nullable = false)),
            @AttributeOverride(name = "city.name", column = @Column(name = "billing_city", nullable = false)),
            @AttributeOverride(name = "city.country", column = @Column(name = "billing_country", nullable = false))
    })
    protected Address billingAddress;
}
