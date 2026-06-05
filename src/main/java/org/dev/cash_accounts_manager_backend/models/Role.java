package org.dev.cash_accounts_manager_backend.models;

import jakarta.persistence.*;
import lombok.Data;
import org.dev.cash_accounts_manager_backend.dtos.RoleDto;
import org.dev.cash_accounts_manager_backend.enums.RoleEnum;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

/**
 * This class contains information concerning account role details
 * like role, description, creation and update dates
 *
 * @author Fabian Frontczak
 */
@Table(schema = "internal", name = "roles")
@Entity
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "code", unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleEnum code;

    @Column(name = "description", nullable = false)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    public RoleDto toDto() {
        return new RoleDto(code, description, createdAt, updatedAt);
    }
}
