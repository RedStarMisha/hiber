package ru.get.hiber.model;

import jakarta.persistence.*;
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

    @Column(name = "username", nullable = false, length = 50)
    private String username;
    @Column
    private String firstname;
    @Column
    private String lastname;

    @Column(nullable = false)
    private String email;

    @Getter
    @Setter
    protected Address homeAddress;
    @Getter
    @Setter
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "billing_street")),
            @AttributeOverride(name = "city.zipcode", column = @Column(name = "billing_zipcode")),
            @AttributeOverride(name = "city.name", column = @Column(name = "billing_city")),
            @AttributeOverride(name = "city.country", column = @Column(name = "billing_country"))
    })
    protected Address billingAddress;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
