package ru.get.hibertest.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "username")
    @NotNull
    @NotBlank
    private String username;
    @Column
    @NotNull
    @NotBlank
    private String firstname;
    @Column
    @NotNull
    @NotBlank
    private String lastname;

    @Column
    private String email;

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
