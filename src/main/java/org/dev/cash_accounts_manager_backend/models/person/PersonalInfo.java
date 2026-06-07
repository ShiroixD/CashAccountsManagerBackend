package org.dev.cash_accounts_manager_backend.models.person;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * This class contains information concerning basic personal information.<br>
 * e.g. John Smith, john.smith@gmail.com, +48606321450, address (Sunny St. 12c/45L, Nova city, Green state, 98-199, New Ground, NG), personal code (92032128236)
 *
 * @author Fabian Frontczak
 */
@Table(schema = "internal", name = "personalInfo")
@Entity
@Data
public class PersonalInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "firstName", nullable = false)
    private String firstName;

    @Column(name = "lastName", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phoneNumber", nullable = false)
    private String phoneNumber;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "address_owner_id", referencedColumnName = "id")
    private Address address;

    @Column(name = "personalCode", nullable = false)
    private String personalCode;

    public PersonalInfo() { }

    public PersonalInfo(String firstName, String lastName, String email, String phoneNumber, Address address, String personalCode) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.personalCode = personalCode;
    }

    public PersonalInfo(PersonalInfo personalInfo) {
        this.firstName = personalInfo.firstName;
        this.lastName = personalInfo.lastName;
        this.email = personalInfo.email;
        this.phoneNumber = personalInfo.phoneNumber;
        this.personalCode = personalInfo.personalCode;
        this.address = personalInfo.address;
    }
}
