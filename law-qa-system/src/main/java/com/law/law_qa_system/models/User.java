package com.law.law_qa_system.models;

import com.law.law_qa_system.models.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.enabled;

@Entity
@Table(name = "tbl_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private Account account;

    @NotNull
    private String name;

    @Pattern(regexp = "^(http|https)://.*$", message = "Avatar must be a valid URL")
    private String avatar;

    @Pattern(regexp = "\\d{10,15}", message = "Phone number must be between 10 and 15 digits")
    private String phoneNum;
    private int tokens = 0;
    private Double balance;
    private LocalDate dateOfBirth;

    private Integer gender;

    private String city;
    private String district;
    private String ward;
    private boolean enabled = false;

}
