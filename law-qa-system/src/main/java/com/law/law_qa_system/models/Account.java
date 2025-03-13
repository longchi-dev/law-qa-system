package com.law.law_qa_system.models;

import com.law.law_qa_system.enums.EnumTypes;
import com.law.law_qa_system.models.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "tbl_account")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account extends BaseEntity {
    @NotBlank(message = "Please input email")
    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @NotBlank(message = "Please input password")
    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    private EnumTypes.AccountRole role;

    @Enumerated(EnumType.STRING)
    private EnumTypes.AccountStatus status;

    @Enumerated(EnumType.STRING)
    private EnumTypes.AuthProvider authProvider;
}
