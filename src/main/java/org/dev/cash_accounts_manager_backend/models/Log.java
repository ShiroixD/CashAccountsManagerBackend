package org.dev.cash_accounts_manager_backend.models;

import jakarta.persistence.*;
import lombok.Data;
import org.dev.cash_accounts_manager_backend.dtos.LogDto;
import org.dev.cash_accounts_manager_backend.enums.ActionsEnum;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

/**
 * This class contains single log details
 * like name, user, related object, description, creation date
 *
 * @author Fabian Frontczak
 */
@Table(schema = "internal", name = "logs")
@Entity
@Data
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false)
    private ActionsEnum name;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = true)
    private User user;

    @Column(name = "objects", nullable = false)
    private String objects;

    @Column(name = "description", nullable = false)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    public Log() {}

    public Log(Integer id, ActionsEnum name, User user, String objects, String description, Date createdAt) {
        this.id = id;
        this.name = name;
        this.user = user;
        this.objects = objects;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Log(ActionsEnum name, User user, String objects, String description) {
        this.name = name;
        this.user = user;
        this.objects = objects;
        this.description = description;
    }
}
