package org.dev.cash_accounts_manager_backend.models.account;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * This class represents registered single action.<br>
 * It stores external bank code {@link org.dev.cash_accounts_manager_backend.enums}
 * and number with additional address info and has data concerning record details like label, description,
 * transaction amount, registration data and time
 *
 * @author Fabian Frontczak
 */
@Table(schema = "internal", name = "action_records")
@Entity
@Data
public class ActionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "externalBankCode", nullable = false)
    private int externalBankCode;

    @Column(name = "externalBankNumber", nullable = false)
    private String externalBankNumber;

    @Column(name = "additionalAddressInfo")
    private String additionalAddressInfo;

    @Column(name = "label", nullable = false)
    private String label;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "fundsAmount", nullable = false)
    private double fundsAmount;

    @Column(name = "registrationDateTime", nullable = false)
    private LocalDateTime registrationDateTime;

    public ActionRecord () { }

    public ActionRecord(Integer id, int externalBankCode, String externalBankNumber, String additionalAddressInfo,
                        String label, String description, double fundsAmount, LocalDateTime registrationDateTime) {
        this.id = id;
        this.externalBankCode = externalBankCode;
        this.externalBankNumber = externalBankNumber;
        this.additionalAddressInfo = additionalAddressInfo;
        this.label = label;
        this.description = description;
        this.fundsAmount = fundsAmount;
        this.registrationDateTime = registrationDateTime;
    }

    public ActionRecord(ActionRecord actionRecord) {
        this.externalBankCode = actionRecord.externalBankCode;
        this.externalBankNumber = actionRecord.externalBankNumber;
        this.additionalAddressInfo = actionRecord.additionalAddressInfo;
        this.label = actionRecord.label;
        this.description = actionRecord.description;
        this.fundsAmount = actionRecord.fundsAmount;
        this.registrationDateTime = actionRecord.registrationDateTime;
    }
}
