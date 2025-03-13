package com.law.law_qa_system.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
@Entity
@Table(name = "tbl_vertification")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String token;

    @Column(nullable = false)
    private String email; // Lưu email tạm

    @Column(nullable = false)
    private String encodedPassword; // Lưu mật khẩu đã mã hóa

    private Instant expiryDate;

    public boolean isExpired() {
        return expiryDate.isBefore(Instant.now());
    }
}
