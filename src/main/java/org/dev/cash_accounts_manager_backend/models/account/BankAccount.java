package org.dev.cash_accounts_manager_backend.models.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.dev.cash_accounts_manager_backend.enums.BankType;
import org.dev.cash_accounts_manager_backend.models.person.PersonalInfo;

import java.util.LinkedList;
import java.util.List;

/**
 * This class represents single account in any bank<br>
 * It's identified by unique name, account number and belongs to particular bank.
 * It stores personal data, current account balance and list of registered events, transactions
 *
 * @author Fabian Frontczak
 */
@Table(schema = "internal", name = "bankAccounts")
@Entity
@Data
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "accountName", nullable = false)
    private String accountName;

    @Column(name = "bankType", nullable = false)
    private BankType bankType;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "personal_info_id", referencedColumnName = "id")
    private PersonalInfo personalInfo;

    @Column(name = "accountNumber", nullable = false)
    private String accountNumber;

    @Column(name = "currentBalance", nullable = false)
    private double currentBalance;

    @Column(name = "businessId")
    private String businessCode;

    @JsonIgnore
    @OneToMany
    @JoinColumn(name = "action_record_owner_id", referencedColumnName = "id")
    private LinkedList<ActionRecord> actionRecords = new LinkedList<>();

    public BankAccount() { }

    public BankAccount(String accountName, BankType bankType, PersonalInfo personalInfo, String accountNumber,
                       double currentBalance, String businessCode, List<ActionRecord> actionRecords) {
        this.accountName = accountName;
        this.bankType = bankType;
        this.personalInfo = personalInfo;
        this.accountNumber = accountNumber;
        this.currentBalance = currentBalance;
        this.businessCode = businessCode;
        this.actionRecords.addAll(actionRecords);
    }

    public BankAccount(String accountName, BankType bankType, PersonalInfo personalInfo, String accountNumber,
                       double currentBalance, String businessCode) {
        this.accountName = accountName;
        this.bankType = bankType;
        this.personalInfo = personalInfo;
        this.accountNumber = accountNumber;
        this.currentBalance = currentBalance;
        this.businessCode = businessCode;
    }

    public BankAccount(String accountName, BankType bankType, PersonalInfo personalInfo, String accountNumber, String businessCode) {
        this.accountName = accountName;
        this.bankType = bankType;
        this.personalInfo = new PersonalInfo(personalInfo);
        this.accountNumber = accountNumber;
        this.currentBalance = 0D;
        this.businessCode = businessCode;
    }

    public BankAccount(BankAccount bankAccount) {
        this.accountName = bankAccount.accountName;
        this.bankType = bankAccount.bankType;
        this.personalInfo = bankAccount.personalInfo;
        this.accountNumber = bankAccount.accountNumber;
        this.currentBalance = bankAccount.currentBalance;
        this.businessCode = bankAccount.businessCode;
        this.actionRecords.addAll(bankAccount.actionRecords);
    }
}
