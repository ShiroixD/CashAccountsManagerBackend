package org.dev.cash_accounts_manager_backend.models.person;

import jakarta.persistence.*;
import lombok.Data;

/**
 * This class contains information concerning location details.<br>
 * e.g. Sunny St. 12c/45L, Nova city, Green state, 98-199, Poland, PL
 *
 * @author Fabian Frontczak
 */
@Table(schema = "internal", name = "addresses")
@Entity
@Data
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "street", nullable = false)
    private String street;

    @Column(name = "houseNumber", nullable = false)
    private String houseNumber;

    @Column(name = "apartmentNumber")
    private String apartmentNumber;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "zipCode", nullable = false)
    private String zipCode;

    @Column(name = "country", nullable = false)
    private String country;

    public Address() {}

    public Address(Integer id, String street, String houseNumber, String apartmentNumber, String city, String state,
                   String zipCode, String country) {
        this.id = id;
        this.street = street;
        this.houseNumber = houseNumber;
        this.apartmentNumber = apartmentNumber;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
    }

    public Address(String street, String houseNumber, String apartmentNumber, String city, String state,
                   String zipCode, String country) {
        this.street = street;
        this.houseNumber = houseNumber;
        this.apartmentNumber = apartmentNumber;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
    }

    public Address(Address address) {
        this.street = address.street;
        this.houseNumber = address.houseNumber;
        this.apartmentNumber = address.apartmentNumber;
        this.city = address.city;
        this.state = address.state;
        this.zipCode = address.zipCode;
        this.country = address.country;
    }
}
